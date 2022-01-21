package com.example.webservice_spring.web;

import com.example.webservice_spring.domain.posts.Posts;
import com.example.webservice_spring.domain.posts.PostsRepository;
import com.example.webservice_spring.web.dto.PostsSaveRequestDto;
import com.example.webservice_spring.web.dto.PostsUpdateRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setup()
    {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    @After
    public void tearDown() throws Exception
    {
        postsRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles="USER")
    public void Posts_등록() throws Exception
    {
        //given -  req dto 생성
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto =
                PostsSaveRequestDto.builder() //dto 생성해(리퀘스트 날리기 위함)
                .title(title)
                .content(content)
                .author("author")
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        //when - 리퀘스트를 날리고 rep를 받았을 때(어디에?)
        mvc.perform(post(url)
        .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());

//         //then - rep의 내용 확인
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);

     }

     @Test
     @WithMockUser(roles="USER")
     public void Posts_수정() throws Exception
     {
         //given

         Posts savedPosts = postsRepository.save(Posts.builder()
         .title("title")
         .content("content")
         .author("author")
         .build()); //엔티티 생성해서 집어넣기

         Long updateId = savedPosts.getId();
         String expectedTitle = "title2";
         String expectedContent = "content2";

         PostsUpdateRequestDto requestDto =
                 PostsUpdateRequestDto.builder()
                 .title(expectedTitle)
                 .content(expectedContent)
                 .build(); //수정 dto

         String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;
         HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto); //리퀘스트 규약형식으로

         //when - 리퀘스트
         mvc.perform(put(url)
                 .contentType(MediaType.APPLICATION_JSON_UTF8)
                 .content(new ObjectMapper().writeValueAsString(requestDto)))
                 .andExpect(status().isOk());


         //then
         List<Posts> all = postsRepository.findAll();
         assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
         assertThat(all.get(0).getContent()).isEqualTo(expectedContent);

     }
}
