package com.back.global.globalExceptionHandler;

import lombok.Getter;

@Getter
public class MemberDuplicateUsernameException extends RuntimeException {
    private final String username;

    public MemberDuplicateUsernameException(String username) {
        super("%s(은)는 이미 사용중인 username 입니다.".formatted(username));
        this.username = username;
    }
}
