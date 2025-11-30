package com.dmytrozah.profitsoft.app;

import com.dmytrozah.profitsoft.Task2App;
import com.dmytrozah.profitsoft.domain.entity.BookData;
import com.dmytrozah.profitsoft.domain.repository.BookAuthorRepository;
import com.dmytrozah.profitsoft.domain.repository.BookRepository;
import com.dmytrozah.profitsoft.rest.dto.BookListDto;
import com.dmytrozah.profitsoft.rest.dto.RestResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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

    private final String bookName = "My little story";
    private final String genres = "Romantic, Tragedy";

    private final String bookSaveBody = """
            {
                "title": "%s",
                "author": "%s",
                "genres": "%s"
            }
            """.formatted(bookName, name, genres);

    private final String authorSaveBody = """
            {
                "name": "%s"
            }
            """.formatted(name);

    private final String newTitle = "My big story";
    private final String newGenres = "A, B, C";

    private final String updateBody = """
            {
                "title": "%s",
                "author": "%s",
                "genres": "%s"
            }
            """.formatted(newTitle, name, newGenres);


    @BeforeEach
    void populateAuthor() throws Exception {
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

        mockMvc.perform(put("/api/books/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody)
        ).andExpect(status().isOk()).andReturn();

        BookData data = bookRepository.findById(bookId).orElse(null);

        assertThat(data).isNotNull();
        assertThat(data.getTitle()).isEqualTo(newTitle);
        assertThat(data.getGenres()).isEqualTo(newGenres);
    }

    @Test
    public void testListBooks() throws Exception {
        List<Long> ids = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            String itBookSaveBody = """
            {
                "title": "%s",
                "author": "%s",
                "genres": "%s"
            }
            """.formatted(bookName + i, name, genres);

            MvcResult result = mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(itBookSaveBody)
            ).andExpect(status().isCreated()).andReturn();

            RestResponse response = parseResponse(result, RestResponse.class);

            ids.add(Long.parseLong(response.getMessage()));
        }

        MvcResult result = mockMvc.perform(post("/api/books/_list")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "size": 50,
                            "page": 0
                        }
                        """)
        ).andExpect(status().isOk()).andReturn();

        BookListDto listDto = parseResponse(result, BookListDto.class);

        assertThat(listDto).isNotNull();
        assertThat(listDto.getInfos().size()).isEqualTo(ids.size());

        for (int i = 0; i < listDto.getInfos().size(); i++) {
            final long id = listDto.getInfos().get(i).getId();

            assertThat(ids.contains(id)).isTrue();
        }
    }

    @Test
    public void testDeleteBook() throws Exception {
        MvcResult saveRes = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookSaveBody)
        ).andExpect(status().isCreated()).andReturn();

        RestResponse response = parseResponse(saveRes, RestResponse.class);

        long bookId = Long.parseLong(response.getMessage());

        mockMvc.perform(delete("/api/books/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody)
        ).andExpect(status().isOk()).andReturn();

        BookData data = bookRepository.findById(bookId).orElse(null);

        assertThat(data).isNull();
    }

    @Test
    public void testGenerateReport() throws Exception {
        for (int i = 0; i < 10; i++) {
            String itBookSaveBody = """
            {
                "title": "%s",
                "author": "%s",
                "genres": "%s"
            }
            """.formatted(bookName + i, name, genres);

            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(itBookSaveBody)
            ).andExpect(status().isCreated()).andReturn();
        }

        MvcResult reportRes = mockMvc.perform(post("/api/books/_report")
        ).andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                )
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books.csv"))
                .andReturn();

        final String content = reportRes.getResponse().getContentAsString();

        assertThat(content).isNotNull();
        assertThat(content).startsWith("Title,Author\n");

        for (int i = 0; i < 10; i++){
            assertThat(content).contains(bookName + i);
        }
    }

    @Test
    public void testGenerateReportAuthor() throws Exception {
        String diffAuthor = name + "-diff";

        String authorSaveBody = """
            {
                "name": "%s"
            }
            """.formatted(diffAuthor);

        final RestResponse creationRes = parseResponse(
                mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorSaveBody)
                ).andExpect(status().isCreated())
                        .andReturn(), RestResponse.class);

        final long newAuthorId = Long.parseLong(creationRes.getMessage());

        for (int i = 0; i < 10; i++) {
            String itBookSaveBody = """
            {
                "title": "%s",
                "author": "%s",
                "genres": "%s"
            }
            """.formatted(bookName + i, i == 9 ? diffAuthor : name, genres);

            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(itBookSaveBody)
            ).andExpect(status().isCreated()).andReturn();
        }

        String generateReportAuthor = """
                {
                    "author_id": %d
                }
                """;

        MvcResult reportRes = mockMvc.perform(post("/api/books/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(generateReportAuthor.formatted(newAuthorId))
                ).andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                )
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books.csv"))
                .andReturn();

        final String content = reportRes.getResponse().getContentAsString();

        assertThat(content).isNotNull();
        assertThat(content).startsWith("Title,Author\n");

        for (int i = 0; i < 9; i++) {
            assertThat(content).doesNotContain(bookName + i);
        }
    }

    private <T> T parseResponse(MvcResult result, Class<T> c) {
        try {
            return mapper.readValue(result.getResponse().getContentAsString(), c);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
