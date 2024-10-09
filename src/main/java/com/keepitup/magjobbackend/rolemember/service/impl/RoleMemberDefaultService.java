package com.keepitup.magjobbackend.rolemember.service.impl;

import com.keepitup.magjobbackend.member.entity.Member;
import com.keepitup.magjobbackend.member.repository.api.MemberRepository;
import com.keepitup.magjobbackend.role.entity.Role;
import com.keepitup.magjobbackend.rolemember.entity.RoleMember;
import com.keepitup.magjobbackend.rolemember.repository.api.RoleMemberRepository;
import com.keepitup.magjobbackend.rolemember.service.api.RoleMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class RoleMemberDefaultService implements RoleMemberService {
    private final RoleMemberRepository roleMemberRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public RoleMemberDefaultService(RoleMemberRepository roleMemberRepository,
                                    MemberRepository memberRepository
    ) {
        this.roleMemberRepository = roleMemberRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public Optional<RoleMember> find(BigInteger id) {
        return roleMemberRepository.findById(id);
    }

    @Override
    public Optional<RoleMember> findByMemberAndRole(Member member, Role role) {
        return roleMemberRepository.findByMemberAndRole(member, role);
    }

    @Override
    public List<RoleMember> findAll() {
        return roleMemberRepository.findAll();
    }

    @Override
    public Page<RoleMember> findAll(Pageable pageable) {
        return roleMemberRepository.findAll(pageable);
    }

    @Override
    public Page<RoleMember> findAllByMember(Member member, Pageable pageable) {
        return roleMemberRepository.findAllByMember(member, pageable);
    }

    @Override
    public Page<RoleMember> findAllByRole(Role role, Pageable pageable) {
        return roleMemberRepository.findAllByRole(role, pageable);
    }

    @Override
    public Optional<Page<Role>> findAllRolesByMember(BigInteger memberId, Pageable pageable) {
        return memberRepository.findById(memberId)
                .map(member -> roleMemberRepository.findAllByMember(member, pageable))
                .map(roleMembers -> roleMembers.map(RoleMember::getRole));
    }

    @Override
    public void create(RoleMember roleMember) {
        roleMemberRepository.save(roleMember);
    }

    @Override
    public void createAll(List<RoleMember> roleMembers) {
        roleMemberRepository.saveAll(roleMembers);
    }

    @Override
    public void delete(BigInteger id) {
        roleMemberRepository.findById(id).ifPresent(roleMemberRepository::delete);
    }

    @Override
    public void update(RoleMember roleMember) {
        roleMemberRepository.save(roleMember);
    }
}
