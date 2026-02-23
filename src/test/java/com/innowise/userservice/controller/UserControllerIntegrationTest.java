package com.innowise.userservice.controller;

import static com.innowise.userservice.constants.CommonConstants.USERS_URL;
import static com.innowise.userservice.constants.UserTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.dto.request.UserRequest;
import com.innowise.userservice.dto.response.UserResponse;
import com.innowise.userservice.service.UserService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
@Transactional
@WithMockUser(roles = "ADMIN")
public class UserControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17.5")
            .withReuse(true);

    @Container
    @ServiceConnection
    static GenericContainer<?> redisContainer =
            new GenericContainer<>("redis:7.4").withExposedPorts(6379).withReuse(true);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @BeforeAll
    static void initSchema(@Autowired JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS user_entity (
                id VARCHAR(36) PRIMARY KEY,
                email VARCHAR(255),
                email_constraint VARCHAR(255),
                email_verified BOOLEAN,
                enabled BOOLEAN,
                federation_link VARCHAR(255),
                first_name VARCHAR(255),
                last_name VARCHAR(255),
                realm_id VARCHAR(255),
                username VARCHAR(255),
                created_timestamp BIGINT,
                service_account_client_link VARCHAR(255),
                not_before INTEGER
            );
        """);
    }

    private UserResponse createMockResponse(String id, String firstName, String email) throws Exception {
        String json = String.format("{\"id\":\"%s\", \"firstName\":\"%s\", \"email\":\"%s\"}", id, firstName, email);
        return objectMapper.readValue(json, UserResponse.class);
    }

    @Test
    void createUser_ThenGetById_ShouldReturnSameUser() throws Exception {
        String mockId = UUID.randomUUID().toString();
        UserResponse mockCreatedUser = createMockResponse(mockId, NAME, EMAIL);

        when(userService.createUser(any(UserRequest.class))).thenReturn(mockCreatedUser);
        when(userService.getUserById(mockId)).thenReturn(mockCreatedUser);

        String requestJson = objectMapper.writeValueAsString(VALID_USER_REQUEST);

        String responseJson = mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserResponse created = objectMapper.readValue(responseJson, UserResponse.class);

        assertThat(created.getFirstName()).isEqualTo(NAME);
        assertThat(created.getEmail()).isEqualTo(EMAIL);

        String fetchedJson = mockMvc.perform(get(USERS_URL + "/" + created.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserResponse fetched = objectMapper.readValue(fetchedJson, UserResponse.class);

        assertThat(fetched.getId()).isEqualTo(created.getId());
        assertThat(fetched.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void updateUser_ShouldPersistChanges() throws Exception {
        String mockId = UUID.randomUUID().toString();
        UserResponse mockCreatedUser = createMockResponse(mockId, NAME, EMAIL);
        UserResponse mockUpdatedUser = createMockResponse(mockId, UPDATED_NAME, UPDATED_EMAIL);

        when(userService.createUser(any(UserRequest.class))).thenReturn(mockCreatedUser);
        when(userService.updateUserById(eq(mockId), any(UserRequest.class))).thenReturn(mockUpdatedUser);

        String createJson = objectMapper.writeValueAsString(VALID_USER_REQUEST);
        String responseJson = mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserResponse created = objectMapper.readValue(responseJson, UserResponse.class);

        String updateJson = objectMapper.writeValueAsString(UPDATED_USER_REQUEST);
        String updatedJson = mockMvc.perform(put(USERS_URL + "/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserResponse updated = objectMapper.readValue(updatedJson, UserResponse.class);

        assertThat(updated.getFirstName()).isEqualTo(UPDATED_NAME);
        assertThat(updated.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    void deleteUser_ShouldRemoveUser() throws Exception {
        String mockId = UUID.randomUUID().toString();
        UserResponse mockCreatedUser = createMockResponse(mockId, NAME, EMAIL);

        when(userService.createUser(any(UserRequest.class))).thenReturn(mockCreatedUser);
        doNothing().when(userService).deleteUserById(mockId);
        when(userService.getUserById(mockId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        String createJson = objectMapper.writeValueAsString(VALID_USER_REQUEST);
        String responseJson = mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserResponse created = objectMapper.readValue(responseJson, UserResponse.class);

        mockMvc.perform(delete(USERS_URL + "/" + created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(USERS_URL + "/" + created.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUsersByIds_ShouldReturnRequestedUsers() throws Exception {
        String mockId1 = UUID.randomUUID().toString();
        String mockId2 = UUID.randomUUID().toString();

        UserResponse mockUser1 = createMockResponse(mockId1, NAME, EMAIL);
        UserResponse mockUser2 = createMockResponse(mockId2, UPDATED_NAME, UPDATED_EMAIL);

        when(userService.createUser(any(UserRequest.class)))
                .thenReturn(mockUser1)
                .thenReturn(mockUser2);

        when(userService.getUsersByIds(any(List.class))).thenReturn(List.of(mockUser1, mockUser2));

        UserResponse user1 = objectMapper.readValue(
                mockMvc.perform(post(USERS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(VALID_USER_REQUEST)))
                        .andReturn().getResponse().getContentAsString(),
                UserResponse.class
        );

        UserResponse user2 = objectMapper.readValue(
                mockMvc.perform(post(USERS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(UPDATED_USER_REQUEST)))
                        .andReturn().getResponse().getContentAsString(),
                UserResponse.class
        );

        List<String> ids = List.of(user1.getId(), user2.getId());
        String idsParam = String.join(",", ids);

        String batchJson = mockMvc.perform(get(USERS_URL + "/batch")
                        .param("ids", idsParam))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<UserResponse> batch = objectMapper.readValue(
                batchJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, UserResponse.class)
        );

        assertThat(batch).hasSize(2);
    }
}