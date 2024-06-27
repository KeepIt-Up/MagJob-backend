package com.keepitup.magjobbackend.member.controller.api;

import com.keepitup.magjobbackend.configuration.PageConfig;
import com.keepitup.magjobbackend.member.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@Tag(name="Member Controller")
public interface MemberController {
    PageConfig pageConfig = new PageConfig();

    @Operation(summary = "Get all Members")
    @GetMapping("api/members")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    GetMembersResponse getMembers(
            @Parameter(
                    name = "page number",
                    description = "Page number to retrieve"
            )
            @RequestParam(defaultValue = "#{pageConfig.number}")
            int page,
            @Parameter(
                    name = "page size",
                    description = "Number of records per page"
            )
            @RequestParam(defaultValue = "#{pageConfig.size}")
            int size
    );

    @Operation(summary = "Get Member")
    @GetMapping("api/members/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    GetMemberResponse getMember(
            @Parameter(
                    name = "id",
                    description = "Member id value",
                    required = true
            )
            @PathVariable("id")
            BigInteger id
    );

    @Operation(summary = "Get Members By Organization")
    @GetMapping("api/organizations/{organizationId}/members")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    GetMembersByOrganizationResponse getMembersByOrganization(
            @Parameter(
                    name = "page number",
                    description = "Page number to retrieve"
            )
            @RequestParam(defaultValue = "#{pageConfig.number}")
            int page,
            @Parameter(
                    name = "page size",
                    description = "Number of records per page"
            )
            @RequestParam(defaultValue = "#{pageConfig.size}")
            int size,
            @Parameter(
                    name = "organizationId",
                    description = "Organization id value",
                    required = true
            )
            @PathVariable("organizationId")
            BigInteger organizationId
    );

    @Operation(summary = "Create Member")
    @PostMapping("api/members")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    GetMemberResponse createMember(
            @Parameter(
                    name = "PostMemberRequest",
                    description = "PostMemberRequest DTO",
                    schema = @Schema(implementation = PostMemberRequest.class),
                    required = true
            )
            @RequestBody
            PostMemberRequest postMemberRequest
    );

    @Operation(summary = "Update Member")
    @PatchMapping("api/members/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    GetMemberResponse updateMember(
            @Parameter(
                    name = "id",
                    description = "Member id value",
                    required = true
            )
            @PathVariable("id")
            BigInteger id,
            @Parameter(
                    name = "PatchMemberRequest",
                    description = "PatchMemberRequest DTO",
                    schema = @Schema(implementation = PatchMemberRequest.class),
                    required = true
            )
            @RequestBody
            PatchMemberRequest patchMemberRequest
    );

    @Operation(summary = "Delete Member")
    @DeleteMapping("/api/members/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteMember(
            @Parameter(
                    name = "id",
                    description = "Member id value",
                    required = true
            )
            @PathVariable("id")
            BigInteger id
    );
}
