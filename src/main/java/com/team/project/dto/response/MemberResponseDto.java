package com.team.project.dto.response;

import com.team.project.domain.MemberRoleEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponseDto {
    private String nickname;
    private String username;
    private MemberRoleEnum role;
}
