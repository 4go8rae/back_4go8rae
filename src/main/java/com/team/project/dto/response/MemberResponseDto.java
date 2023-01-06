package com.team.project.dto.response;

import com.team.project.domain.Member;
import com.team.project.domain.MemberRoleEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponseDto {
    private Long id;
    private String nickname;
    private String username;
    private MemberRoleEnum role;

    public static MemberResponseDto of(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .username(member.getUsername())
                .role(member.getRole())
                .build();
    }
}
