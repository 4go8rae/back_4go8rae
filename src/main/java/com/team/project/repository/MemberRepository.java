package com.team.project.repository;

import com.team.project.domain.Member;
import com.team.project.domain.MemberRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    Optional<Member> findByNickname(String nickname);

    Boolean existsByUsername(String membername);

    Optional<Member> findByUsernameAndRole(String username, MemberRoleEnum role);

}
