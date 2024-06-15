package com.vbs.Jobshop.controller;

import com.vbs.Jobshop.model.PostModel;
import com.vbs.Jobshop.repository.PostRepository;
import com.vbs.Jobshop.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @GetMapping("/post")
    public List<PostModel> listaTodosPosts() {
        List<PostModel> postModelList = postRepository.findAll();
        return postModelList;
    }
    @PostMapping(value = "/post", produces = "application/json")
    public ResponseEntity<Object> anunciarPOst(@RequestBody PostModel postModel){
        PostModel postCadastrado = postService.salvarPost(postModel);
        return new ResponseEntity<>(postCadastrado, HttpStatus.CREATED);

    }

    @GetMapping("/post/{id}")
    public ResponseEntity<PostModel> getPostById(@PathVariable Long id) {
        Optional<PostModel> post = postService.postPorId(   id);
        return post.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/post/{id}/like")
    public ResponseEntity<PostModel> likePost(@PathVariable Long id) {
        try {
            PostModel likedPost = postService.likePost(id);
            return ResponseEntity.ok(likedPost);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/post/{id}/deslike")
    public ResponseEntity<ResponseEntity> deslike(@PathVariable Long id) {
        try {
            ResponseEntity likedPost = postService.deslikePost(id);
            return ResponseEntity.ok(likedPost);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
