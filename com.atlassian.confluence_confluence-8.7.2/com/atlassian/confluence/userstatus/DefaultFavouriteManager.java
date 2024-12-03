/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.userstatus;

import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.LabelPermissionEnforcer;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.confluence.userstatus.FavouriteManager;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.dao.DataAccessException;

public class DefaultFavouriteManager
implements FavouriteManager {
    private SpaceManager spaceManager;
    private LabelManager labelManager;
    private LabelPermissionEnforcer labelPermissionEnforcer;

    public DefaultFavouriteManager(SpaceManager spaceManager, LabelManager labelManager, LabelPermissionEnforcer labelPermissionEnforcer) {
        this.spaceManager = spaceManager;
        this.labelManager = labelManager;
        this.labelPermissionEnforcer = labelPermissionEnforcer;
    }

    @Override
    public boolean isUserFavourite(User user, Space space) {
        if (user == null || space == null) {
            return false;
        }
        List<Label> labels = space.getDescription().getLabels();
        Label favLabel = new Label("favourite", Namespace.PERSONAL, DefaultFavouriteManager.findConfluenceUser(user));
        Label favYankeeLabel = new Label("favorite", Namespace.PERSONAL, DefaultFavouriteManager.findConfluenceUser(user));
        return labels.contains(favLabel) || labels.contains(favYankeeLabel);
    }

    @Override
    public void addSpaceToFavourites(User user, Space space) throws NotAuthorizedException, DataAccessException {
        Preconditions.checkNotNull((Object)space);
        this.checkPermission(user, space.getDescription());
        if (space.getDescription() == null) {
            SpaceDescription spaceDescription = new SpaceDescription();
            spaceDescription.setSpace(space);
            spaceDescription.setBodyAsString("");
            space.setDescription(spaceDescription);
            this.spaceManager.saveSpace(space);
        }
        Label label = new Label("favourite", Namespace.PERSONAL, DefaultFavouriteManager.findConfluenceUser(user));
        this.labelManager.addLabel(space.getDescription(), label);
    }

    @Override
    public void removeSpaceFromFavourites(User user, Space space) throws NotAuthorizedException, DataAccessException {
        Preconditions.checkNotNull((Object)space);
        this.checkPermission(user, space.getDescription());
        this.removeSpaceFromFavourites(user, space, "favourite");
        this.removeSpaceFromFavourites(user, space, "favorite");
    }

    private void removeSpaceFromFavourites(User user, Space space, String labelName) throws NotAuthorizedException, DataAccessException {
        Label label = this.labelManager.getLabel("~" + user.getName() + ":" + labelName);
        if (label == null) {
            return;
        }
        this.labelManager.removeLabel(space.getDescription(), label);
        LabelUtil.recordLabelInteractionInHistory(label);
    }

    @Override
    public boolean isUserFavourite(User user, AbstractPage page) {
        if (user == null || page == null) {
            return false;
        }
        Label favLabel = new Label("favourite", Namespace.PERSONAL, DefaultFavouriteManager.findConfluenceUser(user));
        Label favYankeeLabel = new Label("favorite", Namespace.PERSONAL, DefaultFavouriteManager.findConfluenceUser(user));
        return page.getLabels().contains(favLabel) || page.getLabels().contains(favYankeeLabel);
    }

    @Override
    public void addPageToFavourites(User user, AbstractPage page) throws NotAuthorizedException, DataAccessException {
        Preconditions.checkNotNull((Object)page);
        this.checkPermission(user, page);
        Label label = new Label("favourite", Namespace.PERSONAL, DefaultFavouriteManager.findConfluenceUser(user));
        LabelUtil.addLabel(label.toStringWithNamespace(), this.labelManager, page, DefaultFavouriteManager.findConfluenceUser(user));
    }

    @Override
    public void removePageFromFavourites(User user, AbstractPage page) throws NotAuthorizedException, DataAccessException {
        Preconditions.checkNotNull((Object)page);
        this.checkPermission(user, page);
        this.removePageFromFavourites(user, page, "favourite");
        this.removePageFromFavourites(user, page, "favorite");
    }

    private void removePageFromFavourites(User user, AbstractPage page, String labelName) throws NotAuthorizedException, DataAccessException {
        Label label = new Label(labelName, Namespace.PERSONAL, DefaultFavouriteManager.findConfluenceUser(user));
        this.labelManager.removeLabel(page, label);
    }

    @Override
    public boolean hasPermission(User user, Space space) {
        return this.hasPermissionImpl(user, space.getDescription());
    }

    @Override
    public boolean hasPermission(User user, AbstractPage page) {
        return this.hasPermissionImpl(user, page);
    }

    private void checkPermission(User user, SpaceContentEntityObject labelable) {
        if (!this.hasPermissionImpl(user, labelable)) {
            ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
            throw new NotAuthorizedException(currentUser != null ? currentUser.getName() : null);
        }
    }

    private boolean hasPermissionImpl(User user, SpaceContentEntityObject labelable) {
        Label label = new Label("favourite", Namespace.PERSONAL, DefaultFavouriteManager.findConfluenceUser(user));
        return this.labelPermissionEnforcer.userCanEditLabelOrIsSpaceAdmin(label, labelable);
    }

    private static @Nullable ConfluenceUser findConfluenceUser(User user) {
        return user instanceof ConfluenceUser ? (ConfluenceUser)user : (user != null ? DefaultFavouriteManager.findUserByUsername(user.getName()) : null);
    }

    private static @Nullable ConfluenceUser findUserByUsername(String username) {
        if (username == null) {
            return null;
        }
        ConfluenceUser user = FindUserHelper.getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("No user could be found with the username " + username);
        }
        return user;
    }
}

