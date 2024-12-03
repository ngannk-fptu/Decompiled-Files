/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.spaces.actions.SpaceAdministrative;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.user.User;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSpaceAction
extends ConfluenceActionSupport
implements Spaced {
    private static final Logger log = LoggerFactory.getLogger(AbstractSpaceAction.class);
    protected SpaceManager spaceManager;
    protected String key;
    protected Space space;
    protected WikiStyleRenderer wikiStyleRenderer;
    protected NotificationManager notificationManager;
    protected ThemeManager themeManager;
    protected EventPublisher eventPublisher;

    public String getKey() {
        return this.key;
    }

    public String getSpaceKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key.trim();
        this.space = null;
    }

    public boolean isValidSpaceKey() {
        return Space.isValidSpaceKey(this.key);
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public Space getSpace() {
        if (this.space == null && this.isValidSpaceKey()) {
            this.setSpace(this.spaceManager.getSpace(this.key));
        }
        return this.space;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    @Override
    protected List<String> getPermissionTypes() {
        List<String> permissionTypes = super.getPermissionTypes();
        if (this.getSpace() != null) {
            this.addPermissionTypeTo("VIEWSPACE", permissionTypes);
            if (this instanceof SpaceAdministrative) {
                this.addPermissionTypeTo("SETSPACEPERMISSIONS", permissionTypes);
            }
        }
        return permissionTypes;
    }

    @Override
    public boolean isPermitted() {
        return this.spacePermissionManager.hasAllPermissions(this.getPermissionTypes(), this.getSpace(), this.getAuthenticatedUser());
    }

    public String getSubscribableName() {
        return this.getKey();
    }

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    public boolean isUserWatchingSpace() {
        if (this.isAnonymousUser()) {
            return false;
        }
        return this.notificationManager.getNotificationByUserAndSpace((User)this.getAuthenticatedUser(), this.getSpace()) != null;
    }

    public boolean isPersonalSpace() {
        return this.getSpace().isPersonal();
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    public ThemeManager getThemeManager() {
        return this.themeManager;
    }

    public SpaceManager getSpaceManager() {
        return this.spaceManager;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    protected boolean hasSpaceIA() {
        if (this.themeManager == null) {
            return false;
        }
        Theme theme = this.themeManager.getSpaceTheme(this.key);
        return theme != null && theme.hasSpaceSideBar();
    }
}

