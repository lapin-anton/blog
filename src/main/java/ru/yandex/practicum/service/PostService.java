package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostRepository;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<Post> findAllPosts(String search, int pageNumber, int pageSize) {
        return postRepository.findAll(search, pageNumber, pageSize);
    }

    public void savePost(String title, MultipartFile image, String tags, String text) throws IOException {
        postRepository.savePost(title, image.getBytes(), tags, text);
    }

    public Post findById(Long postId) throws Exception {
        return postRepository.findById(postId);
    }

    public int getPostCount(String search) {
        return postRepository.getPostCount(search);
    }

    public void changePostLikesCount(Long postId, boolean like) throws Exception {
        var post = postRepository.findById(postId);
        post.setLikesCount(like ? post.getLikesCount() + 1 : post.getLikesCount() - 1);
        postRepository.updatePost(post);
    }

    public void deletePost(Long postId) {
        postRepository.delete(postId);
    }

    public void updatePost(Long postId, String title, MultipartFile image, String tags, String text) throws Exception {
        var post = postRepository.findById(postId);
        post.setTitle(title);
        post.setImage(image.getBytes());
        post.setTagsAsText(tags);
        post.setText(text);
        postRepository.updatePost(post);
    }

}
