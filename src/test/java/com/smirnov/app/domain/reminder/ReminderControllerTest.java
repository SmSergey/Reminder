package com.smirnov.app.domain.reminder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smirnov.app.domain.reminder.dto.CreateReminderRequestDto;
import com.smirnov.app.domain.reminder.dto.CreateReminderResponseDto;
import com.smirnov.app.domain.reminder.dto.DeleteReminderRequestDto;
import com.smirnov.app.domain.reminder.dto.UpdateReminderRequestDto;
import com.smirnov.app.domain.user.User;
import com.smirnov.app.domain.user.UserRepository;
import org.junit.jupiter.api.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ReminderController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReminderControllerTest {

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

    private final Page<Reminder> mockPageWithoutContent = new PageImpl<>(new ArrayList<>(), mockPageableDefault, 0);

    private final Page<Reminder> mockPageWithContent = new PageImpl<>(
            List.of(
                    new Reminder(1L, "testTitleSearch22", "testDescription", LocalDateTime.MIN, new User()),
                    new Reminder(2L, "testTitle", "testDescription", LocalDateTime.of(2020, 2, 2, 12, 0), new User()),
                    new Reminder(3L, "testTitleSearch", "testDescription", LocalDateTime.MIN, new User()),
                    new Reminder(4L, "testTitle", "testDescription", LocalDateTime.MIN, new User())),
            PageRequest.of(0, 10, Sort.unsorted()),
            4L);
    private final Reminder mockReminder = new Reminder(15L, "", "", LocalDateTime.MIN, mockUser);

    @BeforeEach
    void initCall() {
        Mockito.when(userRepository.findByEmail(mockUser.getEmail()))
                .thenReturn(Optional.of(mockUser));
    }

    @Test
    void getAllRemindersWithoutContentTest() throws Exception {

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
                .andExpect(MockMvcResultMatchers.content().string(
                        "{" +
                                "\"content\":[]," +
                                "\"currentPage\":0," +
                                "\"pageSize\":10," +
                                "\"totalPages\":0," +
                                "\"totalElements\":0" +
                                "}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getAllRemindersDefaultPageableWithContentTest() throws Exception {

        Mockito.when(reminderRepository.findByOwner(mockUser, mockPageableDefault))
                .thenReturn(mockPageWithContent);

        mockMvc.perform(MockMvcRequestBuilders.get("/list")
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Client("Reminder-app")
                                .accessToken(mockAccessToken))
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Login()
                                .attributes((oAuth2AccessToken) -> oAuth2AccessToken.put("email", "smirnov@gmail.com")))
                )
                .andExpect(MockMvcResultMatchers.content().string(
                        "{" +
                                "\"content\":" +
                                "[" +
                                "{\"id\":1,\"title\":\"testTitleSearch22\",\"description\":\"testDescription\",\"remind\":\"-999999999-01-01T00:00:00\"}," +
                                "{\"id\":2,\"title\":\"testTitle\",\"description\":\"testDescription\",\"remind\":\"2020-02-02T12:00:00\"}," +
                                "{\"id\":3,\"title\":\"testTitleSearch\",\"description\":\"testDescription\",\"remind\":\"-999999999-01-01T00:00:00\"}" +
                                ",{\"id\":4,\"title\":\"testTitle\",\"description\":\"testDescription\",\"remind\":\"-999999999-01-01T00:00:00\"}" +
                                "]," +
                                "\"currentPage\":0," +
                                "\"pageSize\":10," +
                                "\"totalPages\":1," +
                                "\"totalElements\":4" +
                                "}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getAllRemindersPaginationWithoutContentTest() throws Exception {

        final PageRequest pageRequest = PageRequest.of(0, 3, Sort.unsorted());
        final Page<Reminder> page = new PageImpl<>(new ArrayList<>(), pageRequest, 0);

        Mockito.when(reminderRepository.findByOwner(mockUser, pageRequest))
                .thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/list")
                        .queryParam("page", "0")
                        .queryParam("size", "3")
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Client("Reminder-app")
                                .accessToken(mockAccessToken))
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Login()
                                .attributes((oAuth2AccessToken) -> oAuth2AccessToken.put("email", "smirnov@gmail.com")))
                )
                .andExpect(MockMvcResultMatchers.content().json(
                        "{" +
                                "\"content\":" +
                                "[]," +
                                "\"currentPage\":0," +
                                "\"pageSize\":3," +
                                "\"totalPages\":0," +
                                "\"totalElements\":0}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void createReminderValidRequestTest() throws Exception {
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
    }

    @Test
    void createReminderTooLongTittleTest() throws Exception {
        CreateReminderRequestDto requestBody = new CreateReminderRequestDto("testTitle".repeat(100),
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
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createReminderTooLongDescriptionTest() throws Exception {
        CreateReminderRequestDto requestBody = new CreateReminderRequestDto("testTitle",
                "testDescription".repeat(1000),
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
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteReminderTest() throws Exception {
        DeleteReminderRequestDto requestBody = new DeleteReminderRequestDto(mockReminder.getId());

        Mockito.when(reminderRepository.findById(mockReminder.getId()))
                .thenReturn(Optional.of(mockReminder));

        mockMvc.perform(MockMvcRequestBuilders.delete("/reminder/delete")
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
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteReminderWithNotExistingIdTest() throws Exception {
        DeleteReminderRequestDto requestBody = new DeleteReminderRequestDto(mockReminder.getId());

        Mockito.when(reminderRepository.findById(11111L))
                .thenReturn(Optional.of(mockReminder));

        mockMvc.perform(MockMvcRequestBuilders.delete("/reminder/delete")
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
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andExpect(MockMvcResultMatchers.jsonPath("message").value("reminder with this id wasn't found"));
    }

    @Test
    void deleteReminderWithNotValidJsonSchemaTest() throws Exception {

        Mockito.when(reminderRepository.findById(mockReminder.getId()))
                .thenReturn(Optional.of(mockReminder));

        mockMvc.perform(MockMvcRequestBuilders.delete("/reminder/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1111aaaa\"}")
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Client("Reminder-app")
                                .accessToken(mockAccessToken))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Login()
                                .attributes((oAuth2AccessToken) -> oAuth2AccessToken.put("email", "smirnov@gmail.com")))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.jsonPath("message").value("not a valid json schema"));
    }

    @Test
    void updateEntityTest() throws Exception {

        UpdateReminderRequestDto requestBody = new UpdateReminderRequestDto(
                23L,
                "updateTitleTest",
                "updateDescriptionTest",
                LocalDateTime.MIN);
        Reminder oldStateReminder = new Reminder(
                23L,
                "testTitle",
                "testDescription",
                LocalDateTime.MIN, mockUser);
        Mockito.when(reminderRepository.findById(requestBody.getId()))
                .thenReturn(Optional.of(oldStateReminder));
        mockMvc.perform(MockMvcRequestBuilders.patch("/reminder/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(requestBody))
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Client("Reminder-app")
                                .accessToken(mockAccessToken))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Login()
                                .attributes((oAuth2AccessToken) -> oAuth2AccessToken.put("email", "smirnov@gmail.com"))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string("{" +
                                "\"id\":23," +
                                "\"title\":\"updateTitleTest\"," +
                                "\"description\":\"updateDescriptionTest\"," +
                                "\"remind\":\"-999999999-01-01T00:00:00\"" +
                                "}"));
    }

    @Test
    void updateEntityWithTooLongTitleTest() throws Exception {

        UpdateReminderRequestDto requestBody = new UpdateReminderRequestDto(
                23L,
                "updateTitleTest".repeat(100),
                "updateDescriptionTest",
                LocalDateTime.MIN);
        Reminder oldStateReminder = new Reminder(
                23L,
                "testTitle",
                "testDescription",
                LocalDateTime.MIN, mockUser);
        Mockito.when(reminderRepository.findById(requestBody.getId()))
                .thenReturn(Optional.of(oldStateReminder));
        mockMvc.perform(MockMvcRequestBuilders.patch("/reminder/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(requestBody))
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Client("Reminder-app")
                                .accessToken(mockAccessToken))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Login()
                                .attributes((oAuth2AccessToken) -> oAuth2AccessToken.put("email", "smirnov@gmail.com"))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateEntityWithTooLongDescriptionTest() throws Exception {

        UpdateReminderRequestDto requestBody = new UpdateReminderRequestDto(
                23L,
                "updateTitleTest",
                "updateDescriptionTest".repeat(10000),
                LocalDateTime.MIN);
        Reminder oldStateReminder = new Reminder(
                23L,
                "testTitle",
                "testDescription",
                LocalDateTime.MIN, mockUser);
        Mockito.when(reminderRepository.findById(requestBody.getId()))
                .thenReturn(Optional.of(oldStateReminder));
        mockMvc.perform(MockMvcRequestBuilders.patch("/reminder/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(requestBody))
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Client("Reminder-app")
                                .accessToken(mockAccessToken))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Login()
                                .attributes((oAuth2AccessToken) -> oAuth2AccessToken.put("email", "smirnov@gmail.com"))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateEntityForNotExistingEntityTest() throws Exception {

        UpdateReminderRequestDto requestBody = new UpdateReminderRequestDto(
                23111111L,
                "updateTitleTest",
                "updateDescriptionTest",
                LocalDateTime.MIN);
        Reminder oldStateReminder = new Reminder(
                23L,
                "testTitle",
                "testDescription",
                LocalDateTime.MIN, mockUser);
        Mockito.when(reminderRepository.findById(requestBody.getId()))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.patch("/reminder/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(requestBody))
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Client("Reminder-app")
                                .accessToken(mockAccessToken))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Login()
                                .attributes((oAuth2AccessToken) -> oAuth2AccessToken.put("email", "smirnov@gmail.com"))))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("reminder with this id wasn't found"));
    }

    @Test
    void searchReminderTest() throws Exception {
        String query = "testTitleSearch";
        Page<Reminder> mockPage = new PageImpl<>(
                List.of(mockPageWithContent.getContent().get(0)),
                PageRequest.of(1, 1, Sort.unsorted()),
                2L
        );
        Mockito.when(reminderRepository.findByQuery(
                        mockUser.getId(),
                        query,
                        PageRequest.of(1, 1, Sort.unsorted())))
                .thenReturn(mockPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/search")
                        .queryParam("page", "1")
                        .queryParam("size", "1")
                        .queryParam("query", query)
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Client("Reminder-app")
                                .accessToken(mockAccessToken))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Login()
                                .attributes((oAuth2AccessToken) -> oAuth2AccessToken.put("email", "smirnov@gmail.com"))))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json("{" +
                                "\"content\":[{\"id\":1," +
                                "\"title\":\"testTitleSearch22\"," +
                                "\"description\":\"testDescription\"," +
                                "\"remind\":\"-999999999-01-01T00:00:00\"}]," +
                                "\"currentPage\":1," +
                                "\"pageSize\":1," +
                                "\"totalPages\":2," +
                                "\"totalElements\":1" +
                                "}"));
    }

    @Test
    void getSortedRemindersSortByIdAscTest() throws Exception {

        Mockito.when(reminderRepository.findByOwner(mockUser, PageRequest.of(0, 20, Sort.by(Sort.Order.asc("id")))))
                .thenReturn(mockPageWithContent);

        mockMvc.perform(MockMvcRequestBuilders.get("/sort")
                        .queryParam("sort", "id,asc")
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Client("Reminder-app")
                                .accessToken(mockAccessToken))
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Login()
                                .attributes((oAuth2AccessToken) -> oAuth2AccessToken.put("email", "smirnov@gmail.com"))))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(
                                "[" +
                                        "{\"id\":1,\"title\":\"testTitleSearch22\",\"description\":\"testDescription\",\"remind\":\"-999999999-01-01T00:00:00\"}," +
                                        "{\"id\":2,\"title\":\"testTitle\",\"description\":\"testDescription\",\"remind\":\"2020-02-02T12:00:00\"}," +
                                        "{\"id\":3,\"title\":\"testTitleSearch\",\"description\":\"testDescription\",\"remind\":\"-999999999-01-01T00:00:00\"}" +
                                        ",{\"id\":4,\"title\":\"testTitle\",\"description\":\"testDescription\",\"remind\":\"-999999999-01-01T00:00:00\"}" +
                                        "]"));
    }

    @Test
    void getFilteredReminders() throws Exception {

        LocalDate fromDate = LocalDate.of(2020, 1, 1);
        LocalDate toDate = LocalDate.of(2021, 1, 1);
        LocalTime fromTime = LocalTime.of(12, 0);
        LocalTime toTime = LocalTime.of(12, 0);

        Mockito.when(reminderRepository.findRemindersFilteredByDateAndTime(mockUser.getId(),
                        fromDate.atStartOfDay(),
                        toDate.atStartOfDay(),
                        fromTime,
                        toTime))
                .thenReturn(List.of(
                        mockPageWithContent.getContent().get(1)
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/filtr")
                        .queryParam("fromDate", fromDate.format(DateTimeFormatter.ISO_DATE))
                        .queryParam("toDate", toDate.format(DateTimeFormatter.ISO_DATE))
                        .queryParam("fromTime", fromTime.format(DateTimeFormatter.ISO_TIME))
                        .queryParam("toTime", toTime.format(DateTimeFormatter.ISO_TIME))
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Client("Reminder-app")
                                .accessToken(mockAccessToken))
                        .with(SecurityMockMvcRequestPostProcessors
                                .oauth2Login()
                                .attributes((oAuth2AccessToken) -> oAuth2AccessToken.put("email", "smirnov@gmail.com"))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("" +
                        "[{" +
                        "\"id\":2," +
                        "\"title\":\"testTitle\"," +
                        "\"description\":\"testDescription\"," +
                        "\"remind\":\"2020-02-02T12:00:00\"" +
                        "}]"));
    }
}