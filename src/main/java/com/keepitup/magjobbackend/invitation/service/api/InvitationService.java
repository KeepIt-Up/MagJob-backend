package com.keepitup.magjobbackend.invitation.service.api;

import com.keepitup.magjobbackend.invitation.entity.Invitation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Optional;

import java.util.UUID;

public interface InvitationService {

    Optional<Page<Invitation>> findAllByUser(UUID userId, Pageable pageable);

    Optional<Page<Invitation>> findAllByUserAndIsActive(UUID userId, Boolean isActive, Pageable pageable);

    Optional<Page<Invitation>> findAllByOrganization(BigInteger organizationId, Pageable pageable);

    Optional<Page<Invitation>> findAllByOrganizationAndIsActive(BigInteger organizationId, Boolean isActive, Pageable pageable);

    Optional<Invitation> findByUserAndOrganization(UUID userId, BigInteger organizationId);

    Page<Invitation> findAllByDateOfCreation(ZonedDateTime dateOfCreation, Pageable pageable);

    void create(Invitation invitation);

    void delete(UUID userId, BigInteger organizationId);

    void update(Invitation invitation);
}
