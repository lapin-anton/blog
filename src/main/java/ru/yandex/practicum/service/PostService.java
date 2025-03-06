package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    public List<Post> findAllPosts(String search, int pageNumber, int pageSize) {
        var posts = postRepository.findAll(search, pageNumber, pageSize);
        posts.forEach(p -> p.setComments(commentRepository.findAllCommentsByPostId(p.getId())));
        return posts;
    }


    public void savePost(String title, MultipartFile image, String tags, String text) throws IOException {
        postRepository.saveNewPost(title, image.getBytes(), tags, text);
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId);
    }

    public int getPostCount(String search) {
        return postRepository.getPostCount(search);
    }
}
