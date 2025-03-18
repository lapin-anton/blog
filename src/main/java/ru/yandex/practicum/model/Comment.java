package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Comment {

    public Comment(Long postId, String text) {
        this.postId = postId;
        this.text = text;
    }

    private Long id;
    private Long postId;
    private String text;
}
