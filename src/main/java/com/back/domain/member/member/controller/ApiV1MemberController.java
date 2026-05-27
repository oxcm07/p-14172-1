package com.back.domain.member.member.controller;

import com.back.domain.member.member.service.MemberService;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "ApiV1MemberController", description = "API 회원 컨트롤러")
public class ApiV1MemberController {
    private final MemberService memberService;

    public record JoinReqBody(
            @Size(min = 4, max = 30)
            String username,
            @Size(min = 8, max = 30)
            String password,
            @Size(min = 2, max = 30)
            String name
    ) {
    }

    @PostMapping("/join")
    @ResponseStatus(CREATED)
    @Transactional
    @Operation(summary = "회원가입")
    public RsData<Void> join(
            @RequestBody @Valid JoinReqBody reqBody
    ) {
        memberService.join(reqBody.username, reqBody.password, reqBody.name);

        return new RsData<>(
                "201-1",
                "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(reqBody.name)
        );
    }
}
