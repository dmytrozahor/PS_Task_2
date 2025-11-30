package com.dmytrozah.profitsoft.app;

import com.dmytrozah.profitsoft.Task2App;
import com.dmytrozah.profitsoft.domain.entity.BookData;
import com.dmytrozah.profitsoft.domain.repository.BookAuthorRepository;
import com.dmytrozah.profitsoft.domain.repository.BookRepository;
import com.dmytrozah.profitsoft.rest.dto.RestResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Task2App.class
)
@AutoConfigureMockMvc
class BookControllerTest {

    static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookAuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    private String name = "Zahorodnii Dmytro";

    private String bookName = "My little story";
    private String genres = "Romantic, Tragedy";

    String bookSaveBody = """
                {
                    "title": "%s",
                    "author": "%s",
                    "genres": "%s"
                }
                """.formatted(bookName, name, genres);

    private String authorSaveBody = """
                {
                    "name": "%s"
                }
                """.formatted(name);


    private String newTitle = "My big story";
    private String newGenres = "A, B, C";

    private String updateBody = """
                {
                    "title": "%s",
                    "author": "%s",
                    "genres": "%s"
                }
                """.formatted(newTitle, name, newGenres);


    @BeforeEach
    void populateAuthors() throws Exception {
        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorSaveBody)
        ).andExpect(status().isCreated()).andReturn();
    }

    @AfterEach
    void cleanup() {
        authorRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    public void testCreateBook() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(bookSaveBody)
        ).andExpect(status().isCreated()).andReturn();

        RestResponse response = parseResponse(result, RestResponse.class);
        long bookId = Long.parseLong(response.getMessage());

        assertThat(bookId).isGreaterThanOrEqualTo(1);

        final BookData data = bookRepository.findById(bookId).orElse(null);

        assertThat(data).isNotNull();
        assertThat(data.getTitle()).isEqualTo(bookName);
        assertThat(data.getAuthor().getName()).isEqualTo(name);
        assertThat(data.getGenres()).isEqualTo(genres);
    }

    @Test
    public void testCreateBookValidation() throws Exception {
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateBook() throws Exception {
        MvcResult saveRes = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookSaveBody)
        ).andExpect(status().isCreated()).andReturn();

        RestResponse response = parseResponse(saveRes, RestResponse.class);

        long bookId = Long.parseLong(response.getMessage());

        MvcResult updateRes = mockMvc.perform(put("/api/books/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody)
        ).andExpect(status().isOk()).andReturn();

        BookData data = bookRepository.findById(bookId).orElse(null);

        assertThat(data).isNotNull();
        assertThat(data.getTitle()).isEqualTo(newTitle);
        assertThat(data.getGenres()).isEqualTo(newGenres);
    }

    private <T> T parseResponse(MvcResult result, Class<T> c){
        try {
            return mapper.readValue(result.getResponse().getContentAsString(), c);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
