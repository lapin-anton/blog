package ru.yandex.practicum.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private byte[] image;

    private String text;

    private String tags;

    private Integer likesCount;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

}
