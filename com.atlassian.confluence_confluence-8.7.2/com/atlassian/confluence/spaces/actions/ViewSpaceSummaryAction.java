/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.labels.SpaceLabelManager;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.spaces.actions.SpaceLabelAware;
import com.atlassian.confluence.util.ContentUtils;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Iterator;
import java.util.List;
import org.apache.struts2.ServletActionContext;

@RequiresAnyConfluenceAccess
public class ViewSpaceSummaryAction
extends AbstractSpaceAction
implements SpaceLabelAware {
    private static final int ADMIN_LIMIT = 7;
    private String name;
    private String description;
    private boolean showAllAdmins;
    private boolean thereAreMoreAdmins;
    private List<User> adminUsers;
    private List recentlyUpdatedContent;
    private ContentEntityManager contentEntityManager;
    private CommentManager commentManager;
    private SpaceLabelManager spaceLabelManager;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        if (this.getSpace() == null) {
            ServletActionContext.getResponse().sendError(404, "Space not found: " + this.getKey());
            return "ERROR";
        }
        this.name = this.getSpace().getName();
        this.description = this.getSpace().getDescription() != null ? this.getSpace().getDescription().getBodyContent().getBody() : "";
        return "success";
    }

    public List getRecentlyUpdatedContent() {
        if (this.recentlyUpdatedContent == null) {
            Iterator content = this.contentEntityManager.getRecentlyModifiedEntities(this.getSpace().getKey(), this.getMaxRecentChangesSize());
            Iterator comments = this.commentManager.getRecentlyUpdatedComments(this.getSpace(), this.getMaxRecentChangesSize());
            List<ConfluenceEntityObject> updates = ContentUtils.mergeContentObjects(content, comments, this.getMaxRecentChangesSize());
            this.recentlyUpdatedContent = this.getPermittedEntitiesOf(updates);
        }
        return this.recentlyUpdatedContent;
    }

    @Override
    public List getTeamLabelsOnThisSpace() {
        return this.getSpaceLabelManager().getTeamLabelsOnSpace(this.getSpace().getKey());
    }

    @Override
    public List getLabelsOnThisSpace() {
        return this.getSpaceLabelManager().getLabelsOnSpace(this.getSpace());
    }

    public int getMaxRecentChangesSize() {
        return this.getUserInterfaceState().getMaxRecentChangesSize();
    }

    public void setMaxRecentChangesSize(int i) {
        this.getUserInterfaceState().setMaxRecentChangesSize(i);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ContentEntityManager getContentEntityManager() {
        return this.contentEntityManager;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public void setCommentManager(CommentManager commentManager) {
        this.commentManager = commentManager;
    }

    public SpaceLabelManager getSpaceLabelManager() {
        return this.spaceLabelManager;
    }

    public void setSpaceLabelManager(SpaceLabelManager spaceLabelManager) {
        this.spaceLabelManager = spaceLabelManager;
    }

    public List<User> getSpaceAdmins() {
        if (this.adminUsers == null) {
            if (this.showAllAdmins) {
                this.adminUsers = this.spaceManager.getSpaceAdmins(this.getSpace());
            } else {
                this.adminUsers = this.spaceManager.getSpaceAdmins(this.getSpace(), 8);
                this.thereAreMoreAdmins = false;
                if (this.adminUsers.size() > 7) {
                    this.adminUsers = this.adminUsers.subList(0, 7);
                    this.thereAreMoreAdmins = true;
                }
            }
        }
        return this.adminUsers;
    }

    public void setShowAllAdmins(boolean showAllAdmins) {
        this.showAllAdmins = showAllAdmins;
    }

    public boolean thereAreMoreAdmins() {
        return this.thereAreMoreAdmins;
    }
}

