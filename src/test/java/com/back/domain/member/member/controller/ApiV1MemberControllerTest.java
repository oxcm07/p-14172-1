package com.back.domain.member.member.controller;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.global.aspect.ResponseAspect;
import com.back.global.globalExceptionHandler.GlobalExceptionHandler;
import com.back.global.globalExceptionHandler.MemberDuplicateUsernameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiV1MemberController.class)
@Import({GlobalExceptionHandler.class, ResponseAspect.class})
class ApiV1MemberControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("회원가입")
    void t1() throws Exception {
        given(memberService.join("user1", "password1", "홍길동"))
                .willReturn(new Member("user1", "password1", "홍길동"));

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "password1",
                                            "name": "홍길동"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("홍길동님 환영합니다. 회원가입이 완료되었습니다."))
                .andExpect(jsonPath("$.data").value(nullValue()));

        verify(memberService).join("user1", "password1", "홍길동");
    }

    @Test
    @DisplayName("회원가입, 중복 username")
    void t2() throws Exception {
        given(memberService.join("user1", "password1", "홍길동"))
                .willThrow(new MemberDuplicateUsernameException("user1"));

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "password1",
                                            "name": "홍길동"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value("409-1"))
                .andExpect(jsonPath("$.msg").value("user1(은)는 이미 사용중인 username 입니다."))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    @DisplayName("회원가입, username 길이 검증")
    void t3() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "abc",
                                            "password": "password1",
                                            "name": "홍길동"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("username-Size-size must be between 4 and 30"));
    }

    @Test
    @DisplayName("회원가입, password 길이 검증")
    void t4() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "1234567",
                                            "name": "홍길동"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("password-Size-size must be between 8 and 30"));
    }

    @Test
    @DisplayName("회원가입, name 길이 검증")
    void t5() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "password1",
                                            "name": "김"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("name-Size-size must be between 2 and 30"));
    }

    @Test
    @DisplayName("내 정보")
    void t6() throws Exception {
        Member member = new Member("user1", "password1", "홍길동");
        ReflectionTestUtils.setField(member, "id", 1);
        ReflectionTestUtils.setField(member, "createDate", LocalDateTime.of(2026, 5, 27, 13, 50, 0));
        ReflectionTestUtils.setField(member, "modifyDate", LocalDateTime.of(2026, 5, 27, 13, 55, 0));

        given(memberService.findById(1))
                .willReturn(Optional.of(member));

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/members/me")
                                .param("actorId", "1")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.createDate").value(startsWith("2026-05-27T13:50")))
                .andExpect(jsonPath("$.updateDate").value(startsWith("2026-05-27T13:55")))
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(memberService).findById(1);
    }

    @Test
    @DisplayName("내 정보, actorId 파라미터 없음")
    void t7() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/members/me")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("401-1"))
                .andExpect(jsonPath("$.msg").value("로그인 후 이용해주세요."))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }
}
