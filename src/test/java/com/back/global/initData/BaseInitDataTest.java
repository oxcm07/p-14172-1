package com.back.global.initData;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.repository.PostRepository;
import com.back.domain.post.postComment.entity.PostComment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class BaseInitDataTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("샘플 데이터 생성 시 회원 6명을 만든다")
    void t1() {
        Map<String, Member> membersByUsername = memberRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Member::getUsername, Function.identity()));

        assertThat(membersByUsername).hasSize(6);
        assertThat(membersByUsername).containsOnlyKeys(
                "system",
                "admin",
                "user1",
                "user2",
                "user3",
                "user4"
        );

        assertMember(membersByUsername.get("system"), "12345678", "시스템");
        assertMember(membersByUsername.get("admin"), "12345678", "관리자");
        assertMember(membersByUsername.get("user1"), "12345678", "유저1");
        assertMember(membersByUsername.get("user2"), "12345678", "유저2");
        assertMember(membersByUsername.get("user3"), "12345678", "유저3");
        assertMember(membersByUsername.get("user4"), "12345678", "유저4");
    }

    private void assertMember(Member member, String password, String name) {
        assertThat(member).isNotNull();
        assertThat(member.getPassword()).isEqualTo(password);
        assertThat(member.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("샘플 데이터 생성 시 글 3개의 작성자를 지정한다")
    void t2() {
        Map<Integer, Post> postsById = postRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Post::getId, Function.identity()));

        assertThat(postsById).hasSize(3);
        assertThat(postsById.get(1).getAuthor()).isEqualTo("user1");
        assertThat(postsById.get(2).getAuthor()).isEqualTo("user2");
        assertThat(postsById.get(3).getAuthor()).isEqualTo("user3");
    }

    @Test
    @DisplayName("샘플 데이터 생성 시 댓글 5개의 작성자를 지정한다")
    void t3() {
        Map<Integer, PostComment> commentsById = postRepository.findAll()
                .stream()
                .map(Post::getComments)
                .flatMap(List::stream)
                .collect(Collectors.toMap(PostComment::getId, Function.identity()));

        assertThat(commentsById).hasSize(5);
        assertCommentAuthor(commentsById.get(1), "user1");
        assertCommentAuthor(commentsById.get(2), "user2");
        assertCommentAuthor(commentsById.get(3), "user3");
        assertCommentAuthor(commentsById.get(4), "user1");
        assertCommentAuthor(commentsById.get(5), "user2");
    }

    private void assertCommentAuthor(PostComment postComment, String author) {
        assertThat(postComment).isNotNull();
        assertThat(ReflectionTestUtils.getField(postComment, "author")).isEqualTo(author);
    }
}
