/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.mail.MailMessage
 *  com.atlassian.bitbucket.mail.MailMessage$Builder
 *  com.atlassian.bitbucket.mail.MailService
 *  com.atlassian.bitbucket.server.ApplicationPropertiesService
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm.mail;

import com.atlassian.bitbucket.mail.MailMessage;
import com.atlassian.bitbucket.mail.MailService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.mail.ProductMailService;
import com.atlassian.upm.mail.UpmEmail;
import java.util.Objects;

public class BitbucketMailService
implements ProductMailService {
    public static final String CONTENT_TYPE = "Content-Type";
    private final MailService mailService;
    private final ApplicationPropertiesService applicationPropertiesService;

    public BitbucketMailService(MailService mailService, ApplicationPropertiesService applicationPropertiesService) {
        this.mailService = Objects.requireNonNull(mailService, "mailService");
        this.applicationPropertiesService = Objects.requireNonNull(applicationPropertiesService, "applicationPropertiesService");
    }

    @Override
    public boolean isConfigured() {
        return this.mailService.isHostConfigured();
    }

    @Override
    public boolean isDisabled() {
        return !this.isConfigured();
    }

    @Override
    public void sendMail(UpmEmail email) {
        this.mailService.submit(this.mailMessage(email));
    }

    @Override
    public UpmEmail.Format getUserEmailFormatPreference(UserKey userKey) {
        return UpmEmail.Format.HTML;
    }

    @Override
    public Option<String> getInstanceName() {
        return Option.option(this.applicationPropertiesService.getDisplayName());
    }

    private MailMessage mailMessage(UpmEmail email) {
        MailMessage.Builder builder = new MailMessage.Builder();
        builder.to(email.getTo()).cc(email.getCc()).bcc(email.getBcc()).subject(email.getSubject()).text(email.getBody());
        builder.header(CONTENT_TYPE, email.getMimeTypeAndEncoding());
        for (String from : email.getFrom()) {
            builder.from(from);
        }
        for (String key : email.getHeaders().keySet()) {
            builder.header(key, email.getHeaders().get(key));
        }
        return builder.build();
    }
}

