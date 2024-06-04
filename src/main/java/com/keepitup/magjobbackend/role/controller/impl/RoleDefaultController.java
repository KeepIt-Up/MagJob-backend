package com.keepitup.magjobbackend.role.controller.impl;

import com.keepitup.magjobbackend.configuration.Constants;
import com.keepitup.magjobbackend.configuration.KeycloakController;
import com.keepitup.magjobbackend.configuration.SecurityService;
import com.keepitup.magjobbackend.organization.entity.Organization;
import com.keepitup.magjobbackend.organization.service.impl.OrganizationDefaultService;
import com.keepitup.magjobbackend.role.controller.api.RoleController;
import com.keepitup.magjobbackend.role.dto.GetRoleResponse;
import com.keepitup.magjobbackend.role.dto.GetRolesResponse;
import com.keepitup.magjobbackend.role.dto.PatchRoleRequest;
import com.keepitup.magjobbackend.role.dto.PostRoleRequest;
import com.keepitup.magjobbackend.role.entity.Role;
import com.keepitup.magjobbackend.role.function.RequestToRoleFunction;
import com.keepitup.magjobbackend.role.function.RoleToResponseFunction;
import com.keepitup.magjobbackend.role.function.RolesToResponseFunction;
import com.keepitup.magjobbackend.role.function.UpdateRoleWithRequestFunction;
import com.keepitup.magjobbackend.role.service.impl.RoleDefaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.util.Optional;

@Controller
public class RoleDefaultController implements RoleController {
    private final RoleDefaultService roleService;
    private final OrganizationDefaultService organizationService;
    private final RoleToResponseFunction roleToResponseFunction;
    private final RolesToResponseFunction rolesToResponseFunction;
    private final RequestToRoleFunction requestToRoleFunction;
    private final UpdateRoleWithRequestFunction updateRoleWithRequestFunction;
    private final KeycloakController keycloakController;
    private final SecurityService securityService;

    @Autowired
    public RoleDefaultController(
            RoleDefaultService roleService,
            OrganizationDefaultService organizationService,
            RoleToResponseFunction roleToResponseFunction,
            RolesToResponseFunction rolesToResponseFunction,
            RequestToRoleFunction requestToRoleFunction,
            UpdateRoleWithRequestFunction updateRoleWithRequestFunction,
            KeycloakController keycloakController, SecurityService securityService
    ) {
        this.roleService = roleService;
        this.organizationService = organizationService;
        this.roleToResponseFunction = roleToResponseFunction;
        this.rolesToResponseFunction = rolesToResponseFunction;
        this.requestToRoleFunction = requestToRoleFunction;
        this.updateRoleWithRequestFunction = updateRoleWithRequestFunction;
        this.keycloakController = keycloakController;
        this.securityService = securityService;
    }

    @Override
    public GetRolesResponse getRoles() {
        if (!securityService.hasAdminPermission()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return rolesToResponseFunction.apply(roleService.findAll());
    }

    @Override
    public GetRoleResponse getRole(BigInteger id) {
        if (!securityService.hasAdminPermission()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return roleService.find(id)
                .map(roleToResponseFunction)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public GetRolesResponse getRolesByOrganization(BigInteger organizationId) {
        Optional<Organization> organizationOptional = organizationService.find(organizationId);

        Organization organization = organizationOptional
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!securityService.hasPermission(organization, Constants.PERMISSION_NAME_CAN_MANAGE_ROLES)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return rolesToResponseFunction.apply(roleService.findAllByOrganization(organization));
    }

    @Override
    public GetRoleResponse createRole(PostRoleRequest postRoleRequest) {
        Optional<Organization> organizationOptional = organizationService.find(postRoleRequest.getOrganization());

        Organization organization = organizationOptional
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!securityService.hasPermission(organization, Constants.PERMISSION_NAME_CAN_MANAGE_ROLES)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        boolean roleExists = roleService.findAllByOrganization(organization)
                .stream().anyMatch(role -> role.getName().equalsIgnoreCase(postRoleRequest.getName()));

        if (roleExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        roleService.create(requestToRoleFunction.apply(postRoleRequest));

        keycloakController.addChildGroupToKeycloak(organization.getName(), postRoleRequest.getName());

        return roleService.findByName(postRoleRequest.getName())
                .map(roleToResponseFunction)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public GetRoleResponse updateRole(BigInteger id, PatchRoleRequest patchRoleRequest) {
        Role role = roleService.find(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        Optional<Organization> organizationOptional = organizationService.find(role.getOrganization().getId());

        Organization organization = organizationOptional
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!securityService.hasPermission(organization, Constants.PERMISSION_NAME_CAN_MANAGE_ROLES)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        roleService.update(updateRoleWithRequestFunction.apply(role, patchRoleRequest));

        return getRole(id);
    }

    @Override
    public void deleteRole(BigInteger id) {
        Role role = roleService.find(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        Optional<Organization> organizationOptional = organizationService.find(role.getOrganization().getId());

        Organization organization = organizationOptional
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!securityService.hasPermission(organization, Constants.PERMISSION_NAME_CAN_MANAGE_ROLES)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        keycloakController.deleteChildGroupFromKeycloak(organization.getName(), role.getName());

        roleService.delete(id);
    }
}
