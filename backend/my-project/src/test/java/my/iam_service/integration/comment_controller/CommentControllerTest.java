package my.iam_service.integration.comment_controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import my.iam_service.IamServiceApplication;
import my.iam_service.model.dto.comment.CommentDTO;
import my.iam_service.model.entity.User;
import my.iam_service.model.exception.InvalidDataException;
import my.iam_service.model.request.comment.CommentRequest;
import my.iam_service.model.request.comment.UpdateCommentRequest;
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
class CommentControllerTest {

    @Autowired
    @Setter
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
    void getComments_OK_200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/comments/all")
                        .header(HttpHeaders.AUTHORIZATION, currentJwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Transactional
    void createComment_OK_200() throws Exception {
        CommentRequest request = new CommentRequest(2, "This is a test comment", null);

        MvcResult requestResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/comments/create")
                        .header(HttpHeaders.AUTHORIZATION, currentJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        IamResponse<CommentDTO> response = parsePostDTOResponse(requestResult.getResponse().getContentAsByteArray());

        CommentDTO resultBody = Objects.nonNull(response.getPayload()) ? response.getPayload() : null;
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNotNull(resultBody);
        Assertions.assertEquals(request.getMessage(), resultBody.getMessage());
        Assertions.assertEquals(request.getPostId(), resultBody.getPostId());
    }

    @Test
    @Transactional
    void updateComment_OK_200() throws Exception {
        UpdateCommentRequest request = new UpdateCommentRequest(2, "This is a updated test comment");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/comments/1")
                        .header(HttpHeaders.AUTHORIZATION, currentJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Transactional
    void deleteComment_OK_200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/comments/1")
                        .header(HttpHeaders.AUTHORIZATION, currentJwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private IamResponse<CommentDTO> parsePostDTOResponse(byte[] contentAsByteArray) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(IamResponse.class, CommentDTO.class);
            return objectMapper.readValue(contentAsByteArray, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
