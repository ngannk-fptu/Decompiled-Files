/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.admin.actions.debug;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@WebSudoRequired
@AdminOnly
public class BrowseCommentsAction
extends ConfluenceActionSupport {
    CommentManager commentManager;
    SpaceManager spaceManager;
    List<Comment> comments;
    List<String> deleteCommentsList;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public String execute() {
        if (this.deleteCommentsList != null) {
            for (String id : this.deleteCommentsList) {
                this.commentManager.removeCommentFromObject(Long.parseLong(id));
            }
        }
        return this.doDefault();
    }

    @Override
    public String doDefault() {
        int maxItems = 50;
        ListBuilder<Space> listBuilder = this.spaceManager.getSpaces(SpacesQuery.newQuery().withSpaceType(SpaceType.GLOBAL).build());
        ArrayList<Comment> commentsList = new ArrayList<Comment>();
        for (List list : listBuilder) {
            for (Space space : list) {
                Iterator recentComments = this.commentManager.getRecentlyUpdatedComments(space, maxItems);
                while (recentComments.hasNext()) {
                    Comment comment = (Comment)recentComments.next();
                    commentsList.add(comment);
                }
            }
        }
        this.comments = commentsList;
        return "success";
    }

    public void setCommentManager(CommentManager commentManager) {
        this.commentManager = commentManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public List<Comment> getComments() {
        return this.comments;
    }

    public List<String> getDeleteCommentsList() {
        return this.deleteCommentsList;
    }

    public void setDeleteCommentsList(List<String> deleteCommentsList) {
        this.deleteCommentsList = deleteCommentsList;
    }
}

