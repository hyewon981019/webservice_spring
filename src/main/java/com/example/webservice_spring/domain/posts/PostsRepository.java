package com.example.webservice_spring.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsRepository extends JpaRepository<Posts, Long> {
    //엔티티 타입, PK 타입
}
