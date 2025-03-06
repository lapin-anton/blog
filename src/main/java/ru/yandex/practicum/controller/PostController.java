package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.model.Paging;
import ru.yandex.practicum.service.PostService;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/")
    public String showPosts(Model model,
                            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                            @RequestParam(value = "search", defaultValue = "") String search) {
        var postCount = postService.getPostCount(search);
        var paging = new Paging(postCount, pageNumber, pageSize);
        var posts = postService.findAllPosts(search, pageNumber, pageSize);
        model.addAttribute("posts", posts);
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
            @RequestParam("image") MultipartFile image,
            @RequestParam("tags") String tags,
            @RequestParam("text") String text) throws IOException {
        postService.savePost(title, image, tags, text);
        return "redirect:/";
    }

    @GetMapping("/images/{postId}")
    public ResponseEntity<Resource> downloadPicture(@PathVariable("postId") Long postId) {
        var post = postService.findById(postId);
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .contentLength(post.getImage().length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new ByteArrayResource(post.getImage()));
    }

}
