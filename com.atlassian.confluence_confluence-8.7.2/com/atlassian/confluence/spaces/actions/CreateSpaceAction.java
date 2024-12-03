/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.AbstractCreateSpaceAction;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public class CreateSpaceAction
extends AbstractCreateSpaceAction {
    private String name;
    private String spacePermission;
    private String message;
    private boolean result;

    @Override
    public void validate() {
        if (StringUtils.isBlank((CharSequence)this.key)) {
            this.addFieldError("key", this.getText("space.key.empty"));
        } else if (!Space.isValidGlobalSpaceKey(this.getKey())) {
            this.addFieldError("key", this.getText("space.key.invalid"));
        } else if (this.spaceManager.getSpace(this.key) != null) {
            this.addFieldError("key", this.getText("space.key.exists"));
        }
        if (StringUtils.isEmpty((CharSequence)this.name)) {
            this.addFieldError("name", this.getText("space.name.empty"));
        }
        super.validate();
    }

    public String execute() throws Exception {
        ConfluenceUser creator = this.getAuthenticatedUser();
        this.space = "private".equals(this.spacePermission) ? this.spaceManager.createPrivateSpace(this.key, this.name, null, creator) : this.spaceManager.createSpace(this.key, this.name, null, creator);
        this.indexManager.flushQueue(IndexManager.IndexQueueFlushMode.ONLY_FIRST_BATCH);
        return super.execute();
    }

    @Override
    public List<String> getPermissionTypes() {
        List<String> permissionTypes = super.getPermissionTypes();
        this.addPermissionTypeTo("CREATESPACE", permissionTypes);
        return permissionTypes;
    }

    @Override
    public boolean isPermitted() {
        return this.spacePermissionManager.hasAllPermissions(this.getPermissionTypes(), this.getSpace(), this.getAuthenticatedUser());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isResult() {
        return this.result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getSpacePermission() {
        return this.spacePermission;
    }

    public void setSpacePermission(String spacePermission) {
        this.spacePermission = spacePermission;
    }
}

