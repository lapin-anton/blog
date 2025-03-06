package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.repository.CommentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public void addCommentToPost(Long postId, String text) {
        commentRepository.addComment(postId, text);
    }

    public void deleteCommentFromPost(Long commentId) {
        commentRepository.deleteCommentById(commentId);
    }

    public void updateComment(Long commentId, String text) {
        commentRepository.updateComment(commentId, text);
    }

    public List<Comment> findAllCommentsByPostId(Long postId) {
        return commentRepository.findAllCommentsByPostId(postId);
    }

    public void deleteCommentsByPostId(Long postId) {
        commentRepository.deleteCommentsByPostId(postId);
    }
}
