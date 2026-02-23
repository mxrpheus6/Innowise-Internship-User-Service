package com.innowise.userservice.controller;

import static com.innowise.userservice.constants.CommonConstants.USERS_URL;
import static com.innowise.userservice.constants.UserTestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.dto.response.UserResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(profiles = "test")
@Transactional
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

    @Test
    void createUser_ThenGetById_ShouldReturnSameUser() throws Exception {
        String requestJson = objectMapper.writeValueAsString(VALID_USER_REQUEST);

        String responseJson = mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserResponse created = objectMapper.readValue(responseJson, UserResponse.class);

        // Используем getName(), так как в DTO скорее всего осталось поле name
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
        // 1. Create
        String createJson = objectMapper.writeValueAsString(VALID_USER_REQUEST);
        String responseJson = mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserResponse created = objectMapper.readValue(responseJson, UserResponse.class);

        // 2. Update
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
        // Создаем двух юзеров
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

        // ТЕПЕРЬ ID - ЭТО СТРОКИ
        List<String> ids = List.of(user1.getId(), user2.getId());

        // Просто джойним строки через запятую
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