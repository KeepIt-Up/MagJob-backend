package com.keepitup.magjobbackend.rolemember.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
@Schema(description = "GetRoleMembersResponse DTO")
public class GetRoleMembersResponse {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    @EqualsAndHashCode
    public static class RoleMember {
        @Schema(description = "Role member id value")
        private BigInteger id;

        @Schema(description = "User firstname value")
        private String firstName;

        @Schema(description = "User lastname value")
        private String lastName;

        @Schema(description = "Role id value")
        private BigInteger roleId;

        @Schema(description = "Member id value")
        private BigInteger memberId;
    }

    @Singular
    @Schema(description = "Role member list")
    private List<RoleMember> roleMembers;

    @Schema(description = "Number of all objects")
    private Integer count;
}
