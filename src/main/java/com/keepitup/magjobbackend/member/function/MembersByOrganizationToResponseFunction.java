package com.keepitup.magjobbackend.member.function;

import com.keepitup.magjobbackend.member.dto.GetMembersByOrganizationResponse;
import com.keepitup.magjobbackend.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
public class MembersByOrganizationToResponseFunction implements BiFunction<Page<Member>, Integer, GetMembersByOrganizationResponse> {
    @Override
    public GetMembersByOrganizationResponse apply(Page<Member> entities, Integer count) {
        return GetMembersByOrganizationResponse.builder()
                .members(entities.stream()
                        .map(member -> GetMembersByOrganizationResponse.Member.builder()
                                .id(member.getId())
                                .userId(member.getUser().getId())
                                .pseudonym(member.getPseudonym())
                                .firstName(member.getUser().getFirstname())
                                .lastName(member.getUser().getLastname())
                                .roles(member.getRoleMembers().stream()
                                        .map(role -> GetMembersByOrganizationResponse.Member.Role.builder()
                                                .id(role.getRole().getId())
                                                .name(role.getRole().getName())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .toList())
                .count(count)
                .build();
    }
}
