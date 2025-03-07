package ru.yandex.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.practicum.config.DataSourceConfig;
import ru.yandex.practicum.model.Comment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {DataSourceConfig.class, CommentRepository.class})
@TestPropertySource(locations = "classpath:application-test.properties")
class CommentRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void findAllCommentsByPostId_shouldReturnAllCommentsByPostId() throws Exception {
        var expected = List.of(
                new Comment(1L, 1L, "Comment 1"),
                new Comment(2L, 1L, "Comment 2")
        );
        var postId = 1L;

        var founded = commentRepository.findAllCommentsByPostId(postId);

        assertNotNull(founded);
        assertEquals(expected.size(), founded.size());
        assertArrayEquals(expected.toArray(), founded.toArray());
    }

    @Test
    void deleteCommentsByPostId_shouldRemoveAllCommentsByPostId() throws Exception {
        var postId = 2L;

        commentRepository.deleteCommentsByPostId(postId);

        var founded = commentRepository.findAllCommentsByPostId(postId);

        assertNotNull(founded);
        assertEquals(0, founded.size());
    }

    @Test
    void addComment_shouldAddCommentToDb() throws Exception {
        var postId = 1L;
        var commentText = "Added comment";

        commentRepository.addComment(postId, commentText);

        var foundedComment = commentRepository.findAllCommentsByPostId(postId).stream()
                .filter(c -> commentText.equals(c.getText()))
                .findFirst()
                .orElse(null);

        assertNotNull(foundedComment);
    }

    @Test
    void deleteCommentById_shouldDeleteComment() throws Exception {
        var commentId = 3L;

        commentRepository.deleteCommentById(commentId);

        var count = jdbcTemplate.queryForObject("select count(1) from comment where id=" + commentId, Integer.class);

        assertEquals(0, count);
    }

    @Test
    void updateComment_shouldUpdateComment() throws Exception {
        var commentId = 2L;
        var expected = "Updated comment";

        commentRepository.updateComment(commentId, expected);

        var founded = jdbcTemplate.query("select id, post_id, text from comment where id=?",
                (rs, rowNum) -> new Comment(
                        rs.getLong("id"),
                        rs.getLong("post_id"),
                        rs.getString("text")
                ), commentId).get(0);

        assertNotNull(founded);
        assertEquals(commentId, founded.getId());
        assertEquals(expected, founded.getText());
    }

}