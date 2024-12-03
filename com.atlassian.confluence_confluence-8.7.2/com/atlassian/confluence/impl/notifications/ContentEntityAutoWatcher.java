/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.impl.notifications;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserPreferencesAccessor;
import com.atlassian.event.api.EventListener;
import java.util.Objects;

public final class ContentEntityAutoWatcher {
    private final UserPreferencesAccessor userPreferencesAccessor;
    private final NotificationManager notificationManager;

    public ContentEntityAutoWatcher(UserPreferencesAccessor userPreferencesAccessor, NotificationManager notificationManager) {
        this.userPreferencesAccessor = Objects.requireNonNull(userPreferencesAccessor);
        this.notificationManager = Objects.requireNonNull(notificationManager);
    }

    @EventListener
    public void onEvent(AutowatchIfRequiredEvent event) {
        this.autowatchIfRequired(event.getContentEntity(), event.getSaveContext());
    }

    public void autowatchIfRequired(ContentEntityObject ceo, SaveContext saveContext) {
        ConfluenceUser user;
        if (saveContext.isSuppressAutowatch()) {
            return;
        }
        if (!(ceo instanceof AbstractPage) || ceo.isDraft()) {
            return;
        }
        ConfluenceUser confluenceUser = user = ceo.getLastModifier() != null ? ceo.getLastModifier() : ceo.getCreator();
        if (user == null) {
            return;
        }
        if (!this.userPreferencesAccessor.getConfluenceUserPreferences(user).isWatchingOwnContent()) {
            return;
        }
        this.notificationManager.addContentNotification(user, ceo);
    }

    public static class AutowatchIfRequiredEvent {
        final ContentEntityObject ceo;
        final SaveContext saveContext;

        public AutowatchIfRequiredEvent(ContentEntityObject ceo, SaveContext saveContext) {
            this.ceo = Objects.requireNonNull(ceo);
            this.saveContext = Objects.requireNonNull(saveContext);
        }

        public ContentEntityObject getContentEntity() {
            return this.ceo;
        }

        public SaveContext getSaveContext() {
            return this.saveContext;
        }
    }
}

