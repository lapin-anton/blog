package ru.yandex.practicum.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode
@Entity
@Table(name = "post")
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String image;

    private String text;

    private String tags;

    private Integer likesCount;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    public Post(String title, String image, String text, String tags, Integer likesCount) {
        this.title = title;
        this.image = image;
        this.text = text;
        this.tags = tags;
        this.likesCount = likesCount;
    }
}
