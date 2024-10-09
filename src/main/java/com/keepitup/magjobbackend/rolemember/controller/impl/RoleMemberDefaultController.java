package com.keepitup.magjobbackend.rolemember.controller.impl;

import com.keepitup.magjobbackend.configuration.Constants;
import com.keepitup.magjobbackend.configuration.KeycloakController;
import com.keepitup.magjobbackend.configuration.SecurityService;
import com.keepitup.magjobbackend.member.entity.Member;
import com.keepitup.magjobbackend.member.service.impl.MemberDefaultService;
import com.keepitup.magjobbackend.organization.entity.Organization;
import com.keepitup.magjobbackend.role.entity.Role;
import com.keepitup.magjobbackend.role.service.impl.RoleDefaultService;
import com.keepitup.magjobbackend.rolemember.controller.api.RoleMemberController;
import com.keepitup.magjobbackend.rolemember.dto.GetRoleMemberResponse;
import com.keepitup.magjobbackend.rolemember.dto.GetRoleMembersResponse;
import com.keepitup.magjobbackend.rolemember.dto.PostRoleMemberRequest;
import com.keepitup.magjobbackend.rolemember.dto.PostRoleMembersRequest;
import com.keepitup.magjobbackend.rolemember.entity.RoleMember;
import com.keepitup.magjobbackend.rolemember.function.RequestToRoleMemberFunction;
import com.keepitup.magjobbackend.rolemember.function.RequestToRoleMembersFunction;
import com.keepitup.magjobbackend.rolemember.function.RoleMemberToResponseFunction;
import com.keepitup.magjobbackend.rolemember.function.RoleMembersToResponseFunction;
import com.keepitup.magjobbackend.rolemember.service.impl.RoleMemberDefaultService;
import com.keepitup.magjobbackend.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.util.Optional;

@Controller
public class RoleMemberDefaultController implements RoleMemberController {
    private final RoleMemberDefaultService roleMemberService;
    private final RoleDefaultService roleService;
    private final MemberDefaultService memberService;
    private final RoleMemberToResponseFunction roleMemberToResponseFunction;
    private final RoleMembersToResponseFunction roleMembersToResponseFunction;
    private final RequestToRoleMemberFunction requestToRoleMemberFunction;
    private final KeycloakController keycloakController;
    private final RequestToRoleMembersFunction requestToRoleMembersFunction;
    private final SecurityService securityService;

    @Autowired
    public RoleMemberDefaultController(
            RoleMemberDefaultService roleMemberService,
            RoleDefaultService roleService,
            MemberDefaultService memberService,
            RoleMemberToResponseFunction roleMemberToResponseFunction,
            RoleMembersToResponseFunction roleMembersToResponseFunction,
            RequestToRoleMemberFunction requestToRoleMemberFunction,
            KeycloakController keycloakController,
            RequestToRoleMembersFunction requestToRoleMembersFunction,
            SecurityService securityService
    ) {
        this.roleMemberService = roleMemberService;
        this.roleService = roleService;
        this.memberService = memberService;
        this.roleMemberToResponseFunction = roleMemberToResponseFunction;
        this.roleMembersToResponseFunction = roleMembersToResponseFunction;
        this.requestToRoleMemberFunction = requestToRoleMemberFunction;
        this.keycloakController = keycloakController;
        this.requestToRoleMembersFunction = requestToRoleMembersFunction;
        this.securityService = securityService;
    }

