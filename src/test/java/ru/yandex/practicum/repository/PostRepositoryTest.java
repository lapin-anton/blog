package ru.yandex.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.model.entity.Post;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
    }

    @Test
    void findAll_shouldReturnAllPostsByPage() throws Exception {
        var posts = List.of(
                new Post("Post 1", "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10),
                new Post("Post 2", "+xMdSf3cjVcMebeAedfpnw==", "Post Text2", "Tag2 Tag3", 5)
        );
        postRepository.saveAll(posts);
        Pageable pageable = PageRequest.of(0, 5);

        var founded = postRepository.findAll(pageable).toList();

        assertNotNull(founded);
        assertEquals(2, founded.size());
        assertArrayEquals(posts.toArray(), founded.toArray());
    }

    @Test
    void findAllByTagsContaining_shouldReturnAllPostsByTag() throws Exception {
        var posts = List.of(
                new Post("Post 1", "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10),
                new Post("Post 2", "+xMdSf3cjVcMebeAedfpnw==", "Post Text2", "Tag2 Tag3", 5)
        );
        postRepository.saveAll(posts);
        Pageable pageable = PageRequest.of(0, 5);
        var search = "Tag1";

        var founded = postRepository.findAllByTagsContaining(search, pageable).toList();

        assertNotNull(founded);
        assertEquals(1, founded.size());
        assertEquals(posts.get(0), founded.get(0));
    }

    @Test
    void savePost_shouldAddPostToDb() throws Exception {
        var title = "Added Post Title";
        var image = "z8LD8hEMeJU77Bg4sqV3yw==";
        var tags = "added post";
        var text = "Added Post Text";
        var likesCount = 13;
        var post = new Post(title, image, text, tags, likesCount);

        post = postRepository.save(post);

        var foundedOpt = postRepository.findById(post.getId());

        assertTrue(foundedOpt.isPresent());
        assertEquals(title, foundedOpt.get().getTitle());
        assertEquals(image, foundedOpt.get().getImage());
        assertEquals(tags, foundedOpt.get().getTags());
        assertEquals(text, foundedOpt.get().getText());
        assertEquals(likesCount, foundedOpt.get().getLikesCount());
    }

    @Test
    void updatePost_shouldUpdatePostInDb() throws Exception {
        var post = new Post("Post 1", "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10);
        post = postRepository.save(post);
        var title = "Updated Post Title";
        var image = "+xMdSf3cjVcMebeAedfpnw==";
        var tags = "updated post";
        var text = "updated Post Text";
        var likesCount = 100;
        post.setTitle(title);
        post.setImage(image);
        post.setTags(tags);
        post.setText(text);
        post.setLikesCount(likesCount);

        postRepository.save(post);

        var foundedOpt = postRepository.findById(post.getId());

        assertTrue(foundedOpt.isPresent());
        assertEquals(title, foundedOpt.get().getTitle());
        assertEquals(image, foundedOpt.get().getImage());
        assertEquals(tags, foundedOpt.get().getTags());
        assertEquals(text, foundedOpt.get().getText());
        assertEquals(likesCount, foundedOpt.get().getLikesCount());
    }

    @Test
    void getPostCount_shouldReturnSavedPostsCount() throws Exception {
        var posts = List.of(
                new Post("Post 1", "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10),
                new Post("Post 2", "+xMdSf3cjVcMebeAedfpnw==", "Post Text2", "Tag2 Tag3", 5)
        );
        postRepository.saveAll(posts);
        var expectedAllCount = 2L;
        assertEquals(expectedAllCount, postRepository.count());

        var expectedTag1Count = 1L;
        assertEquals(expectedTag1Count, postRepository.getCountByTagsLike("Tag1 Tag2"));
    }

    @Test
    void delete_shouldDeletePostFromDb() throws Exception {
        var post = new Post("Post 1", "z8LD8hEMeJU77Bg4sqV3yw==", "Post Text1", "Tag1 Tag2", 10);
        post = postRepository.save(post);

        postRepository.delete(post);

        var foundedOpt = postRepository.findById(post.getId());

        assertTrue(foundedOpt.isEmpty());
    }

}