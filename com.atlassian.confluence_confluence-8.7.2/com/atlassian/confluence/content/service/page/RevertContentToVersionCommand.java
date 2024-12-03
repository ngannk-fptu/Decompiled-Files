/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.user.User;

public class RevertContentToVersionCommand {
    private final PermissionManager permissionManager;
    private final ContentEntityManager contentEntityManager;
    public static final String NON_UNIQUE_TITLE_ERROR = "reverting.entity.back.produces.title.conflict";

    public RevertContentToVersionCommand(PermissionManager permissionManager, ContentEntityManager contentEntityManager) {
        this.permissionManager = permissionManager;
        this.contentEntityManager = contentEntityManager;
    }

    public void validate(ServiceCommandValidator validator, ContentEntityObject ceo, ContentEntityObject possibleConflictingCeo, int versionToRevert, boolean revertTitle) {
        if (!ceo.isLatestVersion()) {
            validator.addValidationError("reverting.back.outdated.version", new Object[0]);
        }
        if (!this.isValidVersion(ceo, versionToRevert)) {
            validator.addValidationError("invalid.version", new Object[0]);
        }
        if (revertTitle && possibleConflictingCeo != null && possibleConflictingCeo.getId() != ceo.getId()) {
            validator.addValidationError(NON_UNIQUE_TITLE_ERROR, possibleConflictingCeo.getTitle());
        }
    }

    public boolean isAuthorized(User currentUser, ContentEntityObject contentEntityObject) {
        if (contentEntityObject == null) {
            return false;
        }
        return this.permissionManager.hasPermission(currentUser, Permission.EDIT, contentEntityObject);
    }

    public void execute(ContentEntityObject ceo, int version, String revertComment, boolean revertTitle) {
        this.contentEntityManager.revertContentEntityBackToVersion(ceo, version, revertComment, revertTitle);
    }

    public ContentEntityObject getVersionToRevert(ContentEntityObject latestVersion, int version) {
        if (this.isValidVersion(latestVersion, version)) {
            return this.contentEntityManager.getOtherVersion(latestVersion, version);
        }
        return null;
    }

    private boolean isValidVersion(ContentEntityObject latestVersion, int version) {
        return version < latestVersion.getVersion() && version > 0;
    }
}

