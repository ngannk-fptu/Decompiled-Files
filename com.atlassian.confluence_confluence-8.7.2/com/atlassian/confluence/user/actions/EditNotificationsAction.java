/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 */
package com.atlassian.confluence.user.actions;

import bucket.core.actions.PaginationSupport;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked;
import com.atlassian.confluence.event.events.profile.ViewMyWatchesEvent;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.actions.PageAware;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.confluence.user.notifications.AddWatchLink;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ReadOnlyAccessBlocked
public class EditNotificationsAction
extends AbstractUserProfileAction
implements SpaceAware,
PageAware {
    private static final int PAGE_SIZE = 20;
    private long pageId;
    private AbstractPage page;
    private String spaceKey;
    private Space space;
    private ContentTypeEnum contentType;
    private boolean changesSaved = false;
    private AddWatchLink undoLink;
    private List<Notification> spaceNotificationsForUser;
    private List<Notification> pageNotificationsForUser;
    private EventPublisher eventPublisher;
    private final PaginationSupport paginationSupport = new PaginationSupport(20);

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public PaginationSupport getPaginationSupport() {
        return this.paginationSupport;
    }

    public void setStartIndex(int startIndex) {
        this.getPaginationSupport().setStartIndex(startIndex);
    }

    public int getPageSize() {
        return 20;
    }

    public List getPaginatedItems() {
        return this.paginationSupport.getPage();
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String doAddSpaceNotification() {
        if (this.validateSpace()) {
            this.notificationManager.addSpaceNotification(this.getUser(), this.space, this.contentType);
            return "success";
        }
        return "input";
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String doRemoveSpaceNotification() {
        if (this.validateSpace()) {
            Notification notification = this.notificationManager.getNotificationByUserAndSpaceAndType(this.getUser(), this.space, this.contentType);
            if (notification != null) {
                this.notificationManager.removeNotification(notification);
                this.undoLink = new AddWatchLink(this.space, this.contentType);
                return "success";
            }
            return "input";
        }
        return "input";
    }

    private boolean validateSpace() {
        if (this.space == null) {
            if (this.spaceKey == null) {
                this.addActionError("no.space.specified", new Object[0]);
            } else {
                this.addActionError("space.not.found", this.spaceKey);
            }
            return false;
        }
        return true;
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String doAddPageNotification() {
        if (this.validatePage()) {
            this.notificationManager.addContentNotification(this.getUser(), this.getPage());
            return "success";
        }
        return "input";
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String doRemovePageNotification() {
        if (this.validatePage()) {
            Notification notification = this.notificationManager.getNotificationByUserAndContent(this.getUser(), this.getPage());
            if (notification != null) {
                this.notificationManager.removeNotification(notification);
                this.undoLink = new AddWatchLink(this.page);
                return "success";
            }
            return "input";
        }
        return "input";
    }

    private boolean validatePage() {
        if (this.page == null) {
            if (this.getPageId() == 0L) {
                this.addActionError("no.page.specified", new Object[0]);
            } else {
                this.addActionError("no.page.found.for.id", this.getPageId());
            }
            return false;
        }
        return true;
    }

    public List getPageNotificationsForUser() {
        if (this.pageNotificationsForUser == null) {
            this.loadNotificationsForUser();
        }
        return this.pageNotificationsForUser.isEmpty() ? Collections.emptyList() : this.pageNotificationsForUser;
    }

    public List getSpaceNotificationsForUser() {
        if (this.spaceNotificationsForUser == null) {
            this.loadNotificationsForUser();
        }
        return this.spaceNotificationsForUser.isEmpty() ? Collections.emptyList() : this.spaceNotificationsForUser;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        ViewMyWatchesEvent event = new ViewMyWatchesEvent(this);
        this.eventPublisher.publish((Object)event);
        this.resetPagination();
        return super.execute();
    }

    public void resetPagination() {
        this.getPaginationSupport().setItems(this.getPageNotificationsForUser());
    }

    private void loadNotificationsForUser() {
        List<Notification> notificationsForUser = this.notificationManager.getNotificationsByUser(this.getUser());
        this.pageNotificationsForUser = new ArrayList<Notification>();
        this.spaceNotificationsForUser = new ArrayList<Notification>();
        for (Notification notification : notificationsForUser) {
            if (notification.isPageNotification()) {
                this.pageNotificationsForUser.add(notification);
                continue;
            }
            if (!notification.isSpaceNotification()) continue;
            this.spaceNotificationsForUser.add(notification);
        }
    }

    @Override
    public AbstractPage getPage() {
        return this.page;
    }

    @Override
    public void setPage(AbstractPage page) {
        this.page = page;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getSpaceName() {
        if (this.getSpace() != null) {
            return this.space.getName();
        }
        return null;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public AddWatchLink getUndoLink() {
        return this.undoLink;
    }

    @Override
    public boolean isPageRequired() {
        return false;
    }

    @Override
    public boolean isLatestVersionRequired() {
        return false;
    }

    @Override
    public boolean isPermitted() {
        return this.getUsername() != null && super.isPermitted();
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public long getPageId() {
        return this.pageId;
    }

    public boolean isChangesSaved() {
        return this.changesSaved;
    }

    public void setChangesSaved(boolean changesSaved) {
        this.changesSaved = changesSaved;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public boolean isSpaceRequired() {
        return false;
    }

    @Override
    public Space getSpace() {
        return this.space;
    }

    public void setContentType(String contentType) {
        this.contentType = ContentTypeEnum.getByRepresentation(contentType);
    }
}

