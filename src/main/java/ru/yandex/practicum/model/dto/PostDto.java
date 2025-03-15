package ru.yandex.practicum.model.dto;

import lombok.Data;
import lombok.Getter;
import ru.yandex.practicum.model.entity.Post;

import java.util.Arrays;
import java.util.List;

@Data
public class PostDto {

    public PostDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.image = post.getImage();
        this.text = post.getText();
        this.textPreview = text != null && text.length() > 300 ? text.substring(0, 300) + "..." : text;
        this.textParts = text == null ? List.of() : Arrays.stream(text.split("\\n")).toList();
        this.tagsAsText = post.getTags();
        this.tags = tagsAsText == null ? List.of() : Arrays.stream(tagsAsText.split("\\s")).toList();
        this.likesCount = post.getLikesCount();
        this.comments = post.getComments().stream().map(CommentDto::new).toList();
    }
    private Long id;
    private String title;
    private byte[] image;
    private String text;
    private String textPreview;
    private List<String> textParts;
    private List<String> tags;
    private String tagsAsText;
    private Integer likesCount;
    private List<CommentDto> comments;

}
