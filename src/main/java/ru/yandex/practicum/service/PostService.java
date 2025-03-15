package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.repository.PostRepository;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<Post> findAllPosts(String search, int pageNumber, int pageSize) {
        List<Post> posts;
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        if (search.isEmpty()) {
            posts = postRepository.findAll(pageable).toList();
        } else {
            posts = postRepository.findAllByTagsContaining(search, pageable).toList();
        }
        return posts;
    }

    public void savePost(String title, MultipartFile image, String tags, String text) throws IOException {
        var post = new Post();
        post.setTitle(title);
        post.setImage(image.getBytes());
        post.setTags(tags);
        post.setText(text);
        postRepository.save(post);
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    public long getPostCount(String search) {
        long count;
        if (search.isEmpty()) {
            count = postRepository.count();
        } else {
            count = postRepository.getCountByTagsLike(search);
        }
        return count;
    }

    public void changePostLikesCount(Long postId, boolean like) throws Exception {
        var post = postRepository.findById(postId).orElseThrow();
        post.setLikesCount(like ? post.getLikesCount() + 1 : post.getLikesCount() - 1);
        postRepository.save(post);
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    public void updatePost(Long postId, String title, MultipartFile image, String tags, String text) throws Exception {
        var post = postRepository.findById(postId).orElseThrow();
        post.setTitle(title);
        post.setImage(image.getBytes());
        post.setTags(tags);
        post.setText(text);
        postRepository.save(post);
    }

}
