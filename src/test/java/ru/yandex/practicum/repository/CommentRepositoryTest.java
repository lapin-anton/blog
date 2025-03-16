package ru.yandex.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.yandex.practicum.model.entity.Comment;
import ru.yandex.practicum.model.entity.Post;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
    }

    @Test
    void deleteAllByPost_shouldRemoveAllCommentsByPost() throws Exception {
        var post = new Post("Post 1", "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10);
        post = postRepository.save(post);
        var comments = List.of(
                new Comment(post, "Comment 1"),
                new Comment(post, "Comment 2")
        );
        commentRepository.saveAll(comments);

        commentRepository.deleteAllByPost(post);

        var founded = commentRepository.findAllByPost(post);

        assertNotNull(founded);
        assertEquals(0, founded.size());
    }

    @Test
    void save_shouldAddCommentInDb() throws Exception {
        var post = new Post("Post 1", "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10);
        post = postRepository.save(post);
        var commentText = "Added comment";
        var comment = new Comment(post, commentText);
        commentRepository.save(comment);

        var foundedComment = commentRepository.findAllByPost(post).stream()
                .filter(c -> commentText.equals(c.getText()))
                .findFirst()
                .orElse(null);

        assertNotNull(foundedComment);
        assertEquals(commentText, foundedComment.getText());
        assertEquals(post.getId(), foundedComment.getPost().getId());
    }

    @Test
    void deleteById_shouldDeleteComment() throws Exception {
        var post = new Post("Post 1", "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10);
        post = postRepository.save(post);
        var commentText = "Added comment";
        var comment = new Comment(post, commentText);
        commentRepository.save(comment);
        var commentId = comment.getId();

        commentRepository.deleteById(commentId);

        var commentOpt = commentRepository.findById(commentId);

        assertTrue(commentOpt.isEmpty());
    }

    @Test
    void updateComment_shouldUpdateComment() throws Exception {
        var post = new Post("Post 1", "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10);
        post = postRepository.save(post);
        var commentText = "Added comment";
        var comment = new Comment(post, commentText);
        commentRepository.save(comment);
        var commentId = comment.getId();
        var expected = "Updated comment";
        comment.setText(expected);

        commentRepository.save(comment);

        var commentOpt = commentRepository.findById(commentId);

        assertTrue(commentOpt.isPresent());
        assertEquals(expected, commentOpt.get().getText());
    }

}