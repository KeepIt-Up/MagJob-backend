package com.keepitup.magjobbackend.announcementreceiver.controller.impl;

import com.keepitup.magjobbackend.announcement.entity.Announcement;
import com.keepitup.magjobbackend.announcement.service.impl.AnnouncementDefaultService;
import com.keepitup.magjobbackend.announcementreceiver.controller.api.AnnouncementReceiverController;
import com.keepitup.magjobbackend.announcementreceiver.dto.GetAnnouncementReceiverResponse;
import com.keepitup.magjobbackend.announcementreceiver.dto.GetAnnouncementReceiversResponse;
import com.keepitup.magjobbackend.announcementreceiver.dto.PatchAnnouncementReceiverRequest;
import com.keepitup.magjobbackend.announcementreceiver.dto.PostAnnouncementReceiverRequest;
import com.keepitup.magjobbackend.announcementreceiver.function.AnnouncementReceiverToResponseFunction;
import com.keepitup.magjobbackend.announcementreceiver.function.AnnouncementReceiversToResponseFunction;
import com.keepitup.magjobbackend.announcementreceiver.function.RequestToAnnouncementReceiverFunction;
import com.keepitup.magjobbackend.announcementreceiver.function.UpdateAnnouncementReceiverWithRequestFunction;
import com.keepitup.magjobbackend.announcementreceiver.service.impl.AnnouncementReceiverDefaultService;
import com.keepitup.magjobbackend.member.entity.Member;
import com.keepitup.magjobbackend.member.service.impl.MemberDefaultService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.util.Optional;

@RestController
@Log
public class AnnouncementReceiverDefaultController implements AnnouncementReceiverController {
    private final AnnouncementReceiverDefaultService announcementReceiverService;
    private final AnnouncementDefaultService announcementService;
    private final MemberDefaultService memberService;
    private final AnnouncementReceiverToResponseFunction announcementReceiverToResponseFunction;
    private final AnnouncementReceiversToResponseFunction announcementReceiversToResponseFunction;
    private final RequestToAnnouncementReceiverFunction requestToAnnouncementReceiverFunction;
    private final UpdateAnnouncementReceiverWithRequestFunction updateAnnouncementReceiverWithRequestFunction;

    @Autowired
    public AnnouncementReceiverDefaultController(
            AnnouncementReceiverDefaultService announcementReceiverService,
            AnnouncementDefaultService announcementService,
            MemberDefaultService memberService,
            AnnouncementReceiverToResponseFunction announcementReceiverToResponseFunction,
            AnnouncementReceiversToResponseFunction announcementReceiversToResponseFunction,
            RequestToAnnouncementReceiverFunction requestToAnnouncementReceiverFunction,
            UpdateAnnouncementReceiverWithRequestFunction updateAnnouncementReceiverWithRequestFunction
    ) {
        this.announcementReceiverService = announcementReceiverService;
        this.announcementService = announcementService;
        this.memberService = memberService;
        this.announcementReceiverToResponseFunction = announcementReceiverToResponseFunction;
        this.announcementReceiversToResponseFunction = announcementReceiversToResponseFunction;
        this.requestToAnnouncementReceiverFunction = requestToAnnouncementReceiverFunction;
        this.updateAnnouncementReceiverWithRequestFunction = updateAnnouncementReceiverWithRequestFunction;
    }

    @Override
    public GetAnnouncementReceiversResponse getAnnouncementReceivers() {
        return announcementReceiversToResponseFunction.apply(announcementReceiverService.findAll());
    }

    @Override
    public GetAnnouncementReceiverResponse getAnnouncementReceiver(BigInteger id) {
        return announcementReceiverService.find(id)
                .map(announcementReceiverToResponseFunction)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public GetAnnouncementReceiversResponse getAnnouncementReceiversByAnnouncement(BigInteger announcementId) {
        Optional<Announcement> announcementOptional = announcementService.find(announcementId);

        Announcement announcement = announcementOptional
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return announcementReceiversToResponseFunction.apply(announcementReceiverService.findAllByAnnouncement(announcement));
    }

    @Override
    public GetAnnouncementReceiversResponse getAnnouncementReceiversByMember(BigInteger memberId) {
        Optional<Member> memberOptional = memberService.find(memberId);

        Member member = memberOptional
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return announcementReceiversToResponseFunction.apply(announcementReceiverService.findAllByMember(member));
    }

    @Override
    public GetAnnouncementReceiverResponse createAnnouncementReceiver(PostAnnouncementReceiverRequest postAnnouncementReceiverRequest) {
        announcementReceiverService.create(requestToAnnouncementReceiverFunction.apply(postAnnouncementReceiverRequest));
        return announcementReceiverService.findByMemberAndAnnouncement(
                        memberService.find(postAnnouncementReceiverRequest.getMember()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)),
                        announcementService.find(postAnnouncementReceiverRequest.getAnnouncement()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(announcementReceiverToResponseFunction)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public GetAnnouncementReceiverResponse updateAnnouncementReceiver(BigInteger id, PatchAnnouncementReceiverRequest patchAnnouncementReceiverRequest) {
        announcementReceiverService.find(id)
                .ifPresentOrElse(
                        announcementReceiver -> announcementReceiverService.update(updateAnnouncementReceiverWithRequestFunction.apply(announcementReceiver, patchAnnouncementReceiverRequest)),
                        () -> {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                        }
                );
        return getAnnouncementReceiver(id);
    }

    @Override
    public void deleteAnnouncementReceiver(BigInteger id) {
        announcementReceiverService.find(id)
                .ifPresentOrElse(
                        announcementReceiver -> announcementReceiverService.delete(id),
                        () -> {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                        }
                );
    }
}
