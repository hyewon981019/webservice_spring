package com.example.webservice_spring.domain.posts;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;
    //빈 끌어오기;

    @After
    public void cleanup()
    {
        postsRepository.deleteAll();
    }

    @Test
    public void 게시글저장_불러오기()
    {
        //given
        String title = "test title";
        String content = "test content";

        //테스트 엔티티 저장
        postsRepository.save(Posts.builder()
        .title(title)
        .content(content)
        .author("test@test.com")
        .build());

        //when
        List<Posts> postsList = postsRepository.findAll(); //전체 조회 상황

        //then
        //기대 결과
        Posts posts = postsList.get(0);//첫번째 게시물
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content); //같은지 확인

    }

    @Test
    public void BaseTimeEntity_등록()
    {
        //given - 엔티티 만들고 등록(save)
        LocalDateTime now = LocalDateTime.of(2020, 6, 4,0, 0, 0);
        postsRepository.save(Posts.builder()
        .title("title")
        .content("content")
        .author("author")
        .build());

        //when - 전체 목록 조회할때
        List<Posts> postsList = postsRepository.findAll();

        //then
        Posts posts = postsList.get(0);

        System.out.println(">>>>>> createDate =" + posts.getCreateDate() + ", modifiedDate =" + posts.getModifiedDate());

        assertThat(posts.getCreateDate()).isAfter(now);
        assertThat(posts.getModifiedDate()).isAfter(now);

    }
}
