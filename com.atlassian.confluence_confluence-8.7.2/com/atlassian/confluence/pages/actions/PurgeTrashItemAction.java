/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.event.events.space.SpaceTrashPurgeContentEvent;
import com.atlassian.confluence.pages.TrashManager;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import java.util.Arrays;
import java.util.List;

public class PurgeTrashItemAction
extends AbstractSpaceAction {
    protected ContentEntityManager contentEntityManager;
    protected ContentEntityObject contentEntityObject;
    protected long contentId;
    protected String Type;
    private TrashManager trashManager;

    @Override
    public void validate() {
        super.validate();
        if (this.getContentEntityObject().isCurrent()) {
            this.addActionError(this.getText("error.purge.non.trash"));
        }
    }

    public String execute() throws Exception {
        ContentEntityObject contentEntityObject = this.getContentEntityObject();
        this.trashManager.purge(((SpaceContentEntityObject)contentEntityObject).getSpaceKey(), contentEntityObject.getId());
        this.publishEvent(contentEntityObject);
        return "success";
    }

    private void publishEvent(ContentEntityObject contentEntityObject) {
        this.eventPublisher.publish((Object)new SpaceTrashPurgeContentEvent(this, ((SpaceContentEntityObject)contentEntityObject).getSpace(), contentEntityObject.getTypeEnum()));
    }

    public void setTrashManager(TrashManager trashManager) {
        this.trashManager = trashManager;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public long getContentId() {
        return this.contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    @Override
    public boolean isPermitted() {
        ContentEntityObject contentEntityObject = this.getContentEntityObject();
        if (!(contentEntityObject instanceof SpaceContentEntityObject)) {
            return false;
        }
        SpaceContentEntityObject spaceContentEntityObject = (SpaceContentEntityObject)contentEntityObject;
        List<String> permissions = Arrays.asList("SETSPACEPERMISSIONS", "VIEWSPACE");
        return this.spacePermissionManager.hasAllPermissions(permissions, this.getSpace(), this.getAuthenticatedUser()) && spaceContentEntityObject.isInSpace(this.getSpace());
    }

    public ContentEntityObject getContentEntityObject() {
        if (this.contentEntityObject == null) {
            this.contentEntityObject = this.contentEntityManager.getById(this.contentId);
        }
        return this.contentEntityObject;
    }

    public String getType() {
        return this.getNiceContentType(this.getContentEntityObject());
    }
}

