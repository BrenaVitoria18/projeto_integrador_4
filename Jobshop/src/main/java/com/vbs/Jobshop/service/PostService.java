    package com.vbs.Jobshop.service;

    import com.vbs.Jobshop.model.PostModel;
    import com.vbs.Jobshop.repository.PostRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import java.util.Optional;

    @Service
    public class PostService {
        @Autowired
        private PostRepository postRepository;
        private static final Logger logger = LoggerFactory.getLogger(PostService.class);


        public PostModel salvarPost(PostModel postModel) {

            return postRepository.save(postModel);
        }

        public Optional<PostModel> postPorId(Long id) {
            return postRepository.findById(id);
        }
        public PostModel likePost(Long id) {
            Optional<PostModel> optionalPost = postRepository.findById(id);
            if (optionalPost.isPresent()) {
                PostModel post = optionalPost.get();
                post.setLikes(post.getLikes() + 1);
                PostModel savedPost = postRepository.save(post);
                logger.info("Post with ID {} liked successfully. Likes count: {}", savedPost.getIdPost(), savedPost.getLikes());
                return savedPost;
            } else {
                logger.error("Post with ID {} not found", id);
                throw new RuntimeException("Post not found");
            }
        }
        public ResponseEntity<PostModel> deslikePost(Long id) {
            Optional<PostModel> optionalPost = postRepository.findById(id);
            if (optionalPost.isPresent()) {
                PostModel post = optionalPost.get();
                if (post.getLikes() > 0) { // Verifica se há curtidas para descurtir
                    post.setLikes(post.getLikes() - 1);
                    PostModel savedPost = postRepository.save(post);
                    logger.info("Post with ID {} disliked successfully. Likes count: {}", savedPost.getIdPost(), savedPost.getLikes());
                    return ResponseEntity.ok(savedPost);
                } else {
                    logger.warn("Post with ID {} has no likes to remove.", id);
                    return ResponseEntity.badRequest().build(); // Retornar um erro 400 (Bad Request) se não houver curtidas para remover
                }
            } else {
                logger.error("Post with ID {} not found", id);
                return ResponseEntity.notFound().build();
            }
        }

    }
