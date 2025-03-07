package ru.yandex.practicum.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.config.DataSourceConfig;
import ru.yandex.practicum.config.WebConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(classes = {DataSourceConfig.class, WebConfig.class})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class PostControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        jdbcTemplate.execute("DELETE FROM comment");
        jdbcTemplate.execute("ALTER TABLE comment ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("DELETE FROM post");
        jdbcTemplate.execute("ALTER TABLE post ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("INSERT INTO post (title, image, text, tags, likes_count) VALUES (?, ?, ?, ?, ?)",
                "Post 1", "Post Image Content One".getBytes(), "Post Text1", "Tag1 Tag2", 10);
        jdbcTemplate.update("INSERT INTO post (title, image, text, tags, likes_count) VALUES (?, ?, ?, ?, ?)",
                "Post 2", "Post Image Content Two".getBytes(), "Post Text2", "Tag2 Tag3", 5);
        jdbcTemplate.execute("INSERT INTO comment (post_id, text) VALUES (1, 'Comment 1')");
        jdbcTemplate.execute("INSERT INTO comment (post_id, text) VALUES (1, 'Comment 2')");
        jdbcTemplate.execute("INSERT INTO comment (post_id, text) VALUES (2, 'Comment 3')");
        jdbcTemplate.execute("INSERT INTO comment (post_id, text) VALUES (2, 'Comment 4')");
    }

    @Test
    void showPosts_shouldReturnHtmlWithPosts() throws Exception {
        mockMvc.perform(get("/")
                        .param("pageSize", "5")
                        .param("pageNumber", "1")
                        .param("search", "")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("search"))
                .andExpect(xpath("/html/body/div/table/tr").nodeCount(3))
                .andExpect(xpath("/html/body/div/table/tr[2]/td/div[1]/a/h2").string("Post 1"))
                .andExpect(xpath("/html/body/div/table/tr[3]/td/div[1]/a/h2").string("Post 2"));
    }

    @Test
    void showPosts_shoudReturnHtmlWithPostsHavingSearchedTagOnly() throws Exception {
        mockMvc.perform(get("/")
                .param("pageSize", "5")
                .param("pageNumber", "1")
                .param("search", "Tag1")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("search"))
                .andExpect(xpath("/html/body/div/table/tr").nodeCount(2))
                .andExpect(xpath("/html/body/div/table/tr[2]/td/div[1]/a/h2").string("Post 1"));

        mockMvc.perform(get("/")
                        .param("pageSize", "5")
                        .param("pageNumber", "1")
                        .param("search", "Tag2")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("search"))
                .andExpect(xpath("/html/body/div/table/tr").nodeCount(3))
                .andExpect(xpath("/html/body/div/table/tr[2]/td/div[1]/a/h2").string("Post 1"))
                .andExpect(xpath("/html/body/div/table/tr[3]/td/div[1]/a/h2").string("Post 2"));

        mockMvc.perform(get("/")
                        .param("pageSize", "5")
                        .param("pageNumber", "1")
                        .param("search", "UnknownTag")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("search"))
                .andExpect(xpath("/html/body/div/table/tr").nodeCount(1));
    }

    @Test
    void showPosts_shouldReturnPostsOnPageOnly() throws Exception {
        mockMvc.perform(get("/")
                        .param("pageSize", "1")
                        .param("pageNumber", "1")
                        .param("search", "")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("search"))
                .andExpect(xpath("/html/body/div/table/tr").nodeCount(2))
                .andExpect(xpath("/html/body/div/table/tr[2]/td/div[1]/a/h2").string("Post 1"));
    }

    @Test
    void addPost_shouldReturnEmptyAddPostForm() throws Exception {
        mockMvc.perform(get("/add"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("add-post"))
                .andExpect(model().size(0));
    }

    @Test
    void savePost_shouldAddNewPost() throws Exception {
        MockMultipartFile mockImage = new MockMultipartFile(
                "test-image",
                "test-image.png",
                "image/png",
                "Mock Image Content".getBytes()
        );
        mockMvc.perform(multipart("/savePost")
                .file("image", mockImage.getBytes())
                .param("title", "Post 3")
                .param("tags", "Tag3 Tag4")
                .param("text", "Post text4")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/")
                        .param("pageSize", "5")
                        .param("pageNumber", "1")
                        .param("search", "")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("search"))
                .andExpect(xpath("/html/body/div/table/tr").nodeCount(4))
                .andExpect(xpath("/html/body/div/table/tr[4]/td/div[1]/a/h2").string("Post 3"));
    }

    @Test
    void downloadImage_shouldReturnImageResource() throws Exception {
        var imageBytes = "Post Image Content One".getBytes();
        mockMvc.perform(get("/images/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andExpect(header().string("Content-Length", String.valueOf(imageBytes.length)))
                .andExpect(content().bytes(imageBytes));
    }

    @Test
    void showPost_shouldReturnHtmlWithPost() throws Exception {
        mockMvc.perform(get("/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("/html/body/div/table/tr[2]/td/h2").string("Post 1"))
                .andExpect(xpath("/html/body/div/table/tr/td[1]/form/span").nodeCount(2));
    }

    @Test
    void changeRating_shouldRedirectToSamePage() throws Exception {
        Long postId = 1L;
        mockMvc.perform(post("/{postId}/like", postId)
                        .param("like", "true")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/" + postId));
    }

    @Test
    void deletePost_shouldRedirectToPostsPage() throws Exception {
        mockMvc.perform(post("/{postId}/delete", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void editPost_shouldReturnHtmlAddPostForm() throws Exception {
        mockMvc.perform(get("/{postId}/edit", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("add-post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("/html/body/form/table/tr[1]/td/textarea").string("Post 1"));
    }

    @Test
    void updatePost_shouldRedirectToEditedPostPage() throws Exception {
        MockMultipartFile mockImage = new MockMultipartFile(
                "test-image",
                "test-image.png",
                "image/png",
                "Mock Image Content".getBytes()
        );
        var postId = 1L;
        mockMvc.perform(multipart("/{postId}", postId)
                        .file("image", mockImage.getBytes())
                        .param("title", "Post 11")
                        .param("tags", "Tag11 Tag22")
                        .param("text", "Post text11")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/" + postId));

        mockMvc.perform(get("/")
                        .param("pageSize", "5")
                        .param("pageNumber", "1")
                        .param("search", "")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("search"))
                .andExpect(xpath("/html/body/div/table/tr").nodeCount(3))
                .andExpect(xpath("/html/body/div/table/tr[2]/td/div[1]/a/h2").string("Post 11"));
    }

    @Test
    void addComment_shouldReturnSamePostPage() throws Exception {
        var postId = 1L;
        var addedComment = "Added Comment";
        mockMvc.perform(post("/{postId}/comments", postId).param("text", addedComment))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/" + postId));

        mockMvc.perform(get("/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("/html/body/div/table/tr[2]/td/h2").string("Post 1"))
                .andExpect(xpath("/html/body/div/table/tr/td[1]/form/span").nodeCount(3))
                .andExpect(xpath("/html/body/div/table/tr[7]/td[1]/form/span").string(addedComment));
    }

    @Test
    void updateComment_shouldReturnSamePostPage() throws Exception {
        var postId = 1L;
        var commentId = 1L;
        var editedCommentText = "Edited Comment";
        mockMvc.perform(post("/{postId}/comments/{commentId}", postId, commentId)
                        .param("text", editedCommentText))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/" + postId));

        mockMvc.perform(get("/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("/html/body/div/table/tr[2]/td/h2").string("Post 1"))
                .andExpect(xpath("/html/body/div/table/tr/td[1]/form/span").nodeCount(2))
                .andExpect(xpath("/html/body/div/table/tr[5]/td[1]/form/span").string(editedCommentText));
    }

    @Test
    void deleteComment_shouldReturnSamePostPage() throws Exception {
        var postId = 1L;
        var commentId = 1L;
        mockMvc.perform(post("/{postId}/comments/{commentId}/delete", postId, commentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/" + postId));

        mockMvc.perform(get("/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("/html/body/div/table/tr[2]/td/h2").string("Post 1"))
                .andExpect(xpath("/html/body/div/table/tr/td[1]/form/span").nodeCount(1));
    }

}