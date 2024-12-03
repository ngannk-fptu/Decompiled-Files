/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crucible.spi.data.UserProfileData
 *  com.atlassian.crucible.spi.services.UserService
 *  com.atlassian.fisheye.spi.data.MailMessageData
 *  com.atlassian.sal.api.user.UserKey
 *  com.cenqua.fisheye.AppConfig
 *  com.cenqua.fisheye.config.SpringContext
 *  com.cenqua.fisheye.mail.Mailer
 *  io.atlassian.util.concurrent.LazyReference
 */
package com.atlassian.upm.mail;

import com.atlassian.crucible.spi.data.UserProfileData;
import com.atlassian.crucible.spi.services.UserService;
import com.atlassian.fisheye.spi.data.MailMessageData;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.mail.ProductMailService;
import com.atlassian.upm.mail.UpmEmail;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config.SpringContext;
import com.cenqua.fisheye.mail.Mailer;
import io.atlassian.util.concurrent.LazyReference;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FeCruMailService
implements ProductMailService {
    private LazyReference<Mailer> mailService;
    private final UserService userService;

    public FeCruMailService(UserService userService) {
        this.userService = Objects.requireNonNull(userService, "userService");
        this.mailService = new LazyReference<Mailer>(){

            protected Mailer create() {
                Class<?> rootConfigClass = AppConfig.getsConfig().getClass();
                try {
                    Method getMailerMethod = rootConfigClass.getMethod("getMailer", new Class[0]);
                    return (Mailer)getMailerMethod.invoke((Object)AppConfig.getsConfig(), new Object[0]);
                }
                catch (Exception e) {
                    return (Mailer)SpringContext.getComponentByClass(Mailer.class);
                }
            }
        };
    }

    private Mailer getMailer() {
        return (Mailer)this.mailService.get();
    }

    @Override
    public boolean isConfigured() {
        return this.getMailer().isConfigured();
    }

    @Override
    public boolean isDisabled() {
        return !this.isConfigured();
    }

    @Override
    public void sendMail(UpmEmail email) {
        this.getMailer().sendMessage(this.mailMessageData(email));
    }

    @Override
    public UpmEmail.Format getUserEmailFormatPreference(UserKey userKey) {
        try {
            UserProfileData userProfile = this.userService.getUserProfile(userKey.getStringValue());
            if (userProfile != null) {
                return "plainText".equals(userProfile.getPreferences().get("emailFormat")) ? UpmEmail.Format.TEXT : UpmEmail.Format.HTML;
            }
        }
        catch (Exception e) {
            return UpmEmail.Format.HTML;
        }
        return UpmEmail.Format.HTML;
    }

    @Override
    public Option<String> getInstanceName() {
        return Option.none();
    }

    private MailMessageData mailMessageData(UpmEmail email) {
        MailMessageData messageData = new MailMessageData();
        messageData.setSubject(email.getSubject());
        messageData.setBodyText(email.getMimeTypeAndEncoding(), email.getBody());
        for (String s : email.getFrom()) {
            messageData.setFrom(s);
        }
        for (String s : Stream.concat(email.getTo().stream(), email.getCc().stream()).collect(Collectors.toSet())) {
            messageData.addRecipient(s);
        }
        for (String s : email.getFromName()) {
            messageData.setFromDisplayName(s);
        }
        for (String key : email.getHeaders().keySet()) {
            messageData.addHeader(key, email.getHeaders().get(key));
        }
        return messageData;
    }
}

