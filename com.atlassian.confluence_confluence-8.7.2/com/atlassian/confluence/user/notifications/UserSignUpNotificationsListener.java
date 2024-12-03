/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.notifications;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.event.events.user.GroupInviteUserSignupEvent;
import com.atlassian.confluence.event.events.user.UserEvent;
import com.atlassian.confluence.event.events.user.UserSignupEvent;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.mail.notification.listeners.AbstractNotificationsListener;
import com.atlassian.confluence.mail.notification.listeners.NotificationData;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.user.SignupManager;
import com.atlassian.confluence.util.ConfluenceRenderUtils;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class UserSignUpNotificationsListener
extends AbstractNotificationsListener<UserEvent> {
    private static final Logger log = LoggerFactory.getLogger(UserSignUpNotificationsListener.class);
    private SpacePermissionManager spacePermissionManager;
    private UserChecker userChecker;
    private SignupManager signupManager;
    private LicenseService licenseService;

    @Override
    protected ContentEntityObject getContentEntityObject(Map contextMap) {
        return null;
    }

    @Override
    public void processNotifications(UserEvent event) {
        if (this.signupManager.isEmailSentOnInviteSignUp()) {
            NotificationData notificationData = this.getNotificationDataForEvent(event);
            this.attachAvatar(notificationData);
            this.sendAdminNotifications(notificationData);
        }
    }

    private NotificationData getNotificationDataForEvent(UserEvent userEvent) {
        User user = userEvent.getUser();
        NotificationData notificationData = new NotificationData(user, true, null);
        notificationData.setSubject("$i18n.getText('email.user.signed.up', $modifier.fullName)");
        notificationData.setTemplateName("user-signup-notification.vm");
        int currentUsers = this.userChecker.getNumberOfRegisteredUsers();
        if (!this.licenseService.retrieve().isUnlimitedNumberOfUsers()) {
            notificationData.addToContext("showUserCount", Boolean.valueOf(true));
            notificationData.addToContext("currentUsers", Integer.valueOf(currentUsers));
            notificationData.addToContext("licensedUsers", Integer.valueOf(this.countLicensedUsers()));
        }
        notificationData.addToContext("privateSignUp", Boolean.valueOf(userEvent instanceof GroupInviteUserSignupEvent));
        notificationData.addToContext("signedupUser", (Serializable)user);
        notificationData.addToContext("isUserSignupNotification", Boolean.valueOf(true));
        notificationData.addToContext("manageNotificationsOverride", Boolean.valueOf(true));
        notificationData.addAllToContext(this.getInitialContext());
        return notificationData;
    }

    private int countLicensedUsers() {
        ConfluenceLicense confluenceLicense = this.licenseService.retrieve();
        return confluenceLicense.getMaximumNumberOfUsers();
    }

    private void sendAdminNotifications(NotificationData notificationData) {
        HashSet recipients = Sets.newHashSet();
        List<SpacePermission> permissions = this.spacePermissionManager.getGlobalPermissions("ADMINISTRATECONFLUENCE");
        permissions.addAll(this.spacePermissionManager.getGlobalPermissions("SYSTEMADMINISTRATOR"));
        for (SpacePermission permission : permissions) {
            Group group;
            if (permission.getUserName() != null) {
                recipients.add(permission.getUserName());
            }
            if (permission.getGroup() == null || (group = this.userAccessor.getGroup(permission.getGroup())) == null) continue;
            recipients.addAll(this.userAccessor.getMemberNamesAsList(group));
        }
        log.info("Sending user notifications for '{}' to {} people.", (Object)notificationData.getSubject(), (Object)recipients.size());
        for (String recipient : recipients) {
            this.sendNotification(recipient, notificationData.cloneContext(), notificationData);
        }
    }

    private Map<String, Serializable> getInitialContext() {
        HashMap<String, Serializable> context = new HashMap<String, Serializable>();
        String domainName = GeneralUtil.getGlobalSettings().getBaseUrl();
        if (StringUtils.isNotEmpty((CharSequence)domainName) && domainName.endsWith("/")) {
            domainName = domainName.substring(0, domainName.length() - 1);
        }
        context.put("baseurl", (Serializable)((Object)domainName));
        BootstrapManager bootstrapManager = (BootstrapManager)BootstrapUtils.getBootstrapManager();
        Object contextPath = bootstrapManager.getWebAppContextPath();
        if (StringUtils.isNotEmpty((CharSequence)contextPath) && !((String)contextPath).startsWith("/")) {
            contextPath = "/" + (String)contextPath;
        }
        context.put("contextPath", (Serializable)contextPath);
        context.put("stylesheet", (Serializable)((Object)ConfluenceRenderUtils.renderDefaultStylesheet()));
        return context;
    }

    public Class[] getHandledEventClasses() {
        return new Class[]{UserSignupEvent.class};
    }

    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    @Override
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    public void setUserChecker(UserChecker userChecker) {
        this.userChecker = userChecker;
    }

    public void setSignupManager(SignupManager easyUserManager) {
        this.signupManager = easyUserManager;
    }

    public void setLicenseService(LicenseService licenseService) {
        this.licenseService = licenseService;
    }
}

