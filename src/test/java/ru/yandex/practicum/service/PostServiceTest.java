package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.repository.PostRepository;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        var posts = List.of(
                new Post("Post 1", "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10),
                new Post("Post 2", "+xMdSf3cjVcMebeAedfpnw==", "Post Text2", "Tag2 Tag3", 5)
        );
        Page<Post> postPage = new PageImpl<>(posts);
        Page<Post> postsBySearch = new PageImpl<>(List.of(posts.get(0)));
        when(postRepository.findAll(PageRequest.of(pageNumber - 1, pageSize))).thenReturn(postPage);
        when(postRepository.findAllByTagsContaining(search, PageRequest.of(pageNumber - 1, pageSize)))
                .thenReturn(postsBySearch);

        assertEquals(posts, postService.findAllPosts("", pageNumber, pageSize));
        verify(postRepository, times(1)).findAll(PageRequest.of(pageNumber - 1, pageSize));

        assertEquals(List.of(posts.get(0)), postService.findAllPosts(search, pageNumber, pageSize));
        verify(postRepository, times(1)).findAllByTagsContaining(search, PageRequest.of(pageNumber - 1, pageSize));
    }

    @Test
    void savePost_shouldSaveNewPost() throws IOException {
        String title = "New Post";
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "image-content".getBytes());
        String tags = "tag1 tag2";
        String text = "This is a test post.";
        var post = new Post();
        post.setTitle(title);
        if (image != null) {
            post.setImage(Base64.getEncoder().encodeToString(image.getBytes()));
        }
        post.setTags(tags);
        post.setText(text);
        post.setLikesCount(0);

        postService.savePost(title, image, tags, text);

        verify(postRepository, times(1)).save(post);
    }

    @Test
    void findById_shouldReturnPost() throws Exception {
        Long postId = 1L;
        var expectedPost = new Post("Title", null, "Text", "tag1 tag2", 5);
        when(postRepository.findById(postId)).thenReturn(Optional.of(expectedPost));

        var actualPost = postService.findById(postId);

        assertEquals(expectedPost, actualPost);
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void getPostCount_shouldReturnCount() {
        String search = "test";
        long expectedAllCount = 42;
        long expectedSearchCount = 15;
        when(postRepository.count()).thenReturn(expectedAllCount);
        when(postRepository.getCountByTagsLike(search)).thenReturn(expectedSearchCount);

        long actualAllCount = postService.getPostCount("");

        assertEquals(expectedAllCount, actualAllCount);
        verify(postRepository, times(1)).count();

        long actualSearchCount = postService.getPostCount(search);

        assertEquals(expectedSearchCount, actualSearchCount);
        verify(postRepository, times(1)).getCountByTagsLike(search);
    }

    @Test
    void changePostLikesCount_shouldIncreaseLikes() throws Exception {
        Long postId = 1L;
        Post post = new Post("Title", null, "Text", "tag1 tag2", 5);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.changePostLikesCount(postId, true);

        assertEquals(6, post.getLikesCount());
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void changePostLikesCount_shouldDecreaseLikes() throws Exception {
        Long postId = 1L;
        Post post = new Post("Title", null, "Text", "tag1 tag2", 5);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.changePostLikesCount(postId, false);

        assertEquals(4, post.getLikesCount());
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void deletePost_shouldCallRepositoryDelete() {
        Long postId = 1L;

        postService.deletePost(postId);

        verify(postRepository, times(1)).deleteById(postId);
    }

    @Test
    void updatePost_shouldUpdateExistingPost() throws Exception {
        Long postId = 1L;
        String newTitle = "Updated Title";
        MockMultipartFile newImage = new MockMultipartFile("image", "newImage.jpg", "image/jpeg", "new-image-content".getBytes());
        String newTags = "newTag1 newTag2";
        String newText = "Updated text.";

        Post existingPost = new Post("Old Title", null, "Old Text", "oldTag1 oldTag2", 10);

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        postService.updatePost(postId, newTitle, newImage, newTags, newText);

        assertEquals(newTitle, existingPost.getTitle());
        assertArrayEquals(newImage.getBytes(), Base64.getDecoder().decode(existingPost.getImage()));
        assertEquals(newTags, existingPost.getTags());
        assertEquals(newText, existingPost.getText());

        verify(postRepository, times(1)).save(existingPost);
    }

}