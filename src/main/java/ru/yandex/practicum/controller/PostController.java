package ru.yandex.practicum.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.model.Paging;
import ru.yandex.practicum.model.dto.PostDto;
import ru.yandex.practicum.service.CommentService;
import ru.yandex.practicum.service.PostService;

import java.io.IOException;
import java.util.Base64;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private final CommentService commentService;

    @GetMapping("/")
    public String showPosts(Model model,
                            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                            @RequestParam(value = "search", defaultValue = "") String search) {
        var postCount = postService.getPostCount(search);
        var paging = new Paging(postCount, pageNumber, pageSize);
        var posts = postService.findAllPosts(search, pageNumber, pageSize);
        var postDtos = posts.stream().map(PostDto::new).toList();
        model.addAttribute("posts", postDtos);
        model.addAttribute("paging", paging);
        model.addAttribute("search", search);
        return "posts";
    }

    @GetMapping("/add")
    public String addPost() {
        return "add-post";
    }

    @PostMapping("/savePost")
    public String savePost(
            @RequestParam("title") String title,
            @RequestParam(value = "image") MultipartFile image,
            @RequestParam("tags") String tags,
            @RequestParam("text") String text) throws IOException {
        postService.savePost(title, image, tags, text);
        return "redirect:/";
    }

    @GetMapping("/images/{postId}")
    public ResponseEntity<Resource> downloadImage(@PathVariable("postId") Long postId) throws Exception {
        var post = postService.findById(postId);
        var image = Base64.getDecoder().decode(post.getImage());
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .contentLength(image.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new ByteArrayResource(image));
    }

    @GetMapping("/{postId}")
    public String showPost(Model model, @PathVariable("postId") Long postId) throws Exception {
        var post = postService.findById(postId);
        var postDto = new PostDto(post);
        model.addAttribute("post", postDto);
        return "post";
    }

    @PostMapping("/{postId}/like")
    public String changeRating(@PathVariable("postId") Long postId, @RequestParam("like") boolean like) throws Exception {
        postService.changePostLikesCount(postId, like);
        return "redirect:/" + postId;
    }

    @PostMapping("/{postId}/delete")
    @Transactional
    public String deletePost(@PathVariable("postId") Long postId) throws Exception {
        var post = postService.findById(postId);
        commentService.deleteCommentsByPostId(post);
        postService.deletePost(postId);
        return "redirect:/";
    }

    @GetMapping("/{postId}/edit")
    public String editPost(Model model, @PathVariable("postId") Long postId) throws Exception {
        var post = postService.findById(postId);
        var postDto = new PostDto(post);
        model.addAttribute("post", postDto);
        return "add-post";
    }

    @PostMapping("/{postId}")
    public String updatePost(
            @PathVariable("postId") Long postId,
            @RequestParam("title") String title,
            @RequestParam("image") MultipartFile image,
            @RequestParam("tags") String tags,
            @RequestParam("text") String text
            ) throws Exception {
        postService.updatePost(postId, title, image, tags, text);
        return "redirect:/" + postId;
    }

    @PostMapping("/{postId}/comments")
    public String addComment(@PathVariable("postId") Long postId, @RequestParam("text") String text) throws Exception {
        var post = postService.findById(postId);
        commentService.addCommentToPost(post, text);
        return "redirect:/" + postId;
    }

    @PostMapping("/{postId}/comments/{commentId}")
    public String updateComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @RequestParam("text") String text) throws Exception {
        commentService.updateComment(commentId, text);
        return "redirect:/" + postId;
    }

    @PostMapping("/{postId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId) {
        commentService.deleteCommentFromPost(commentId);
        return "redirect:/" + postId;
    }

}
