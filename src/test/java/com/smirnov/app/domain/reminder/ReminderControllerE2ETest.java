package com.smirnov.app.domain.reminder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smirnov.app.domain.reminder.dto.CreateReminderRequestDto;
import com.smirnov.app.domain.reminder.dto.CreateReminderResponseDto;
import com.smirnov.app.domain.reminder.dto.DeleteReminderRequestDto;
import com.smirnov.app.domain.user.User;
import com.smirnov.app.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ReminderController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReminderControllerE2ETest {

    @Autowired
    MockMvc mockMvc;

    private static final ObjectMapper jsonMapper = new ObjectMapper() {{
        findAndRegisterModules();
    }};

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ReminderRepository reminderRepository;

    @MockBean
    private ReminderService reminderService;


    private final OAuth2AccessToken mockAccessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
            "token_value",
            Instant.now(),
            Instant.now());

    private final User mockUser = new User(46L, "smirnov@gmail.com", 140L);

    private final Pageable mockPageableDefault = PageRequest.of(0, 10, Sort.unsorted());

    private final Page<Reminder> mockPageWithoutContent = new PageImpl<>((new ArrayList<>()), mockPageableDefault, 0);

    private final Reminder mockReminder = new Reminder(15L, "", "", LocalDateTime.MIN, mockUser);

    @BeforeEach
    void initCall() {
        Mockito.when(userRepository.findByEmail(mockUser.getEmail()))
                .thenReturn(Optional.of(mockUser));
    }

    @Test
    void createDeleteScenario() throws Exception {

        Mockito.when(reminderRepository.findByOwner(mockUser, mockPageableDefault))
                .thenReturn(mockPageWithoutContent);

        mockMvc.perform(MockMvcRequestBuilders.get("/list")
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Client("Reminder-app")
                                .accessToken(mockAccessToken))
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Login()
                                .attributes((oAuth2AccessToken) -> oAuth2AccessToken.put("email", "smirnov@gmail.com")))
                )
                .andExpect(MockMvcResultMatchers.content().string("{" +
                                "\"content\":" +
                                "[" + "]," +
                                "\"currentPage\":0," +
                                "\"pageSize\":10," +
                                "\"totalPages\":0," +
                                "\"totalElements\":0" +
                                "}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        CreateReminderRequestDto requestBody = new CreateReminderRequestDto("testTitle",
                "testDescription",
                LocalDateTime.of(2020, 1, 1, 12, 0));

        Mockito.when(reminderService.createReminder(requestBody, mockUser))
                .thenReturn(mockReminder);

        mockMvc.perform(MockMvcRequestBuilders.post("/reminder/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(requestBody))
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Client("Reminder-app")
                                .accessToken(mockAccessToken))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Login()
                                .attributes((oAuth2AccessToken) -> oAuth2AccessToken.put("email", "smirnov@gmail.com")))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content()
                        .string(jsonMapper.writeValueAsString(new CreateReminderResponseDto(15L))));


        Mockito.when(reminderRepository.findByOwner(mockUser, mockPageableDefault))
                .thenReturn(new PageImpl<>(List.of(mockReminder), mockPageableDefault, 0));

        mockMvc.perform(MockMvcRequestBuilders.get("/list")
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Client("Reminder-app")
                                .accessToken(mockAccessToken))
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Login()
                                .attributes((oAuth2AccessToken) -> oAuth2AccessToken.put("email", "smirnov@gmail.com")))
                )
                .andExpect(MockMvcResultMatchers.content().string("{\"content\":[{\"id\":15,\"title\":\"\",\"description\":\"\",\"remind\":\"-999999999-01-01T00:00:00\"}],\"currentPage\":0,\"pageSize\":10,\"totalPages\":1,\"totalElements\":1}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        DeleteReminderRequestDto deleteRequestBody = new DeleteReminderRequestDto(15L);

        Mockito.when(reminderRepository.findById(mockReminder.getId()))
                .thenReturn(Optional.of(mockReminder));

        mockMvc.perform(MockMvcRequestBuilders.delete("/reminder/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(deleteRequestBody))
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Client("Reminder-app")
                                .accessToken(mockAccessToken))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Login()
                                .attributes((oAuth2AccessToken) -> oAuth2AccessToken.put("email", "smirnov@gmail.com")))
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.when(reminderRepository.findByOwner(mockUser, mockPageableDefault))
                .thenReturn(mockPageWithoutContent);

        mockMvc.perform(MockMvcRequestBuilders.get("/list")
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Client("Reminder-app")
                                .accessToken(mockAccessToken))
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Login()
                                .attributes((oAuth2AccessToken) -> oAuth2AccessToken.put("email", "smirnov@gmail.com")))
                )
                .andExpect(MockMvcResultMatchers.content().string("{" +
                        "\"content\":" +
                        "[" + "]," +
                        "\"currentPage\":0," +
                        "\"pageSize\":10," +
                        "\"totalPages\":0," +
                        "\"totalElements\":0" +
                        "}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
