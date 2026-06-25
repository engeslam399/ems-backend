package com.example.ems.controller;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ems.exception.GlobalExceptionHandler;
import com.example.ems.dto.DepartmentDto;
import com.example.ems.model.Department;
import com.example.ems.service.DepartmentService;
import com.example.ems.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmployeeRestControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private DepartmentController departmentController;

    @Autowired
    private EmployeeController employeeController;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EmployeeService employeeService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    private Department salesDept;

    @BeforeEach
    void setUp() {
        // Build standalone MockMvc instance with controllers and advice registered
        mockMvc = MockMvcBuilders.standaloneSetup(departmentController, employeeController)
                .setControllerAdvice(globalExceptionHandler)
                .build();

        // Clean database
        employeeService.findAll().forEach(e -> employeeService.deleteById(e.getId()));
        departmentService.findAll().forEach(d -> departmentService.deleteById(d.getId()));

        salesDept = Department.builder()
                .code("SAL")
                .name("Sales Department")
                .description("Sales and Marketing")
                .build();
        salesDept = departmentService.save(salesDept);
    }

    @Test
    void testGetDepartments() throws Exception {
        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code", is("SAL")));
    }

    @Test
    void testCreateDepartmentSuccess() throws Exception {
        DepartmentDto newDept = DepartmentDto.builder()
                .code("MARK")
                .name("Marketing")
                .description("Digital Marketing")
                .build();

        mockMvc.perform(post("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDept)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is("MARK")))
                .andExpect(jsonPath("$.name", is("Marketing")));
    }

    @Test
    void testCreateDepartmentValidationFailure() throws Exception {
        // Missing code and name
        DepartmentDto invalidDept = DepartmentDto.builder()
                .description("No details")
                .build();

        mockMvc.perform(post("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDept)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasKey("code")))
                .andExpect(jsonPath("$.errors", hasKey("name")));
    }

    @Test
    void testCreateDepartmentDuplicateFailure() throws Exception {
        // Create duplicate with "SAL"
        DepartmentDto duplicateDept = DepartmentDto.builder()
                .code("SAL")
                .name("Sales clone")
                .build();

        mockMvc.perform(post("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateDept)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code", is("Department code already exists")));
    }

    @Test
    void testCreateEmployeeMultipartSuccess() throws Exception {
        mockMvc.perform(multipart("/api/employees")
                .param("code", "EMP100")
                .param("name", "John Miller")
                .param("dateOfBirth", "1995-10-25")
                .param("address", "Avenue Street")
                .param("mobile", "55512345")
                .param("salary", "4500.00")
                .param("departmentId", salesDept.getId().toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is("EMP100")))
                .andExpect(jsonPath("$.name", is("John Miller")))
                .andExpect(jsonPath("$.department.code", is("SAL")));
    }

    @Test
    void testCreateEmployeeValidationFailure() throws Exception {
        // Missing required fields
        mockMvc.perform(multipart("/api/employees")
                .param("address", "Avenue Street"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasKey("code")))
                .andExpect(jsonPath("$.errors", hasKey("name")))
                .andExpect(jsonPath("$.errors", hasKey("salary")))
                .andExpect(jsonPath("$.errors", hasKey("departmentId")));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        // Create an employee first
        mockMvc.perform(multipart("/api/employees")
                .param("code", "EMP200")
                .param("name", "Jane Miller")
                .param("salary", "3000.00")
                .param("departmentId", salesDept.getId().toString()))
                .andExpect(status().isCreated());

        // Find the created employee in repository
        Long id = employeeService.findAll().stream()
                .filter(e -> e.getCode().equals("EMP200"))
                .findFirst()
                .orElseThrow()
                .getId();

        // Delete employee
        mockMvc.perform(delete("/api/employees/" + id))
                .andExpect(status().isNoContent());

        // Get single should return 404
        mockMvc.perform(get("/api/employees/" + id))
                .andExpect(status().isNotFound());
    }
}
