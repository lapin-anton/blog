package ru.yandex.practicum.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.model.entity.Comment;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.util.Base64;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void showPosts_shouldReturnHtmlWithPosts() throws Exception {
        var posts = List.of(
                new Post("Post 1", "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10),
                new Post("Post 2", "+xMdSf3cjVcMebeAedfpnw==", "Post Text2", "Tag2 Tag3", 5)
        );
        postRepository.saveAll(posts);

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
        var posts = List.of(
                new Post("Post 1", "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10),
                new Post("Post 2", "+xMdSf3cjVcMebeAedfpnw==", "Post Text2", "Tag2 Tag3", 5)
        );
        postRepository.saveAll(posts);

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
        var posts = List.of(
                new Post("Post 1", "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10),
                new Post("Post 2", "+xMdSf3cjVcMebeAedfpnw==", "Post Text2", "Tag2 Tag3", 5)
        );
        postRepository.saveAll(posts);

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
                .param("title", "Post 1")
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
                .andExpect(xpath("/html/body/div/table/tr").nodeCount(2))
                .andExpect(xpath("/html/body/div/table/tr[2]/td/div[1]/a/h2").string("Post 1"));
    }

    @Test
    void downloadImage_shouldReturnImageResource() throws Exception {
        var image = "z8LD8hEMeJU77Bg4sqV3yw==";
        var imageBytes = Base64.getDecoder().decode(image);
        var post = new Post("Post 1", image, "Post Text1", "Tag1 Tag2", 10);
        post = postRepository.save(post);

        mockMvc.perform(get("/images/{postId}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andExpect(header().string("Content-Length", String.valueOf(imageBytes.length)))
                .andExpect(content().bytes(imageBytes));
    }

    @Test
    void showPost_shouldReturnHtmlWithPost() throws Exception {
        var post = new Post("Post 1", "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10);
        post = postRepository.save(post);
        var comments = List.of(
                new Comment(post, "Comment 1"),
                new Comment(post, "Comment 2")
        );
        commentRepository.saveAll(comments);

        mockMvc.perform(get("/{postId}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("/html/body/div/table/tr[2]/td/h2").string("Post 1"))
                .andExpect(xpath("/html/body/div/table/tr/td[1]/form/span").nodeCount(2));
    }

    @Test
    void changeRating_shouldRedirectToSamePage() throws Exception {
        var post = new Post("Post 1", "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10);
        post = postRepository.save(post);
        var postId = post.getId();

        mockMvc.perform(post("/{postId}/like", postId)
                        .param("like", "true")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/" + postId));
    }

    @Test
    void deletePost_shouldRedirectToPostsPage() throws Exception {
        var post = new Post("Post 1", "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10);
        post = postRepository.save(post);

        mockMvc.perform(post("/{postId}/delete", post.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void editPost_shouldReturnHtmlAddPostForm() throws Exception {
        var postTitle = "Post 1";
        var post = new Post(postTitle, "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10);
        post = postRepository.save(post);

        mockMvc.perform(get("/{postId}/edit", post.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("add-post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("/html/body/form/table/tr[1]/td/textarea").string(postTitle));
    }

    @Test
    void updatePost_shouldRedirectToEditedPostPage() throws Exception {
        MockMultipartFile mockImage = new MockMultipartFile(
                "test-image",
                "test-image.png",
                "image/png",
                "Mock Image Content".getBytes()
        );
        var post = new Post("Post 1", "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10);
        post = postRepository.save(post);
        var postId = post.getId();
        var editedTitle = "Post Edited";

        mockMvc.perform(multipart("/{postId}", postId)
                        .file("image", mockImage.getBytes())
                        .param("title", editedTitle)
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
                .andExpect(xpath("/html/body/div/table/tr").nodeCount(2))
                .andExpect(xpath("/html/body/div/table/tr[2]/td/div[1]/a/h2").string(editedTitle));
    }

    @Test
    void addComment_shouldReturnSamePostPage() throws Exception {
        var postTitle = "Post 1";
        var post = new Post(postTitle, "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10);
        post = postRepository.save(post);

        var postId = post.getId();
        var addedComment = "Added Comment";
        mockMvc.perform(post("/{postId}/comments", postId).param("text", addedComment))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/" + postId));

        mockMvc.perform(get("/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("/html/body/div/table/tr[2]/td/h2").string(postTitle))
                .andExpect(xpath("/html/body/div/table/tr/td[1]/form/span").nodeCount(1))
                .andExpect(xpath("/html/body/div/table/tr[5]/td[1]/form/span").string(addedComment));
    }

    @Test
    void updateComment_shouldReturnSamePostPage() throws Exception {
        var postTitle = "Post 1";
        var post = new Post(postTitle, "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10);
        post = postRepository.save(post);
        var comment = new Comment(post, "Comment 1");
        comment = commentRepository.save(comment);
        var postId = post.getId();
        var commentId = comment.getId();

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
                .andExpect(xpath("/html/body/div/table/tr[2]/td/h2").string(postTitle))
                .andExpect(xpath("/html/body/div/table/tr/td[1]/form/span").nodeCount(1))
                .andExpect(xpath("/html/body/div/table/tr[5]/td[1]/form/span").string(editedCommentText));
    }

    @Test
    void deleteComment_shouldReturnSamePostPage() throws Exception {
        var postTitle = "Post 1";
        var post = new Post(postTitle, "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10);
        post = postRepository.save(post);
        var comment = new Comment(post, "Comment 1");
        comment = commentRepository.save(comment);
        var postId = post.getId();
        var commentId = comment.getId();

        mockMvc.perform(post("/{postId}/comments/{commentId}/delete", postId, commentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/" + postId));

        mockMvc.perform(get("/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("/html/body/div/table/tr[2]/td/h2").string(postTitle))
                .andExpect(xpath("/html/body/div/table/tr/td[1]/form/span").nodeCount(0));
    }

}