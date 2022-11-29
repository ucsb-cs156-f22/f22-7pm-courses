package edu.ucsb.cs156.courses.controllers;

import edu.ucsb.cs156.courses.repositories.UserRepository;
import edu.ucsb.cs156.courses.testconfig.TestConfig;
import edu.ucsb.cs156.courses.ControllerTestCase;
import edu.ucsb.cs156.courses.entities.PersonalSchedule;
import edu.ucsb.cs156.courses.entities.User;
import edu.ucsb.cs156.courses.repositories.PersonalScheduleRepository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = PersonalSchedulesController.class)
@Import(TestConfig.class)
@AutoConfigureDataJpa
public class PersonalSchedulesControllerTests extends ControllerTestCase {

    @MockBean
    PersonalScheduleRepository personalscheduleRepository;

    @MockBean
    UserRepository userRepository;

    // Authorization tests for /api/personalschedules/admin/all

    @Test
    public void api_schedules_admin_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/personalschedules/admin/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules_admin_all__user_logged_in__returns_403() throws Exception {
        mockMvc.perform(get("/api/personalschedules/admin/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules_admin__user_logged_in__returns_403() throws Exception {
        mockMvc.perform(get("/api/personalschedules/admin?id=7"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void api_schedules_admin_all__admin_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/personalschedules/admin/all"))
                .andExpect(status().isOk());
    }

    // Authorization tests for /api/personalschedules/all

    @Test
    public void api_schedules_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/personalschedules/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules_all__user_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/personalschedules/all"))
                .andExpect(status().isOk());
    }

    // Authorization tests for /api/personalschedules/post

    @Test
    public void api_schedules_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/personalschedules/post"))
                .andExpect(status().is(403));
    }

    // Tests with mocks for database actions

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules__user_logged_in__returns_a_schedules_that_exists() throws Exception {

        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        PersonalSchedule personalschedule1 = PersonalSchedule.builder().name("Name 1").description("Description 1").quarter("20221").user(u).id(7L).build();
        when(personalscheduleRepository.findByIdAndUser(eq(7L), eq(u))).thenReturn(Optional.of(personalschedule1));

        // act
        MvcResult response = mockMvc.perform(get("/api/personalschedules?id=7"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(personalscheduleRepository, times(1)).findByIdAndUser(7L, u);
        String expectedJson = mapper.writeValueAsString(personalschedule1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules__user_logged_in__search_for_schedules_that_does_not_exist() throws Exception {

        // arrange

        User u = currentUserService.getCurrentUser().getUser();

        when(personalscheduleRepository.findByIdAndUser(eq(7L), eq(u))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(get("/api/personalschedules?id=7"))
                .andExpect(status().isNotFound()).andReturn();

        // assert

        verify(personalscheduleRepository, times(1)).findByIdAndUser(7L, u);
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("PersonalSchedule with id 7 not found", json.get("message"));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules__user_logged_in__search_for_schedules_that_belongs_to_another_user() throws Exception {

        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        User otherUser = User.builder().id(999L).build();
        PersonalSchedule otherUsersPersonalschedule = PersonalSchedule.builder().name("Name 1").description("Description 1").quarter("20221").user(otherUser).id(13L)
                .build();

        when(personalscheduleRepository.findByIdAndUser(eq(13L), eq(otherUser))).thenReturn(Optional.of(otherUsersPersonalschedule));

        // act
        MvcResult response = mockMvc.perform(get("/api/personalschedules?id=13"))
                .andExpect(status().isNotFound()).andReturn();

        // assert

        verify(personalscheduleRepository, times(1)).findByIdAndUser(13L, u);
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("PersonalSchedule with id 13 not found", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void api_schedules__admin_logged_in__search_for_schedule_that_belongs_to_another_user() throws Exception {

        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        User otherUser = User.builder().id(999L).build();
        PersonalSchedule otherUsersPersonalschedule = PersonalSchedule.builder().name("Name 1").description("Description 1").quarter("20221").user(otherUser).id(27L)
                .build();

        when(personalscheduleRepository.findById(eq(27L))).thenReturn(Optional.of(otherUsersPersonalschedule));

        // act
        MvcResult response = mockMvc.perform(get("/api/personalschedules/admin?id=27"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(personalscheduleRepository, times(1)).findById(27L);
        String expectedJson = mapper.writeValueAsString(otherUsersPersonalschedule);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void api_schedules__admin_logged_in__search_for_schedule_that_does_not_exist() throws Exception {

        // arrange

        when(personalscheduleRepository.findById(eq(29L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(get("/api/personalschedules/admin?id=29"))
                .andExpect(status().isNotFound()).andReturn();

        // assert

        verify(personalscheduleRepository, times(1)).findById(29L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("PersonalSchedule with id 29 not found", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void api_schedules_admin_all__admin_logged_in__returns_all_schedules() throws Exception {

        // arrange

        User u1 = User.builder().id(1L).build();
        User u2 = User.builder().id(2L).build();
        User u = currentUserService.getCurrentUser().getUser();

        PersonalSchedule p1 = PersonalSchedule.builder().name("Name 1").description("Description 1").quarter("20221").user(u1).id(1L).build();
        PersonalSchedule p2 = PersonalSchedule.builder().name("Name 2").description("Description 2").quarter("20222").user(u2).id(2L).build();
        PersonalSchedule p3 = PersonalSchedule.builder().name("Name 3").description("Description 3").quarter("20223").user(u).id(3L).build();

        ArrayList<PersonalSchedule> expectedSchedules = new ArrayList<>();
        expectedSchedules.addAll(Arrays.asList(p1, p2, p3));

        when(personalscheduleRepository.findAll()).thenReturn(expectedSchedules);

        // act
        MvcResult response = mockMvc.perform(get("/api/personalschedules/admin/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(personalscheduleRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedSchedules);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules_all__user_logged_in__returns_only_schedules_for_user() throws Exception {

        // arrange

        User thisUser = currentUserService.getCurrentUser().getUser();

        PersonalSchedule p1 = PersonalSchedule.builder().name("Name 1").description("Description 1").quarter("20221").user(thisUser).id(1L).build();
        PersonalSchedule p2 = PersonalSchedule.builder().name("Name 2").description("Description 2").quarter("20222").user(thisUser).id(2L).build();

        ArrayList<PersonalSchedule> expectedSchedules = new ArrayList<>();
        expectedSchedules.addAll(Arrays.asList(p1, p2));
        when(personalscheduleRepository.findAllByUserId(thisUser.getId())).thenReturn(expectedSchedules);

        // act
        MvcResult response = mockMvc.perform(get("/api/personalschedules/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(personalscheduleRepository, times(1)).findAllByUserId(eq(thisUser.getId()));
        String expectedJson = mapper.writeValueAsString(expectedSchedules);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules_post__user_logged_in() throws Exception {
        // arrange

        User thisUser = currentUserService.getCurrentUser().getUser();

        PersonalSchedule expectedSchedule = PersonalSchedule.builder().name("Test Name").description("Test Description").quarter("20222").user(thisUser).id(0L).build();

        when(personalscheduleRepository.save(eq(expectedSchedule))).thenReturn(expectedSchedule);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/personalschedules/post?name=Test Name&description=Test Description&quarter=20222")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(personalscheduleRepository, times(1)).save(expectedSchedule);
        String expectedJson = mapper.writeValueAsString(expectedSchedule);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules__user_logged_in__delete_schedule() throws Exception {
        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        PersonalSchedule ps1 = PersonalSchedule.builder().name("Name 1").description("Description 1").quarter("20221").user(u).id(15L).build();
        when(personalscheduleRepository.findByIdAndUser(eq(15L), eq(u))).thenReturn(Optional.of(ps1));

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/personalschedules?id=15")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(personalscheduleRepository, times(1)).findByIdAndUser(15L, u);
        verify(personalscheduleRepository, times(1)).delete(ps1);
        Map<String, Object> json = responseToJson(response);
        assertEquals("PersonalSchedule with id 15 deleted", json.get("message"));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules__user_logged_in__delete_schedule_that_does_not_exist() throws Exception {
        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        User otherUser = User.builder().id(98L).build();
        PersonalSchedule ps1 = PersonalSchedule.builder().name("Name 1").description("Description 1").quarter("20221").user(u).id(15L).build();
        when(personalscheduleRepository.findByIdAndUser(eq(15L), eq(otherUser))).thenReturn(Optional.of(ps1));

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/personalschedules?id=15")
                        .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(personalscheduleRepository, times(1)).findByIdAndUser(15L, u);
        Map<String, Object> json = responseToJson(response);
        assertEquals("PersonalSchedule with id 15 not found", json.get("message"));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules__user_logged_in__cannot_delete_delete_belonging_to_another_user() throws Exception {
        // arrange
        User u = currentUserService.getCurrentUser().getUser();
        User otherUser = User.builder().id(98L).build();
        PersonalSchedule ps1 = PersonalSchedule.builder().name("Name 1").description("Description 1").quarter("20221").user(otherUser).id(31L).build();
        when(personalscheduleRepository.findById(eq(31L))).thenReturn(Optional.of(ps1));

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/personalschedules?id=31")
                        .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(personalscheduleRepository, times(1)).findByIdAndUser(31L, u);
        Map<String, Object> json = responseToJson(response);
        assertEquals("PersonalSchedule with id 31 not found", json.get("message"));
    }


    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void api_schedules__admin_logged_in__delete_schedule() throws Exception {
        // arrange

        User otherUser = User.builder().id(98L).build();
        PersonalSchedule ps1 = PersonalSchedule.builder().name("Name 1").description("Description 1").quarter("20221").user(otherUser).id(16L).build();
        when(personalscheduleRepository.findById(eq(16L))).thenReturn(Optional.of(ps1));

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/personalschedules/admin?id=16")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(personalscheduleRepository, times(1)).findById(16L);
        verify(personalscheduleRepository, times(1)).delete(ps1);
        Map<String, Object> output = responseToJson(response);
        assertEquals("PersonalSchedule with id 16 deleted", output.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void api_schedules__admin_logged_in__cannot_delete_schedule_that_does_not_exist() throws Exception {
        // arrange

        when(personalscheduleRepository.findById(eq(17L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/personalschedules/admin?id=17")
                        .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(personalscheduleRepository, times(1)).findById(17L);
        Map<String, Object> output = responseToJson(response);
        assertEquals("PersonalSchedule with id 17 not found", output.get("message"));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules__user_logged_in__put_schedule() throws Exception {
        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        User otherUser = User.builder().id(999).build();
        PersonalSchedule ps1 = PersonalSchedule.builder().name("Name 1").description("Description 1").quarter("20221").user(u).id(67L).build();
        // We deliberately set the user information to another user
        // This should get ignored and overwritten with current user when todo is saved

        PersonalSchedule updatedSchedule = PersonalSchedule.builder().name("Name 2").description("Description 2").quarter("20222").user(otherUser).id(67L).build();
        PersonalSchedule correctSchedule = PersonalSchedule.builder().name("Name 2").description("Description 2").quarter("20222").user(u).id(67L).build();

        String requestBody = mapper.writeValueAsString(updatedSchedule);
        String expectedReturn = mapper.writeValueAsString(correctSchedule);

        when(personalscheduleRepository.findByIdAndUser(eq(67L), eq(u))).thenReturn(Optional.of(ps1));

        // act
        MvcResult response = mockMvc.perform(
                put("/api/personalschedules?id=67")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(personalscheduleRepository, times(1)).findByIdAndUser(67L, u);
        verify(personalscheduleRepository, times(1)).save(correctSchedule); // should be saved with correct user
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedReturn, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules__user_logged_in__cannot_put_schedule_that_does_not_exist() throws Exception {
        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        PersonalSchedule updatedSchedule = PersonalSchedule.builder().name("Name 2").description("Description 2").quarter("20222").id(67L).build();

        String requestBody = mapper.writeValueAsString(updatedSchedule);

        when(personalscheduleRepository.findByIdAndUser(eq(67L), eq(u))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/personalschedules?id=67")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(personalscheduleRepository, times(1)).findByIdAndUser(67L, u);
        Map<String, Object> output = responseToJson(response);
        assertEquals("PersonalSchedule with id 67 not found", output.get("message"));
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules__user_logged_in__cannot_put_schedule_for_another_user() throws Exception {
        // arrange

        User u = currentUserService.getCurrentUser().getUser();
        User otherUser = User.builder().id(98L).build();
        PersonalSchedule ps1 = PersonalSchedule.builder().name("Name 1").description("Description 1").quarter("20221").user(otherUser).id(31L).build();
        PersonalSchedule updatedSchedule = PersonalSchedule.builder().name("Name 2").description("Description 2").quarter("20222").id(31L).build();

        when(personalscheduleRepository.findByIdAndUser(eq(31L), eq(otherUser))).thenReturn(Optional.of(ps1));

        String requestBody = mapper.writeValueAsString(updatedSchedule);

        // act
        MvcResult response = mockMvc.perform(
                put("/api/personalschedules?id=31")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(personalscheduleRepository, times(1)).findByIdAndUser(31L, u);
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("PersonalSchedule with id 31 not found", json.get("message"));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules__user_logged_in__cannot_put_schedule_that_already_exists() throws Exception {
        // arrange

        User user = currentUserService.getCurrentUser().getUser();
        PersonalSchedule ps1 = PersonalSchedule.builder().name("Name 1").description("Description 1").quarter("20221").user(user).id(77L).build();
        PersonalSchedule ps2 = PersonalSchedule.builder().name("Name 2").description("Description 2").quarter("20222").user(user).id(78L).build();
        PersonalSchedule ps3 = PersonalSchedule.builder().name("Name 2").description("Description 2").quarter("20224").user(user).id(78L).build();
        PersonalSchedule ps4 = PersonalSchedule.builder().name("Name 3").description("Description 2").quarter("20222").user(user).id(78L).build();
        String ps2String = mapper.writeValueAsString(ps2);

        when(personalscheduleRepository.findByIdAndUser(eq(77L), eq(user))).thenReturn(Optional.of(ps1));

        ArrayList<PersonalSchedule> expectedSchedules = new ArrayList<>();
        expectedSchedules.addAll(Arrays.asList(ps3, ps4, ps1, ps2));

        when(personalscheduleRepository.findAllByUserId(user.getId())).thenReturn(expectedSchedules);

        // act
        MvcResult response = mockMvc.perform(
                put("/api/personalschedules?id=77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(ps2String)
                        .with(csrf()))
                .andExpect(status().is(404)).andReturn();

        // assert
        verify(personalscheduleRepository, times(1)).findByIdAndUser(77L, user);
        verify(personalscheduleRepository, times(1)).findAllByUserId(user.getId());
        Map<String, Object> json = responseToJson(response);
        assertEquals("NameAndQuarterExistsException", json.get("type"));
    }

@WithMockUser(roles = { "USER" })
@Test
public void api_schedules__user_logged_in__can_put_schedule_that_does_not_already_exist() throws Exception {

        User user = currentUserService.getCurrentUser().getUser();
        PersonalSchedule ps1 = PersonalSchedule.builder().name("NameLengthened1").description("Description 1").quarter("20221").user(user).id(77L).build();
        PersonalSchedule ps1Array = PersonalSchedule.builder().name("NameLengthened1").description("Description 1").quarter("20221").user(user).id(77L).build();
        PersonalSchedule ps2 = PersonalSchedule.builder().name("NameLengthened2").description("Description 2").quarter("20222").user(user).id(77L).build();

//     String ps1String = mapper.writeValueAsString(ps1);
        String ps2String = mapper.writeValueAsString(ps2);

        when(personalscheduleRepository.findByIdAndUser(eq(77L), eq(user))).thenReturn(Optional.of(ps1));

        ArrayList<PersonalSchedule> expectedSchedules = new ArrayList<>();
        expectedSchedules.addAll(Arrays.asList(ps1Array));

        when(personalscheduleRepository.findAllByUserId(user.getId())).thenReturn(expectedSchedules);

    // act
        MvcResult response = mockMvc.perform(
                put("/api/personalschedules?id=77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(ps2String)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

    // assert
        verify(personalscheduleRepository, times(1)).findByIdAndUser(77L, user);
        verify(personalscheduleRepository, times(1)).findAllByUserId(user.getId());
        String responseString = response.getResponse().getContentAsString();
        assertEquals(ps2String, responseString);
}
@WithMockUser(roles = { "USER" })
@Test
public void api_schedules__user_logged_in__can_put_schedule_with_quarter_that_does_not_already_exist() throws Exception {

        User user = currentUserService.getCurrentUser().getUser();
        PersonalSchedule ps1 = PersonalSchedule.builder().name("NameLengthened1").description("Description 1").quarter("20221").user(user).id(77L).build();
        PersonalSchedule ps1Array = PersonalSchedule.builder().name("NameLengthened1").description("Description 1").quarter("20221").user(user).id(77L).build();
        PersonalSchedule ps2 = PersonalSchedule.builder().name("NameLengthened1").description("Description 2").quarter("20222").user(user).id(77L).build();

//     String ps1String = mapper.writeValueAsString(ps1);
        String ps2String = mapper.writeValueAsString(ps2);

        when(personalscheduleRepository.findByIdAndUser(eq(77L), eq(user))).thenReturn(Optional.of(ps1));

        ArrayList<PersonalSchedule> expectedSchedules = new ArrayList<>();
        expectedSchedules.addAll(Arrays.asList(ps1Array));

        when(personalscheduleRepository.findAllByUserId(user.getId())).thenReturn(expectedSchedules);

    // act
        MvcResult response = mockMvc.perform(
                put("/api/personalschedules?id=77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(ps2String)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

    // assert
        verify(personalscheduleRepository, times(1)).findByIdAndUser(77L, user);
        verify(personalscheduleRepository, times(1)).findAllByUserId(user.getId());
        String responseString = response.getResponse().getContentAsString();
        assertEquals(ps2String, responseString);
}


    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void api_schedules__admin_logged_in__put_schedule() throws Exception {
        // arrange

        User otherUser = User.builder().id(255L).build();
        PersonalSchedule ps1 = PersonalSchedule.builder().name("Name 1").description("Description 1").quarter("20221").user(otherUser).id(77L).build();
        User yetAnotherUser = User.builder().id(512L).build();
        // We deliberately put the wrong user on the updated schedule
        // We expect the controller to ignore this and keep the user the same
        PersonalSchedule updatedSchedule = PersonalSchedule.builder().name("Name 2").description("Description 2").quarter("20222").user(yetAnotherUser).id(77L)
                .build();
        PersonalSchedule correctSchedule = PersonalSchedule.builder().name("Name 2").description("Description 2").quarter("20222").user(otherUser).id(77L)
                .build();

        String requestBody = mapper.writeValueAsString(updatedSchedule);
        String expectedJson = mapper.writeValueAsString(correctSchedule);

        when(personalscheduleRepository.findById(eq(77L))).thenReturn(Optional.of(ps1));

        // act
        MvcResult response = mockMvc.perform(
                put("/api/personalschedules/admin?id=77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(personalscheduleRepository, times(1)).findById(77L);
        verify(personalscheduleRepository, times(1)).save(correctSchedule);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void api_schedules__admin_logged_in__cannot_put_schedule_that_does_not_exist() throws Exception {
        // arrange

        User otherUser = User.builder().id(345L).build();
        PersonalSchedule updatedSchedule = PersonalSchedule.builder().name("Name 1").description("Description 1").quarter("20221").user(otherUser).id(77L)
                .build();

        String requestBody = mapper.writeValueAsString(updatedSchedule);

        when(personalscheduleRepository.findById(eq(77L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/personalschedules/admin?id=77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(personalscheduleRepository, times(1)).findById(77L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("PersonalSchedule with id 77 not found", json.get("message"));
    }

    // Call 
    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void api_schedules__admin_logged_in__cannot_put_schedule_that_already_exists() throws Exception {
        // arrange

        User user = User.builder().id(255L).build();
        PersonalSchedule ps1 = PersonalSchedule.builder().name("Name 2").description("Description 1").quarter("20221").user(user).id(77L).build();
        PersonalSchedule ps2 = PersonalSchedule.builder().name("Name 2").description("Description 2").quarter("20222").user(user).id(78L).build();
        PersonalSchedule ps3 = PersonalSchedule.builder().name("Name 2").description("Description 1").quarter("20224").user(user).id(77L).build();
        PersonalSchedule ps4 = PersonalSchedule.builder().name("Name 3").description("Description 1").quarter("20221").user(user).id(77L).build();

        // String ps1String = mapper.writeValueAsString(ps1);
        String ps2String = mapper.writeValueAsString(ps2);

        when(personalscheduleRepository.findById(eq(77L))).thenReturn(Optional.of(ps1));

        ArrayList<PersonalSchedule> expectedSchedules = new ArrayList<>();
        expectedSchedules.addAll(Arrays.asList(ps3, ps4, ps1, ps2));

        when(personalscheduleRepository.findAll()).thenReturn(expectedSchedules);

        // act
        MvcResult response = mockMvc.perform(
                put("/api/personalschedules/admin?id=77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(ps2String)
                        .with(csrf()))
                .andExpect(status().is(404)).andReturn();

        // assert
        verify(personalscheduleRepository, times(1)).findById(77L);
        verify(personalscheduleRepository, times(1)).findAll();
        Map<String, Object> json = responseToJson(response);
        assertEquals("NameAndQuarterExistsException", json.get("type"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void api_schedules__admin_logged_in__can_put_schedule_that_does_not_already_exist() throws Exception {
        // arrange

        User user = User.builder().id(255L).build();
        PersonalSchedule ps1 = PersonalSchedule.builder().name("NameLengthened1").description("Description 1").quarter("20221").user(user).id(77L).build();
        PersonalSchedule ps1Array = PersonalSchedule.builder().name("NameLengthened1").description("Description 1").quarter("20221").user(user).id(77L).build();
        PersonalSchedule ps2 = PersonalSchedule.builder().name("NameLengthened2").description("Description 2").quarter("20222").user(user).id(77L).build();

        // String ps1String = mapper.writeValueAsString(ps1);
        String ps2String = mapper.writeValueAsString(ps2);

        when(personalscheduleRepository.findById(eq(77L))).thenReturn(Optional.of(ps1));

        ArrayList<PersonalSchedule> expectedSchedules = new ArrayList<>();
        expectedSchedules.addAll(Arrays.asList(ps1Array));

        when(personalscheduleRepository.findAll()).thenReturn(expectedSchedules);

        // act
        MvcResult response = mockMvc.perform(
                put("/api/personalschedules/admin?id=77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(ps2String)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(personalscheduleRepository, times(1)).findById(77L);
        verify(personalscheduleRepository, times(1)).findAll();
        String responseString = response.getResponse().getContentAsString();
        assertEquals(ps2String, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void api_schedules__admin_logged_in__can_put_schedule_with_quarter_that_does_not_already_exist() throws Exception {
        // arrange

        User user = User.builder().id(255L).build();
        PersonalSchedule ps1 = PersonalSchedule.builder().name("NameLengthened1").description("Description 1").quarter("20221").user(user).id(77L).build();
        PersonalSchedule ps1Array = PersonalSchedule.builder().name("NameLengthened1").description("Description 1").quarter("20221").user(user).id(77L).build();
        PersonalSchedule ps2 = PersonalSchedule.builder().name("NameLengthened1").description("Description 2").quarter("20222").user(user).id(77L).build();

        // String ps1String = mapper.writeValueAsString(ps1);
        String ps2String = mapper.writeValueAsString(ps2);

        when(personalscheduleRepository.findById(eq(77L))).thenReturn(Optional.of(ps1));

        ArrayList<PersonalSchedule> expectedSchedules = new ArrayList<>();
        expectedSchedules.addAll(Arrays.asList(ps1Array));

        when(personalscheduleRepository.findAll()).thenReturn(expectedSchedules);

        // act
        MvcResult response = mockMvc.perform(
                put("/api/personalschedules/admin?id=77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(ps2String)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(personalscheduleRepository, times(1)).findById(77L);
        verify(personalscheduleRepository, times(1)).findAll();
        String responseString = response.getResponse().getContentAsString();
        assertEquals(ps2String, responseString);
    }
    
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedule_user_post_invalid_name() throws Exception {
        // arrange
        User u = currentUserService.getCurrentUser().getUser();

        // act
        // ThisSixteenChars -> exactly 16 characters to consider boundary change in mutation tests
        MvcResult response = mockMvc.perform(
                post("/api/personalschedules/post?description=theDescription&name=ThisNameIsWayTooLong&quarter=W22")
                        .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        // assert
        Map<String, Object> json = responseToJson(response);
        assertEquals("Name: ThisNameIsWayTooLong too long (Name must be no more than 15 characters)", json.get("message"));
        assertEquals("CharLimitExceededException", json.get("type"));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules_post__user_logged_in_cannot_post_schedule_if_name_and_quarter_already_exists() throws Exception {
        // arrange
        User user = currentUserService.getCurrentUser().getUser();
        PersonalSchedule p1 = PersonalSchedule.builder().name("Name1").description("Description1").quarter("20222").user(user).id(1L).build();

        ArrayList<PersonalSchedule> expectedSchedules = new ArrayList<>();
        expectedSchedules.addAll(Arrays.asList(p1));

        when(personalscheduleRepository.findAllByUserId(user.getId())).thenReturn(expectedSchedules);

        // act
        mockMvc.perform(
                post("/api/personalschedules/post?description=Description1&name=Name1&quarter=20224")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();
        
        mockMvc.perform(
                post("/api/personalschedules/post?description=Description1&name=Name2&quarter=20222")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        MvcResult response = mockMvc.perform(
                post("/api/personalschedules/post?description=Description1&name=Name1&quarter=20222")
                        .with(csrf()))
                .andExpect(status().is(404)).andReturn();

        // assert
        verify(personalscheduleRepository, times(3)).findAllByUserId(user.getId());
        Map<String, Object> json = responseToJson(response);
        assertEquals("NameAndQuarterExistsException", json.get("type"));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules_post__user_logged_in_can_post_schedule_if_quarter_does_not_already_exist() throws Exception {
        // arrange
        User user = currentUserService.getCurrentUser().getUser();
        PersonalSchedule p1 = PersonalSchedule.builder().name("NameLengthened1").description("Description1").quarter("20222").user(user).id(0L).build();
        PersonalSchedule p2 = PersonalSchedule.builder().name("NameLengthened1").description("Description1").quarter("20221").user(user).id(1L).build();

        ArrayList<PersonalSchedule> expectedSchedules = new ArrayList<>();
        expectedSchedules.addAll(Arrays.asList(p2));

        when(personalscheduleRepository.findAllByUserId(user.getId())).thenReturn(expectedSchedules);
        when(personalscheduleRepository.save(eq(p1))).thenReturn(p1);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/personalschedules/post?description=Description1&name=NameLengthened1&quarter=20222")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        // verify(personalscheduleRepository, times(1)).findAllByUserId(user.getId());
        verify(personalscheduleRepository, times(1)).save(p1);
        String responseString = response.getResponse().getContentAsString();
        String expectedJson = mapper.writeValueAsString(p1);
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_schedules_post__user_logged_in_can_post_schedule_if_name_does_not_already_exist() throws Exception {
        // arrange
        User user = currentUserService.getCurrentUser().getUser();
        PersonalSchedule p1 = PersonalSchedule.builder().name("NameLengthened1").description("Description1").quarter("20222").user(user).id(0L).build();
        PersonalSchedule p2 = PersonalSchedule.builder().name("NameLengthened2").description("Description1").quarter("20222").user(user).id(1L).build();

        ArrayList<PersonalSchedule> expectedSchedules = new ArrayList<>();
        expectedSchedules.addAll(Arrays.asList(p2));

        when(personalscheduleRepository.findAllByUserId(user.getId())).thenReturn(expectedSchedules);
        when(personalscheduleRepository.save(eq(p1))).thenReturn(p1);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/personalschedules/post?description=Description1&name=NameLengthened1&quarter=20222")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        // verify(personalscheduleRepository, times(1)).findAllByUserId(user.getId());
        verify(personalscheduleRepository, times(1)).save(p1);
        String responseString = response.getResponse().getContentAsString();
        String expectedJson = mapper.writeValueAsString(p1);
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void api_schedules_post__admin_logged_in_cannot_post_schedule_if_name_and_quarter_already_exists() throws Exception {
        // arrange
        User user = currentUserService.getCurrentUser().getUser();
        PersonalSchedule p1 = PersonalSchedule.builder().name("Name1").description("Description1").quarter("20222").user(user).id(1L).build();

        ArrayList<PersonalSchedule> expectedSchedules = new ArrayList<>();
        expectedSchedules.addAll(Arrays.asList(p1));

        when(personalscheduleRepository.findAllByUserId(user.getId())).thenReturn(expectedSchedules);

        // act
        mockMvc.perform(
                post("/api/personalschedules/post?description=Description1&name=Name1&quarter=20224")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        mockMvc.perform(
                post("/api/personalschedules/post?description=Description1&name=Name2&quarter=20222")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();
        
        MvcResult response = mockMvc.perform(
                post("/api/personalschedules/post?description=Description1&name=Name1&quarter=20222")
                        .with(csrf()))
                .andExpect(status().is(404)).andReturn();

        // assert
        verify(personalscheduleRepository, times(3)).findAllByUserId(user.getId());
        Map<String, Object> json = responseToJson(response);
        assertEquals("NameAndQuarterExistsException", json.get("type"));
    }
    
    public void api_schedules_post_fifteen_char_name() throws Exception {
        // arrange

        User thisUser = currentUserService.getCurrentUser().getUser();

        PersonalSchedule expectedSchedule = PersonalSchedule.builder().name("15 Characters!!").description("Test Description").quarter("20222").user(thisUser).id(0L).build();

        when(personalscheduleRepository.save(eq(expectedSchedule))).thenReturn(expectedSchedule);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/personalschedules/post?name=15 Characters!!&description=Test Description&quarter=20222")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(personalscheduleRepository, times(1)).save(expectedSchedule);
        String expectedJson = mapper.writeValueAsString(expectedSchedule);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

}