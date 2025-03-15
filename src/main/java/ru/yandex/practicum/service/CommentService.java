package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.entity.Comment;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.repository.CommentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public void addCommentToPost(Post post, String text) {
        var comment = new Comment();
        comment.setPost(post);
        comment.setText(text);
        commentRepository.save(comment);
    }

    public void deleteCommentFromPost(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public void updateComment(Long commentId, String text) throws Exception {
        var comment = commentRepository.findById(commentId).orElseThrow();
        comment.setText(text);
        commentRepository.save(comment);
    }

    public List<Comment> findAllCommentsByPostId(Post post) {
        return commentRepository.findAllByPost(post);
    }

    public void deleteCommentsByPostId(Post post) {
        commentRepository.deleteAllByPost(post);
    }
}
