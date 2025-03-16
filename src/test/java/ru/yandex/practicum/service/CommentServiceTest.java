package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

import ru.yandex.practicum.model.entity.Comment;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.repository.CommentRepository;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addCommentToPost_shouldCallRepository() {
        var post = new Post();
        String text = "Test comment";
        var comment = new Comment();
        comment.setPost(post);
        comment.setText(text);

        commentService.addCommentToPost(post, text);

        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void deleteCommentFromPost_shouldCallRepository() {
        Long commentId = 1L;
        commentService.deleteCommentFromPost(commentId);
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void updateComment_shouldCallRepository() throws Exception {
        Long commentId = 1L;
        String newText = "Updated comment";
        var comment = new Comment();
        comment.setId(commentId);
        comment.setPost(new Post());
        comment.setText(newText);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.updateComment(commentId, newText);

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void deleteCommentsByPost_shouldCallRepository() {
        var post = new Post();
        commentService.deleteCommentsByPost(post);
        verify(commentRepository, times(1)).deleteAllByPost(post);
    }

}