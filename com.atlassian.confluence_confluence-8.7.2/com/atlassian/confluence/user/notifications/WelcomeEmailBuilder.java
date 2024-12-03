/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user.notifications;

import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.mail.notification.listeners.NotificationData;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserVerificationTokenManager;
import com.atlassian.confluence.user.UserVerificationTokenType;
import com.atlassian.confluence.util.HtmlUtil;
import javax.activation.DataSource;
import org.apache.commons.lang3.StringUtils;

public class WelcomeEmailBuilder {
    private final SettingsManager settingsManager;
    private final DataSourceFactory dataSourceFactory;
    private final UserVerificationTokenManager tokenManager;

    public WelcomeEmailBuilder(SettingsManager settingsManager, DataSourceFactory dataSourceFactory, UserVerificationTokenManager tokenManager) {
        this.settingsManager = settingsManager;
        this.dataSourceFactory = dataSourceFactory;
        this.tokenManager = tokenManager;
    }

    public NotificationData buildFrom(ConfluenceUser user) {
        NotificationData notificationData = new NotificationData(user, false, null);
        notificationData.setSubject("$i18nBean.getText(\"create.user.email.subject\", \"$instanceName\")");
        notificationData.setTemplateName("create-user-notification.vm");
        return notificationData;
    }

    public NotificationContext buildContextFrom(ConfluenceUser user, NotificationData notificationData) {
        String siteName = this.getSiteTitle();
        String username = user.getName();
        String baseUrl = this.getBaseUrl();
        ConfluenceUser inviter = AuthenticatedUserThreadLocal.get();
        DataSource avatarDataSource = this.dataSourceFactory.getAvatar(inviter);
        NotificationContext context = notificationData.cloneContext();
        context.addTemplateImage(avatarDataSource);
        context.put("avatarCid", avatarDataSource.getName());
        String token = this.tokenManager.generateAndSaveToken(username, UserVerificationTokenType.USER_SIGNUP);
        context.put("inviterFullname", inviter.getFullName());
        context.put("instanceName", siteName);
        context.put("siteUrl", baseUrl);
        context.put("newUserUsername", username);
        context.put("baseurl", baseUrl);
        context.put("newUserFirstname", StringUtils.capitalize((String)StringUtils.substringBefore((String)user.getFullName(), (String)" ")));
        context.put("resetPasswordLink", this.getChangePasswordLink(token, username));
        return context;
    }

    private String getSiteTitle() {
        String siteName = this.settingsManager.getGlobalSettings().getSiteTitle();
        if (siteName == null) {
            siteName = "Confluence";
        }
        return siteName;
    }

    private String getBaseUrl() {
        return this.settingsManager.getGlobalSettings().getBaseUrl();
    }

    private String getChangePasswordLink(String token, String username) {
        return "/resetuserpassword.action?username=" + HtmlUtil.urlEncode(username) + "&token=" + token;
    }
}

