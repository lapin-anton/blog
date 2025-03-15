package ru.yandex.practicum.model.dto;

import lombok.Data;
import ru.yandex.practicum.model.entity.Comment;

@Data
public class CommentDto {

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.postId = comment.getPost().getId();
        this.text = comment.getText();
    }

    private Long id;
    private Long postId;
    private String text;

}
