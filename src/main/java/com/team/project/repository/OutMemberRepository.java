package com.team.project.repository;

import com.team.project.domain.OutMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutMemberRepository extends JpaRepository<OutMember, Long> {

    Boolean existsByUsername(String membername);
}
