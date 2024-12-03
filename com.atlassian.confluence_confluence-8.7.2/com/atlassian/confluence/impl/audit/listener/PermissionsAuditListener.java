/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.event.events.permission.GlobalPermissionRemoveEvent;
import com.atlassian.confluence.event.events.permission.GlobalPermissionSaveEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionRemoveEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionSaveEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionsRemoveForGroupEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionsRemoveForUserEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionsRemoveFromSpaceEvent;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditAction;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractAuditListener;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceUpdateTrigger;
import com.atlassian.event.api.EventListener;
import java.util.ArrayList;
import java.util.Optional;

public class PermissionsAuditListener
extends AbstractAuditListener {
    public static final String GLOBAL_PERMISSION_REMOVED = AuditHelper.buildSummaryTextKey("global.permission.removed");
    public static final String GLOBAL_PERMISSION_ADDED = AuditHelper.buildSummaryTextKey("global.permission.added");
    public static final String SPACE_PERMISSION_REMOVED = AuditHelper.buildSummaryTextKey("space.permission.removed");
    public static final String SPACE_PERMISSION_ADDED = AuditHelper.buildSummaryTextKey("space.permission.added");
    private static final String AUTHENTICATED_USERS = "authenticated-users";

    public PermissionsAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, auditingContext);
    }

    @EventListener
    public void globalPermissionRemoveEvent(GlobalPermissionRemoveEvent event) {
        this.save(() -> this.buildPermissionRecord(event.getPermission(), null, GLOBAL_PERMISSION_REMOVED, AuditAction.REMOVE));
    }

    @EventListener
    public void globalPermissionSaveEvent(GlobalPermissionSaveEvent event) {
        this.save(() -> this.buildPermissionRecord(event.getPermission(), null, GLOBAL_PERMISSION_ADDED, AuditAction.ADD));
    }

    @EventListener
    public void spacePermissionRemoveEvent(SpacePermissionRemoveEvent event) {
        event.getPermissions().forEach(permission -> this.save(() -> this.buildPermissionRecord((SpacePermission)permission, event.getSpace(), SPACE_PERMISSION_REMOVED, AuditAction.REMOVE)));
    }

    @EventListener
    public void spacePermissionSaveEvent(SpacePermissionSaveEvent event) {
        event.getPermissions().forEach(permission -> this.save(() -> this.buildPermissionRecord((SpacePermission)permission, permission.getSpace(), SPACE_PERMISSION_ADDED, AuditAction.ADD)));
    }

    @EventListener
    public void spacePermissionsRemoveForGroupEvent(SpacePermissionsRemoveForGroupEvent event) {
        event.getPermissions().forEach(permission -> this.save(() -> this.buildPermissionRecord((SpacePermission)permission, permission.getSpace(), SPACE_PERMISSION_REMOVED, AuditAction.REMOVE)));
    }

    @EventListener
    public void spacePermissionsRemoveForUserEvent(SpacePermissionsRemoveForUserEvent event) {
        event.getPermissions().forEach(permission -> this.save(() -> this.buildPermissionRecord((SpacePermission)permission, permission.getSpace(), SPACE_PERMISSION_REMOVED, AuditAction.REMOVE)));
    }

    @EventListener
    public void spacePermissionsRemoveFromSpaceEvent(SpacePermissionsRemoveFromSpaceEvent event) {
        if (event.getUpdateTrigger() == SpaceUpdateTrigger.SPACE_REMOVED) {
            return;
        }
        event.getPermissions().forEach(permission -> this.save(() -> this.buildPermissionRecord((SpacePermission)permission, event.getSpace(), SPACE_PERMISSION_REMOVED, AuditAction.REMOVE)));
    }

    private AuditEvent buildPermissionRecord(SpacePermission permission, Space space, String summary, AuditAction action) {
        ArrayList<ChangedValue> changedValues = new ArrayList<ChangedValue>();
        if (permission != null) {
            changedValues.addAll(this.getAuditHandlerService().handle(permission, action));
        }
        AuditEvent.Builder builder = AuditEvent.fromI18nKeys((String)AuditCategories.PERMISSIONS, (String)summary, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.PERMISSIONS).changedValues(changedValues);
        this.getAffectedObject(permission).ifPresent(arg_0 -> ((AuditEvent.Builder)builder).affectedObject(arg_0));
        this.getAssociatedObject(space).ifPresent(arg_0 -> ((AuditEvent.Builder)builder).affectedObject(arg_0));
        return builder.build();
    }

    private Optional<AuditResource> getAffectedObject(SpacePermission permission) {
        if (permission == null) {
            return Optional.empty();
        }
        if (permission.isUserPermission()) {
            return Optional.of(this.buildResource(this.auditHelper.fetchUserFullName(permission.getUserSubject()), this.resourceTypes.user(), this.auditHelper.fetchUserKey(permission.getUserSubject())));
        }
        if (permission.isGroupPermission()) {
            return Optional.of(this.buildResource(permission.getGroup(), this.resourceTypes.group(), permission.getGroup()));
        }
        if (permission.isAuthenticatedUsersPermission()) {
            return Optional.of(this.buildResource(AUTHENTICATED_USERS, this.resourceTypes.group(), AUTHENTICATED_USERS));
        }
        if (permission.isAnonymousPermission()) {
            return Optional.of(this.buildResourceWithoutId(this.auditHelper.translate("anonymous.name"), this.resourceTypes.user()));
        }
        return Optional.empty();
    }

    private Optional<AuditResource> getAssociatedObject(Space space) {
        return Optional.ofNullable(space).map(s -> this.buildResource(s.getName(), this.resourceTypes.space(), s.getId()));
    }
}

