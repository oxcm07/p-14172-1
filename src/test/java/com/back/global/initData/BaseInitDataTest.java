package com.back.global.initData;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

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
}