    @Override
    public GetRoleMembersResponse getRoleMembers(int page, int size) {
        if (!securityService.hasAdminPermission()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
  
        PageRequest pageRequest = PageRequest.of(page, size);
        Integer count = roleMemberService.findAll().size();
        return roleMembersToResponseFunction.apply(roleMemberService.findAll(pageRequest), count);
    }

    @Override
    public GetRoleMemberResponse getRoleMember(BigInteger id) {
        if (!securityService.hasAdminPermission()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return roleMemberService.find(id)
                .map(roleMemberToResponseFunction)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public GetRoleMembersResponse getRoleMembersByRole(int page, int size, BigInteger roleId) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Optional<Role> roleOptional = roleService.find(roleId);

        Role role = roleOptional
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Organization organization = role.getOrganization();

        if(!securityService.belongsToOrganization(organization)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Integer count = roleMemberService.findAllByRole(role, Pageable.unpaged()).getNumberOfElements();

        return roleMembersToResponseFunction.apply(roleMemberService.findAllByRole(role, pageRequest), count);
    }

    @Override
    public GetRoleMembersResponse getRoleMembersByMember(int page, int size, BigInteger memberId) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Optional<Member> memberOptional = memberService.find(memberId);

        Member member = memberOptional
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Organization organization = member.getOrganization();

        if(!securityService.belongsToOrganization(organization)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Integer count = roleMemberService.findAllByMember(member, Pageable.unpaged()).getNumberOfElements();

        return roleMembersToResponseFunction.apply(roleMemberService.findAllByMember(member, pageRequest), count);
    }

    @Override
    public GetRoleMemberResponse createRoleMember(PostRoleMemberRequest postRoleMemberRequest) {
        Organization organization = roleService.find(postRoleMemberRequest.getRole()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        ).getOrganization();

        User user = memberService.find(postRoleMemberRequest.getMember()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        ).getUser();

        Role role = roleService.find(postRoleMemberRequest.getRole()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String roleName = role.getName();

        Optional<Page<Role>> memberRoles = roleMemberService.findAllRolesByMember(postRoleMemberRequest.getMember(), Pageable.unpaged());

        if (!securityService.hasPermission(organization, Constants.PERMISSION_NAME_CAN_MANAGE_ROLES)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (memberRoles.isEmpty() || memberRoles.get().stream().toList().contains(role)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        roleMemberService.create(requestToRoleMemberFunction.apply(postRoleMemberRequest));

        keycloakController.addUserToKeycloakGroup(organization.getName(), user.getId(), roleName);

        return roleMemberService.findByMemberAndRole(
                    memberService.find(postRoleMemberRequest.getMember()).orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
                    ),
                    roleService.find(postRoleMemberRequest.getRole()).orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
                    ))
                .map(roleMemberToResponseFunction)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public GetRoleMembersResponse createRoleMembers(PostRoleMembersRequest postRoleMembersRequest) {
        Organization organization = roleService.find(postRoleMembersRequest.getRoleId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        ).getOrganization();

        if (!securityService.hasPermission(organization, Constants.PERMISSION_NAME_CAN_MANAGE_ROLES)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Role role = roleService.find(postRoleMembersRequest.getRoleId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        for (PostRoleMembersRequest.RoleMember roleMemberRequest : postRoleMembersRequest.getRoleMembers()) {
            BigInteger memberId = roleMemberRequest.getMemberId();

            Optional<Page<Role>> memberRoles = roleMemberService.findAllRolesByMember(memberId, Pageable.unpaged());

            if (memberRoles.isPresent() && memberRoles.get().stream().anyMatch(existingRole -> existingRole.equals(role))) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Member " + memberId + " already has the role");
            }
        }

        roleMemberService.createAll(requestToRoleMembersFunction.apply(postRoleMembersRequest));

        Integer count = roleMemberService.findAllByRole(role, Pageable.unpaged()).getNumberOfElements();

        RoleMember roleMember = roleMemberService.findAllByRole(role, Pageable.unpaged()).getContent().get(0);

        roleMember.setMember(memberService.find(roleMember.getMember().getId()).get());
        roleMemberService.update(roleMember);

        return roleMembersToResponseFunction.apply(roleMemberService.findAllByRole(
                role,
                Pageable.unpaged()
        ), count);
    }

    @Override
    public void deleteRoleMember(BigInteger memberId, BigInteger roleId) {
        Role role = roleService.find(roleId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        Organization organization = role.getOrganization();

        Member member = memberService.find(memberId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        User user = member.getUser();


        if (!securityService.hasPermission(organization, Constants.PERMISSION_NAME_CAN_MANAGE_ROLES)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        keycloakController.removeUserFromKeycloakGroup(organization.getName(), user.getId(), role.getName());

        roleMemberService.findByMemberAndRole(member, role)
                .ifPresentOrElse(
                        roleMember -> roleMemberService.delete(roleMember.getId()),
                        () -> {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                        }
                );
    }
}
