package com.back.global.initData;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {
    @Autowired
    @Lazy
    private BaseInitData self;
    private final PostService postService;
    @Autowired
    private MemberService memberService;

    @Bean
    ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.work1();
            self.work2();
        };
    }

    @Transactional
    public void work1(){
        if(memberService.count() > 0) return;

        Member member1 = memberService.join("system", "12345678", "시스템");
        Member member2 = memberService.join("admin", "12345678", "관리자");
        Member member3 = memberService.join("user1", "12345678", "유저1");
        Member member4 = memberService.join("user2", "12345678", "유저2");
        Member member5 = memberService.join("user3", "12345678", "유저3");
        Member member6 = memberService.join("user4", "12345678", "유저4");
    }

    @Transactional
    public void work2() {
        if (postService.count() > 0) return;

        Post post1 = postService.write("제목 1", "내용 1");
        Post post2 = postService.write("제목 2", "내용 2");
        Post post3 = postService.write("제목 3", "내용 3");

        post1.addComment("댓글 1-1");
        post1.addComment("댓글 1-2");
        post1.addComment("댓글 1-3");
        post2.addComment("댓글 2-1");
        post2.addComment("댓글 2-2");
    }
}