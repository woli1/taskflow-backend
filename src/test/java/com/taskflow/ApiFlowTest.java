package com.taskflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiFlowTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    void registerThenCreateProjectAndTask() throws Exception {
        String reg = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Walid","email":"walid@test.com","password":"password123"}"""))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String token = om.readTree(reg).get("token").asText();

        String proj = mvc.perform(post("/api/projects").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Mon projet","description":"test"}"""))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long projectId = om.readTree(proj).get("id").asLong();

        mvc.perform(post("/api/projects/" + projectId + "/tasks").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Première tâche","priority":"HIGH"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void protectedRouteRequiresToken() throws Exception {
        mvc.perform(get("/api/projects")).andExpect(status().isUnauthorized());
    }

    @Test
    void validationErrorsReturn400() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","email":"pas-un-email","password":"x"}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());
    }
}
