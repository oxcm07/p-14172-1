package com.back.domain.member.member.service;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
import com.back.global.globalExceptionHandler.MemberDuplicateUsernameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원가입")
    void t1() {
        given(memberRepository.existsByUsername("user1"))
                .willReturn(false);
        given(memberRepository.save(any(Member.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Member member = memberService.join("user1", "password1", "홍길동");

        assertThat(member.getUsername()).isEqualTo("user1");
        assertThat(member.getPassword()).isEqualTo("password1");
        assertThat(member.getName()).isEqualTo("홍길동");
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입, 중복 username")
    void t2() {
        given(memberRepository.existsByUsername("user1"))
                .willReturn(true);

        assertThatThrownBy(() -> memberService.join("user1", "password1", "홍길동"))
                .isInstanceOf(MemberDuplicateUsernameException.class)
                .hasMessage("user1(은)는 이미 사용중인 username 입니다.");
    }
}
