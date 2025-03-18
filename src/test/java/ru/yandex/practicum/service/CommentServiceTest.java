package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.repository.CommentRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        Long postId = 1L;
        String text = "Test comment";
        commentService.addCommentToPost(postId, text);
        verify(commentRepository, times(1)).addComment(postId, text);
    }

    @Test
    void deleteCommentFromPost_shouldCallRepository() {
        Long commentId = 1L;
        commentService.deleteCommentFromPost(commentId);
        verify(commentRepository, times(1)).deleteCommentById(commentId);
    }

    @Test
    void updateComment_shouldCallRepository() {
        Long commentId = 1L;
        String newText = "Updated comment";
        commentService.updateComment(commentId, newText);
        verify(commentRepository, times(1)).updateComment(commentId, newText);
    }

    @Test
    void findAllCommentsByPostId_shouldReturnListOfComments() {
        Long postId = 1L;
        List<Comment> expectedComments = Arrays.asList(
                new Comment(1L, 1L, "Comment 1"),
                new Comment(2L, 1L, "Comment 2")
        );
        when(commentRepository.findAllCommentsByPostId(postId)).thenReturn(expectedComments);
        List<Comment> actualComments = commentService.findAllCommentsByPostId(postId);
        assertEquals(expectedComments, actualComments);
        verify(commentRepository, times(1)).findAllCommentsByPostId(postId);
    }

    @Test
    void deleteCommentsByPostId_shouldCallRepository() {
        Long postId = 1L;
        commentService.deleteCommentsByPostId(postId);
        verify(commentRepository, times(1)).deleteCommentsByPostId(postId);
    }

}