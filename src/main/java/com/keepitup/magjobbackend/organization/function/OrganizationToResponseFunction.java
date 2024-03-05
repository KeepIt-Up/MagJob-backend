package com.keepitup.magjobbackend.organization.function;

import com.keepitup.magjobbackend.organization.dto.GetOrganizationResponse;
import com.keepitup.magjobbackend.organization.entity.Organization;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class OrganizationToResponseFunction implements Function<Organization, GetOrganizationResponse> {

    @Override
    public GetOrganizationResponse apply(Organization organization) {
        return GetOrganizationResponse.builder()
                .id(organization.getId())
                .dateOfCreation(organization.getDateOfCreation())
                .name(organization.getName())
                .profileBannerUrl(organization.getProfileBannerUrl())
                .build();
    }
}
