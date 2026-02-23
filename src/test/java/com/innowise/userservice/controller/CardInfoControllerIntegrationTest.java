package com.innowise.userservice.controller;

import static com.innowise.userservice.constants.CommonConstants.CARD_INFOS_URL;
import static com.innowise.userservice.constants.CommonConstants.USERS_URL;
import static com.innowise.userservice.constants.UserTestConstants.BIRTH_DATE;
import static com.innowise.userservice.constants.UserTestConstants.EMAIL;
import static com.innowise.userservice.constants.UserTestConstants.SURNAME;
import static com.innowise.userservice.constants.UserTestConstants.NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.constants.CardInfoTestConstants;
import com.innowise.userservice.dto.request.CardInfoRequest;
import com.innowise.userservice.dto.request.UserRequest;
import com.innowise.userservice.dto.response.CardInfoResponse;
import com.innowise.userservice.dto.response.UserResponse;
import com.innowise.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
@Transactional
@WithMockUser(roles = "ADMIN")
public class CardInfoControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17.5").withReuse(true);

    @Container
    @ServiceConnection
    static GenericContainer<?> redisContainer =
            new GenericContainer<>("redis:7.4").withExposedPorts(6379).withReuse(true);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private UserService userService;

    private UserResponse createTestUser() throws Exception {
        String mockUserId = UUID.randomUUID().toString();

        // 1. Ручками сохраняем юзера в тестовую БД для обхода Foreign Key
        // Добавил email на всякий случай, если в таблице есть ограничение NOT NULL
        jdbcTemplate.update("INSERT INTO users (id, email) VALUES (?, ?)", UUID.fromString(mockUserId), EMAIL);

        // 2. Настраиваем мок-ответ для контроллера
        String mockUserJson = String.format("{\"id\":\"%s\", \"firstName\":\"%s\", \"email\":\"%s\"}", mockUserId, NAME, EMAIL);
        UserResponse mockResponse = objectMapper.readValue(mockUserJson, UserResponse.class);
        when(userService.createUser(any(UserRequest.class))).thenReturn(mockResponse);

        UserRequest userRequest = new UserRequest(
                NAME,
                SURNAME,
                BIRTH_DATE,
                EMAIL
        );

        String userJson = objectMapper.writeValueAsString(userRequest);

        // 3. Дергаем контроллер
        String responseJson = mockMvc.perform(post(USERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(responseJson, UserResponse.class);
    }

    private CardInfoRequest buildCardRequest(UUID userId, CardInfoRequest base) {
        return new CardInfoRequest(
                base.number(),
                base.holder(),
                base.expirationDate(),
                userId
        );
    }

    @Test
    void createCardInfo_ThenGetById_ShouldReturnSameCard() throws Exception {
        UserResponse user = createTestUser();

        CardInfoRequest cardRequest = buildCardRequest(UUID.fromString(user.getId()), CardInfoTestConstants.VALID_CARD_INFO_REQUEST);

        String responseJson = mockMvc.perform(post(CARD_INFOS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CardInfoResponse created = objectMapper.readValue(responseJson, CardInfoResponse.class);

        assertThat(created.getNumber()).isEqualTo(CardInfoTestConstants.CARD_NUMBER);

        String fetchedJson = mockMvc.perform(get(CARD_INFOS_URL + "/" + created.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CardInfoResponse fetched = objectMapper.readValue(fetchedJson, CardInfoResponse.class);

        assertThat(fetched.getId()).isEqualTo(created.getId());
        assertThat(fetched.getNumber()).isEqualTo(CardInfoTestConstants.CARD_NUMBER);
    }

    @Test
    void updateCardInfo_ShouldPersistChanges() throws Exception {
        UserResponse user = createTestUser();

        CardInfoRequest cardRequest = buildCardRequest(UUID.fromString(user.getId()), CardInfoTestConstants.VALID_CARD_INFO_REQUEST);

        CardInfoResponse created = objectMapper.readValue(
                mockMvc.perform(post(CARD_INFOS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cardRequest)))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString(),
                CardInfoResponse.class
        );

        CardInfoRequest updatedRequest = buildCardRequest(UUID.fromString(user.getId()), CardInfoTestConstants.UPDATED_CARD_INFO_REQUEST);

        CardInfoResponse updated = objectMapper.readValue(
                mockMvc.perform(put(CARD_INFOS_URL + "/" + created.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedRequest)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(),
                CardInfoResponse.class
        );

        assertThat(updated.getNumber()).isEqualTo(CardInfoTestConstants.UPDATED_CARD_NUMBER);
    }

    @Test
    void deleteCardInfo_ShouldRemoveCard() throws Exception {
        UserResponse user = createTestUser();

        CardInfoRequest cardRequest = buildCardRequest(UUID.fromString(user.getId()), CardInfoTestConstants.VALID_CARD_INFO_REQUEST);

        CardInfoResponse created = objectMapper.readValue(
                mockMvc.perform(post(CARD_INFOS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cardRequest)))
                        .andReturn().getResponse().getContentAsString(),
                CardInfoResponse.class
        );

        mockMvc.perform(delete(CARD_INFOS_URL + "/" + created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(CARD_INFOS_URL + "/" + created.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCardInfosByIds_ShouldReturnRequestedCards() throws Exception {
        UserResponse user = createTestUser();

        CardInfoResponse card1 = objectMapper.readValue(
                mockMvc.perform(post(CARD_INFOS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(buildCardRequest(UUID.fromString(user.getId()), CardInfoTestConstants.VALID_CARD_INFO_REQUEST))))
                        .andReturn().getResponse().getContentAsString(),
                CardInfoResponse.class
        );

        CardInfoResponse card2 = objectMapper.readValue(
                mockMvc.perform(post(CARD_INFOS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(buildCardRequest(UUID.fromString(user.getId()), CardInfoTestConstants.UPDATED_CARD_INFO_REQUEST))))
                        .andReturn().getResponse().getContentAsString(),
                CardInfoResponse.class
        );

        List<UUID> ids = List.of(card1.getId(), card2.getId());
        String idsParam = ids.stream().map(UUID::toString).collect(Collectors.joining(","));

        String batchJson = mockMvc.perform(get(CARD_INFOS_URL + "/batch")
                        .param("ids", idsParam))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<CardInfoResponse> batch = objectMapper.readValue(
                batchJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, CardInfoResponse.class)
        );

        assertThat(batch).hasSize(2);
    }
}