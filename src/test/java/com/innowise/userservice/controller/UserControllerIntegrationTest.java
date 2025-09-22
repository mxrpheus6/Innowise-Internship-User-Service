package com.innowise.userservice.controller;

import static com.innowise.userservice.constants.UserTestConstants.NAME;
import static com.innowise.userservice.constants.UserTestConstants.EMAIL;
import static com.innowise.userservice.constants.UserTestConstants.UPDATED_EMAIL;
import static com.innowise.userservice.constants.UserTestConstants.UPDATED_NAME;
import static com.innowise.userservice.constants.UserTestConstants.UPDATED_USER_REQUEST;
import static com.innowise.userservice.constants.UserTestConstants.VALID_USER_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.dto.response.UserResponse;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17.5");

    @Container
    @ServiceConnection
    static GenericContainer<?> redisContainer =
            new GenericContainer<>("redis:7.4").withExposedPorts(6379);

    @DynamicPropertySource
    static void postgreSqlProperties(DynamicPropertyRegistry registry) {
        registry.add("postgresql.driver", postgreSQLContainer::getDriverClassName);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/v1/users";

    @Test
    void createUser_ThenGetById_ShouldReturnSameUser() throws Exception {
        String requestJson = objectMapper.writeValueAsString(VALID_USER_REQUEST);

        String responseJson = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserResponse created = objectMapper.readValue(responseJson, UserResponse.class);

        assertThat(created.getName()).isEqualTo(NAME);
        assertThat(created.getEmail()).isEqualTo(EMAIL);

        String fetchedJson = mockMvc.perform(get(BASE_URL + "/" + created.getId()))
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
        String createJson = objectMapper.writeValueAsString(VALID_USER_REQUEST);
        String responseJson = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserResponse created = objectMapper.readValue(responseJson, UserResponse.class);

        String updateJson = objectMapper.writeValueAsString(UPDATED_USER_REQUEST);
        String updatedJson = mockMvc.perform(put(BASE_URL + "/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserResponse updated = objectMapper.readValue(updatedJson, UserResponse.class);

        assertThat(updated.getName()).isEqualTo(UPDATED_NAME);
        assertThat(updated.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    void deleteUser_ShouldRemoveUser() throws Exception {
        String createJson = objectMapper.writeValueAsString(VALID_USER_REQUEST);
        String responseJson = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserResponse created = objectMapper.readValue(responseJson, UserResponse.class);

        mockMvc.perform(delete(BASE_URL + "/" + created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(BASE_URL + "/" + created.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUsersByIds_ShouldReturnRequestedUsers() throws Exception {
        UserResponse user1 = objectMapper.readValue(
                mockMvc.perform(post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(VALID_USER_REQUEST)))
                        .andReturn().getResponse().getContentAsString(),
                UserResponse.class
        );

        UserResponse user2 = objectMapper.readValue(
                mockMvc.perform(post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(UPDATED_USER_REQUEST)))
                        .andReturn().getResponse().getContentAsString(),
                UserResponse.class
        );

        List<UUID> ids = List.of(user1.getId(), user2.getId());
        String idsParam = ids.stream().map(UUID::toString).collect(Collectors.joining(","));

        String batchJson = mockMvc.perform(get(BASE_URL + "/batch")
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
