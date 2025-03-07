package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllPosts_shouldReturnPosts() {
        String search = "test";
        int pageNumber = 1;
        int pageSize = 10;
        List<Post> expectedPosts = Arrays.asList(
                new Post(1L, "Title 1", null, "Text 1", "tag1 tag2", 5),
                new Post(2L, "Title 2", null, "Text 2", "tag3 tag4", 10)
        );
        when(postRepository.findAll(search, pageNumber, pageSize)).thenReturn(expectedPosts);
        List<Post> actualPosts = postService.findAllPosts(search, pageNumber, pageSize);
        assertEquals(expectedPosts, actualPosts);
        verify(postRepository, times(1)).findAll(search, pageNumber, pageSize);
    }

    @Test
    void savePost_shouldSaveNewPost() throws IOException {
        String title = "New Post";
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "image-content".getBytes());
        String tags = "tag1 tag2";
        String text = "This is a test post.";
        postService.savePost(title, image, tags, text);
        verify(postRepository, times(1)).savePost(eq(title), eq(image.getBytes()), eq(tags), eq(text));
    }

    @Test
    void findById_shouldReturnPost() {
        Long postId = 1L;
        Post expectedPost = new Post(postId, "Title", null, "Text", "tag1 tag2", 5);
        when(postRepository.findById(postId)).thenReturn(expectedPost);

        Post actualPost = postService.findById(postId);

        assertEquals(expectedPost, actualPost);
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void getPostCount_shouldReturnCount() {
        String search = "test";
        int expectedCount = 42;
        when(postRepository.getPostCount(search)).thenReturn(expectedCount);

        int actualCount = postService.getPostCount(search);

        assertEquals(expectedCount, actualCount);
        verify(postRepository, times(1)).getPostCount(search);
    }

    @Test
    void changePostLikesCount_shouldIncreaseLikes() {
        Long postId = 1L;
        Post post = new Post(postId, "Title", null, "Text", "tag1 tag2", 5);
        when(postRepository.findById(postId)).thenReturn(post);

        postService.changePostLikesCount(postId, true);

        assertEquals(6, post.getLikesCount());
        verify(postRepository, times(1)).updatePost(post);
    }

    @Test
    void changePostLikesCount_shouldDecreaseLikes() {
        Long postId = 1L;
        Post post = new Post(postId, "Title", null, "Text", "tag1 tag2", 5);
        when(postRepository.findById(postId)).thenReturn(post);

        postService.changePostLikesCount(postId, false);

        assertEquals(4, post.getLikesCount());
        verify(postRepository, times(1)).updatePost(post);
    }

    @Test
    void deletePost_shouldCallRepositoryDelete() {
        Long postId = 1L;
        postService.deletePost(postId);
        verify(postRepository, times(1)).delete(postId);
    }

    @Test
    void updatePost_shouldUpdateExistingPost() throws IOException {
        Long postId = 1L;
        String newTitle = "Updated Title";
        MockMultipartFile newImage = new MockMultipartFile("image", "newImage.jpg", "image/jpeg", "new-image-content".getBytes());
        String newTags = "newTag1 newTag2";
        String newText = "Updated text.";

        Post existingPost = new Post(postId, "Old Title", null, "Old Text", "oldTag1 oldTag2", 10);

        when(postRepository.findById(postId)).thenReturn(existingPost);

        postService.updatePost(postId, newTitle, newImage, newTags, newText);

        assertEquals(newTitle, existingPost.getTitle());
        assertArrayEquals(newImage.getBytes(), existingPost.getImage());
        assertEquals(newTags, existingPost.getTagsAsText());
        assertEquals(newText, existingPost.getText());

        verify(postRepository, times(1)).updatePost(existingPost);
    }

}