package com.team.project.controller;

import com.team.project.dto.request.LoginRequestDto;
import com.team.project.dto.request.MemberOutDto;
import com.team.project.dto.request.NicknameDto;
import com.team.project.dto.request.SignupRequestDto;
import com.team.project.dto.response.MemberResponseDto;
import com.team.project.jwt.UserDetailsImpl;
import com.team.project.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    
    //회원가입
    @PostMapping("/api/member/signup")
    public ResponseEntity<MemberResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        return memberService.signup(requestDto);
    }

    //로그인
    @PostMapping("/api/member/login")
    public ResponseEntity<MemberResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        return memberService.login(requestDto);
    }

    //로그아웃
    @PostMapping("/api/auth/member/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @RequestHeader(value = "RefreshToken") String refreshToken){
        return memberService.logout(userDetails.getMember().getId(),refreshToken);
    }

    //닉네임 변경
    @PutMapping("/api/auth/member/updatenickname")
    public ResponseEntity<MemberResponseDto> updateNickname(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                 @RequestBody NicknameDto nicknameDto) {
        return memberService.updateNickname(userDetails.getMember(), nicknameDto);
    }

    //비밀번호 변경
    @PutMapping("/api/auth/member/updatepassword")
    public ResponseEntity<String> updatePassword(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @RequestBody NicknameDto nicknameDto) {
        return memberService.updatePassword(userDetails.getMember(), nicknameDto);
    }

    // 만료된 access token 재 발급
    @PostMapping("/api/member/retoken")
    public ResponseEntity<String> reToken(
            @RequestHeader(value = "RefreshToken") String refreshToken) {

        return memberService.reToken(refreshToken);
    }

    @PostMapping("/api/auth/member/memberout")
    public ResponseEntity<String> memberout(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestBody MemberOutDto memberOutDto){
        return memberService.memberout(userDetails.getMember(), memberOutDto);
    }

}
