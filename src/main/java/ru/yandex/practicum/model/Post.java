package ru.yandex.practicum.model;

import java.util.List;

public class Post {

    private Long id;
    private String title;
    private String text;
    private String textPreview;
    private List<String> textParts;
    private List<String> tags;
    private String tagsAsText;
    private Integer likesCount;
    private List<Comment> comments;

}
