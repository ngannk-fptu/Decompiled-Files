/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pats.notifications.mail;

import com.atlassian.pats.api.TokenMailSenderService;
import com.atlassian.pats.core.properties.SystemProperty;
import com.atlassian.pats.events.token.TokenEvent;
import com.atlassian.pats.notifications.mail.MailRenderer;
import com.atlassian.pats.notifications.mail.TokenMail;
import com.atlassian.pats.notifications.mail.services.ProductMailService;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTokenMailSenderService
implements TokenMailSenderService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultTokenMailSenderService.class);
    private final ProductMailService productMailService;
    private final MailRenderer mailRenderer;

    public DefaultTokenMailSenderService(ProductMailService productMailService, MailRenderer mailRenderer) {
        this.productMailService = productMailService;
        this.mailRenderer = mailRenderer;
    }

    @Override
    public void sendTokenEventMail(@Nonnull TokenEvent event) {
        if (this.areMailNotificationsEnabled()) {
            this.sendMail(event);
        }
    }

    private boolean areMailNotificationsEnabled() {
        if (!SystemProperty.MAIL_NOTIFICATIONS_ENABLED.getValue().booleanValue()) {
            logger.debug("Mail notifications disabled for PATs!");
            return false;
        }
        if (this.productMailService.isDisabled() || !this.productMailService.isConfigured()) {
            logger.debug("Mail notifications disabled for the platform!");
            return false;
        }
        return true;
    }

    private void sendMail(TokenEvent event) {
        try {
            logger.trace("Creating email for event: [{}]", (Object)event);
            TokenMail mail = this.mailRenderer.tokenEvent(event);
            this.productMailService.sendMail(mail);
        }
        catch (Exception e) {
            logger.trace("Error creating/sending email for event: ", (Throwable)e);
            logger.warn(String.format("Error creating/sending email for event: [%s] with msg: [%s]", event, e.getMessage()));
        }
    }
}

