package ru.yandex.practicum.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.config.DataSourceConfig;
import ru.yandex.practicum.model.Post;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Import(DataSourceConfig.class)
class PostRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM post");
        jdbcTemplate.execute("ALTER TABLE post ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("INSERT INTO post (title, image, text, tags, likes_count) VALUES (?, ?, ?, ?, ?)",
                "Post 1", "Post Image Content One".getBytes(), "Post Text1", "Tag1 Tag2", 10);
        jdbcTemplate.update("INSERT INTO post (title, image, text, tags, likes_count) VALUES (?, ?, ?, ?, ?)",
                "Post 2", "Post Image Content Two".getBytes(), "Post Text2", "Tag2 Tag3", 5);
    }

    @Test
    void findAll_shouldReturnAllPostsByPage() throws Exception {
        var expected = List.of(
                new Post(1L, "Post 1", "Post Image Content One".getBytes(), "Post Text1", "Tag1 Tag2", 10),
                new Post(2L, "Post 2", "Post Image Content Two".getBytes(), "Post Text2", "Tag2 Tag3", 5)
        );

        var founded = postRepository.findAll("", 1, 5);

        assertNotNull(founded);
        assertEquals(2, founded.size());
        assertArrayEquals(expected.toArray(), founded.toArray());
    }

    @Test
    void findAll_shouldReturnAllPostsByTag() throws Exception {
        var expected = List.of(
                new Post(1L, "Post 1", "Post Image Content One".getBytes(), "Post Text1", "Tag1 Tag2", 10),
                new Post(2L, "Post 2", "Post Image Content Two".getBytes(), "Post Text2", "Tag2 Tag3", 5)
        );

        var founded = postRepository.findAll("Tag1", 1, 5);

        assertNotNull(founded);
        assertEquals(1, founded.size());
        assertEquals(expected.get(0), founded.get(0));
    }

    @Test
    void savePost_shouldAddPostToDb() throws Exception {
        var title = "Added Post Title";
        var image = "Added Post Image".getBytes();
        var tags = "added post";
        var text = "Added Post Text";

        var postId = postRepository.savePost(title, image, tags, text);

        var founded = postRepository.findById(postId);

        assertNotNull(founded);
        assertEquals(title, founded.getTitle());
        assertArrayEquals(image, founded.getImage());
        assertEquals(tags, founded.getTagsAsText());
        assertEquals(text, founded.getText());
        assertEquals(0, founded.getLikesCount());
    }

    @Test
    void updatePost_shouldUpdatePostInDb() throws Exception {
        var postId = 1L;
        var title = "Updated Post Title";
        var image = "Updated Post Image".getBytes();
        var tags = "updated post";
        var text = "updated Post Text";
        var likesCount = 100;
        var post = new Post(postId, title, image, text, tags, likesCount);

        postRepository.updatePost(post);

        var founded = postRepository.findById(postId);

        assertNotNull(founded);
        assertEquals(post, founded);
    }

    @Test
    void getPostCount_shouldReturnSavedPostsCount() throws Exception {
        var expectedAllCount = 2;
        assertEquals(expectedAllCount, postRepository.getPostCount(""));

        var expectedTag1Count = 1;
        assertEquals(expectedTag1Count, postRepository.getPostCount("Tag1"));
    }

    @Test
    void delete_shouldDeletePostFromDb() throws Exception {
        var postId = 1L;

        postRepository.delete(postId);

        Assertions.assertThrows(Exception.class, () -> postRepository.findById(postId));
    }

}