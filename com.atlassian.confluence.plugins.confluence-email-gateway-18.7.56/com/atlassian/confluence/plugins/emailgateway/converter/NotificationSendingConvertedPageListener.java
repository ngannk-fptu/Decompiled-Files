/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.user.SendUserInviteEvent
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.plugins.sharepage.api.SharePageService
 *  com.atlassian.confluence.plugins.sharepage.api.ShareRequest
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.SignupManager
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.google.common.collect.Sets
 *  javax.mail.internet.InternetAddress
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.emailgateway.converter;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.user.SendUserInviteEvent;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.emailgateway.api.EmailContentParser;
import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThread;
import com.atlassian.confluence.plugins.emailgateway.api.UsersByEmailService;
import com.atlassian.confluence.plugins.emailgateway.service.EmailThreadConvertedEvent;
import com.atlassian.confluence.plugins.sharepage.api.SharePageService;
import com.atlassian.confluence.plugins.sharepage.api.ShareRequest;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.SignupManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.event.api.EventListener;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.mail.internet.InternetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationSendingConvertedPageListener {
    private static final Logger log = LoggerFactory.getLogger(NotificationSendingConvertedPageListener.class);
    private final I18NBeanFactory i18NBeanFactory;
    private final SettingsManager settingsManager;
    private final SignupManager signupManager;
    private final LocaleManager localeManager;
    private final SharePageService sharePageService;
    private final NotificationManager notificationManager;
    private final UsersByEmailService usersByEmailService;
    private final EmailContentParser parser;

    public NotificationSendingConvertedPageListener(I18NBeanFactory i18NBeanFactory, SettingsManager settingsManager, SignupManager signupManager, LocaleManager localeManager, SharePageService sharePageService, NotificationManager notificationManager, UsersByEmailService usersByEmailService, EmailContentParser parser) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.settingsManager = settingsManager;
        this.signupManager = signupManager;
        this.localeManager = localeManager;
        this.sharePageService = sharePageService;
        this.notificationManager = notificationManager;
        this.usersByEmailService = usersByEmailService;
        this.parser = parser;
    }

    @EventListener
    public void sendNotifications(EmailThreadConvertedEvent event) throws EntityException {
        StagedEmailThread emailThread = event.getEmailThread();
        ContentEntityObject content = event.getContent();
        if (!(content instanceof AbstractPage)) {
            log.debug("Ignoring created content: {}", (Object)content.getIdAsString());
            return;
        }
        AbstractPage page = (AbstractPage)content;
        ConfluenceUser creator = page.getCreator();
        List<InternetAddress> usersFromContent = this.parser.getEmailAddressesFromContent(emailThread.getBodyContentAsString());
        usersFromContent.addAll(emailThread.getParticipants());
        HashSet notYetUsers = Sets.newHashSet();
        HashSet existingUsers = Sets.newHashSet();
        this.separateInternetAddressesIntoUsers(usersFromContent, notYetUsers, existingUsers);
        existingUsers.remove(creator);
        this.sendInvites(page, (User)creator, notYetUsers);
        this.addSharesAndWatches(page, existingUsers);
    }

    private void separateInternetAddressesIntoUsers(List<InternetAddress> usersFromContent, Collection<String> notYetUsers, Collection<User> existingUsers) throws EntityException {
        for (InternetAddress participant : usersFromContent) {
            User user = this.usersByEmailService.getUniqueUserByEmail(participant);
            if (user != null) {
                existingUsers.add(user);
                continue;
            }
            notYetUsers.add(participant.toString());
        }
    }

    public void sendInvites(AbstractPage page, User creator, Collection<String> notYetUsers) {
        if (!notYetUsers.isEmpty()) {
            I18NBean i18NBean = this.getI18nBean(creator);
            String urlPath = this.settingsManager.getGlobalSettings().getBaseUrl() + page.getUrlPath();
            String message = i18NBean.getText("email.to.page.invitation.text", (Object[])new String[]{page.getTitle(), urlPath});
            SendUserInviteEvent event = new SendUserInviteEvent((Object)this, creator, message, new ArrayList<String>(notYetUsers));
            this.signupManager.sendInvites(event);
        }
    }

    public void addSharesAndWatches(AbstractPage createdPage, Collection<User> existingUsers) {
        for (User existingUser : existingUsers) {
            ShareRequest shareRequest = new ShareRequest();
            shareRequest.setUsers(Collections.singleton(existingUser.getName()));
            shareRequest.setEmails(Collections.emptySet());
            shareRequest.setEntityId(Long.valueOf(createdPage.getId()));
            shareRequest.setNote(this.getI18nBean(existingUser).getText("email.to.page.share.text"));
            this.sharePageService.share(shareRequest);
            this.notificationManager.addContentNotification(existingUser, (ContentEntityObject)createdPage);
        }
    }

    private I18NBean getI18nBean(User user) {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale(user));
    }
}

