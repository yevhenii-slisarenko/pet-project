package my.iam_service.integration.post_controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import my.iam_service.IamServiceApplication;
import my.iam_service.model.dto.post.PostDTO;
import my.iam_service.model.entity.User;
import my.iam_service.model.exception.InvalidDataException;
import my.iam_service.model.request.post.NewPostRequest;
import my.iam_service.model.response.IamResponse;
import my.iam_service.repository.UserRepository;
import my.iam_service.security.JwtTokenProvider;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Objects;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = {IamServiceApplication.class})
@AutoConfigureMockMvc
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@Tag("integration")
class PostControllerTest {

    @Autowired @Setter
    private MockMvc mockMvc;

    @Autowired @Setter
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String currentJwt;

    @BeforeAll
    @Transactional
    void authorize() {
        User user = userRepository.findById(1)
                .orElseThrow(() -> new InvalidDataException("User with ID: 1 not found"));

        Hibernate.initialize(user.getRoles());
        this.currentJwt = "Bearer " + jwtTokenProvider.generateToken(user);
    }

    @Test
    void getPosts_OK_200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/posts/all")
                        .header(HttpHeaders.AUTHORIZATION, currentJwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Transactional
    void createPost_OK_200() throws Exception {
        NewPostRequest request = new NewPostRequest("Simple Title", "Simple content", 50, null);

        MvcResult requestResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/posts/create")
                        .header(HttpHeaders.AUTHORIZATION, currentJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        IamResponse<PostDTO> response = parsePostDTOResponse(requestResult.getResponse().getContentAsByteArray());

        PostDTO resultBody = Objects.nonNull(response.getPayload()) ? response.getPayload() : null;
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNotNull(resultBody);
        Assertions.assertEquals(request.getTitle(), resultBody.getTitle());
        Assertions.assertEquals(request.getContent(), resultBody.getContent());
        Assertions.assertEquals(request.getLikes() , resultBody.getLikes());
    }

    @Test
    @Transactional
    void updatePost_OK_200() throws Exception {
        NewPostRequest request = new NewPostRequest("Updated Title", "Updated content", 50, null);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/posts/1")
                        .header(HttpHeaders.AUTHORIZATION, currentJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Transactional
    void deletePost_OK_200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/posts/1")
                        .header(HttpHeaders.AUTHORIZATION, currentJwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private IamResponse<PostDTO> parsePostDTOResponse(byte[] contentAsByteArray) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(IamResponse.class, PostDTO.class);
            return objectMapper.readValue(contentAsByteArray, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
