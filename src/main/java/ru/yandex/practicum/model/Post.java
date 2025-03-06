package ru.yandex.practicum.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class Post {

    public Post (Long id, String title, byte[] image, String text, String tags, Integer likesCount) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.text = text;
        this.textPreview = text != null && text.length() > 300 ? text.substring(0, 300) + "..." : text;
        this.textParts = text == null ? List.of() : Arrays.stream(text.split("\\n")).toList();
        this.tags = tags == null ? List.of() : Arrays.stream(tags.split("\\s")).toList();
        this.likesCount = likesCount;
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
    private List<Comment> comments;

}
