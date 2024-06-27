package com.keepitup.magjobbackend.invitation.controller.impl;

import com.keepitup.magjobbackend.configuration.Constants;
import com.keepitup.magjobbackend.configuration.KeycloakController;
import com.keepitup.magjobbackend.configuration.SecurityService;
import com.keepitup.magjobbackend.invitation.controller.api.InvitationController;
import com.keepitup.magjobbackend.invitation.dto.AcceptInvitationRequest;
import com.keepitup.magjobbackend.invitation.dto.GetInvitationResponse;
import com.keepitup.magjobbackend.invitation.dto.GetInvitationsResponse;
import com.keepitup.magjobbackend.invitation.dto.PostInvitationRequest;
import com.keepitup.magjobbackend.invitation.entity.Invitation;
import com.keepitup.magjobbackend.invitation.function.InvitationToResponseFunction;
import com.keepitup.magjobbackend.invitation.function.InvitationsToResponseFunction;
import com.keepitup.magjobbackend.invitation.function.RequestToInvitationFunction;
import com.keepitup.magjobbackend.invitation.service.api.InvitationService;
import com.keepitup.magjobbackend.jwt.CustomJwt;
import com.keepitup.magjobbackend.member.dto.GetMemberResponse;
import com.keepitup.magjobbackend.member.entity.Member;
import com.keepitup.magjobbackend.member.function.MemberToResponseFunction;
import com.keepitup.magjobbackend.member.service.api.MemberService;
import com.keepitup.magjobbackend.organization.entity.Organization;
import com.keepitup.magjobbackend.organization.service.api.OrganizationService;
import com.keepitup.magjobbackend.user.entity.User;
import com.keepitup.magjobbackend.user.service.api.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

@RestController
@Log
public class InvitationDefaultController implements InvitationController {

    private final InvitationService service;
    private final InvitationsToResponseFunction invitationsToResponse;
    private final InvitationToResponseFunction invitationToResponse;
    private final RequestToInvitationFunction requestToInvitation;
    private final MemberService memberService;
    private final UserService userService;
    private final OrganizationService organizationService;
    private final MemberToResponseFunction memberToResponse;
    private final KeycloakController keycloakController;
    private final SecurityService securityService;


    @Autowired
    public InvitationDefaultController(InvitationService service,
                                       InvitationsToResponseFunction invitationsToResponse,
                                       InvitationToResponseFunction invitationToResponse,
                                       RequestToInvitationFunction requestToInvitation,
                                       MemberService memberService,
                                       UserService userService,
                                       OrganizationService organizationService,
                                       MemberToResponseFunction memberToResponse,
                                       KeycloakController keycloakController,
                                       SecurityService securityService
    ) {
        this.service = service;
        this.invitationsToResponse = invitationsToResponse;
        this.invitationToResponse = invitationToResponse;
        this.requestToInvitation = requestToInvitation;
        this.memberService = memberService;
        this.userService = userService;
        this.organizationService = organizationService;
        this.memberToResponse = memberToResponse;
        this.keycloakController = keycloakController;
        this.securityService = securityService;
    }


    @Override
    public GetInvitationResponse getInvitation(UUID userId, BigInteger organizationId) {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        UUID loggedInUserId = UUID.fromString(jwt.getExternalId());

        if (!loggedInUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return service.findByUserAndOrganization(userId, organizationId)
                .map(invitationToResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public GetInvitationsResponse getInvitationsByUser(int page, int size, UUID userId) {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        UUID loggedInUserId = UUID.fromString(jwt.getExternalId());

        if (!loggedInUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
  
        PageRequest pageRequest = PageRequest.of(page, size);

        Optional<Page<Invitation>> countOptional = service.findAllByUserAndIsActive(userId, true, Pageable.unpaged());
        if (countOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Integer count = countOptional.get().getNumberOfElements();

        Optional<Page<Invitation>> invitations = service.findAllByUserAndIsActive(userId, true, pageRequest);
        if (invitations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return invitationsToResponse.apply(invitations.get(), count);
    }

    @Override
    public GetInvitationsResponse getInvitationsByOrganization(int page, int size, BigInteger organizationId) {
        Optional<Organization> organizationOptional = organizationService.find(organizationId);

        Organization organization = organizationOptional
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if(!securityService.belongsToOrganization(organization)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
      
        PageRequest pageRequest = PageRequest.of(page, size);

        Optional<Page<Invitation>> countOptional = service.findAllByOrganizationAndIsActive(organizationId, true, Pageable.unpaged());
        if (countOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Integer count = countOptional.get().getNumberOfElements();

        Optional<Page<Invitation>> invitations = service.findAllByOrganizationAndIsActive(organizationId, true, pageRequest);
        if (invitations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return invitationsToResponse.apply(invitations.get(), count);
    }

    @Override
    public GetInvitationResponse sendInvitation(PostInvitationRequest request) {
        Organization organization = organizationService.find(request.getOrganization()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        if (!securityService.hasPermission(organization, Constants.PERMISSION_NAME_CAN_MANAGE_INVITATIONS)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Optional<Invitation> invitation = service.findByUserAndOrganization(request.getUserId(), request.getOrganization());
        if (invitation.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        else {
            if (memberService.findByUserAndOrganization(userService.find(request.getUserId()).get(),
                    organizationService.find(request.getOrganization()).get()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            } else {
                service.create(requestToInvitation.apply(request));
            }
        }

        Optional<Invitation> invitationOptional = service.findByUserAndOrganization(request.getUserId(), request.getOrganization());

        User user = userService.find(request.getUserId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        if (invitationOptional.isPresent()) {
            invitationOptional.get().setUser(user);
            invitationOptional.get().setOrganization(organization);
        }

        return invitationOptional
                .map(invitationToResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public void deleteInvitation(UUID userId, BigInteger organizationId) {

        Organization organization = organizationService.find(organizationId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        if (!securityService.hasPermission(organization, Constants.PERMISSION_NAME_CAN_MANAGE_INVITATIONS)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        service.findByUserAndOrganization(userId, organizationId)
                .ifPresentOrElse(
                        invitation -> service.delete(userId, organizationId),
                        () -> {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                        }
                );
    }

    @Override
    public GetMemberResponse acceptInvitation(AcceptInvitationRequest request) {

        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        UUID loggedInUserId = UUID.fromString(jwt.getExternalId());

        if (!loggedInUserId.equals(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Optional<Invitation> invitation = service.findByUserAndOrganization(request.getUserId(), request.getOrganization());
        Optional<User> user = userService.find(request.getUserId());
        Optional<Organization> organization = organizationService.find(request.getOrganization());

        if (invitation.isPresent()) {

            if (user.isPresent() && organization.isPresent()) {
                memberService.create(Member.builder()
                        .pseudonym(request.getPseudonym())
                        .organization(organization.get())
                        .user(user.get())
                        .build());

                keycloakController.addUserToKeycloakGroup(organization.get().getName(), user.get().getId());

                service.delete(user.get().getId(), organization.get().getId());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return memberToResponse.apply(memberService.findByUserAndOrganization(user.get(), organization.get()).get());
    }

    @Override
    public void rejectInvitation(PostInvitationRequest request) {

        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        UUID loggedInUserId = UUID.fromString(jwt.getExternalId());

        if (!loggedInUserId.equals(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Optional<Invitation> invitation = service.findByUserAndOrganization(request.getUserId(), request.getOrganization());
        if (invitation.isPresent()) {
            service.delete(request.getUserId(), request.getOrganization());
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
