package com.team.project.domain;

import com.team.project.dto.request.NicknameDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String nickname;

    @Column
    @Enumerated(value = EnumType.STRING)
    private MemberRoleEnum role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Product> product;

    public void updateNickname(NicknameDto nicknameDto){
        this.nickname = nicknameDto.getNickname();
    }

    public void updatePassword(String newPassword){
        this.password = newPassword;
    }

    public void OutMember(){
        this.username = null;
        this.password = null;
        this.role = null;
    }

}
