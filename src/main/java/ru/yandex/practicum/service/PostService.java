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
        var post = postRepository.findById(postId);
        post.setComments(commentRepository.findAllCommentsByPostId(postId));
        return post;
    }

    public int getPostCount(String search) {
        return postRepository.getPostCount(search);
    }

    public void changePostLikesCount(Long postId, boolean like) {
        var post = postRepository.findById(postId);
        post.setLikesCount(like ? post.getLikesCount() + 1 : post.getLikesCount() - 1);
        postRepository.updatePost(post);
    }

    public void deletePost(Long postId) {
        commentRepository.deleteCommentsByPostId(postId);
        postRepository.delete(postId);
    }

    public void updatePost(Long postId, String title, MultipartFile image, String tags, String text) throws IOException {
        var post = postRepository.findById(postId);
        post.setTitle(title);
        post.setImage(image.getBytes());
        post.setTagsAsText(tags);
        post.setText(text);
        postRepository.updatePost(post);
    }

    public void addCommentToPost(Long postId, String text) {
        commentRepository.addComment(postId, text);
    }
}
