package com.vbs.Jobshop.repository;

import com.vbs.Jobshop.model.PostModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostModel, Long> {
    
}
