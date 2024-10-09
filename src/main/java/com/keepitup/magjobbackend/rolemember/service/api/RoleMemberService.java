package com.keepitup.magjobbackend.rolemember.service.api;

import com.keepitup.magjobbackend.member.entity.Member;
import com.keepitup.magjobbackend.role.entity.Role;
import com.keepitup.magjobbackend.rolemember.entity.RoleMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface RoleMemberService {

    Optional<RoleMember> find(BigInteger id);

    Optional<RoleMember> findByMemberAndRole(Member member, Role role);

    List<RoleMember> findAll();

    Page<RoleMember> findAll(Pageable pageable);

    Page<RoleMember> findAllByMember(Member member, Pageable pageable);

    Page<RoleMember> findAllByRole(Role role, Pageable pageable);

    Optional<Page<Role>> findAllRolesByMember(BigInteger memberId, Pageable pageable);

    void create(RoleMember roleMember);

    void createAll(List<RoleMember> roleMembers);

    void delete(BigInteger id);

    void update(RoleMember roleMember);
}
