/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pats.notifications.mail;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.pats.core.properties.SystemProperty;
import com.atlassian.pats.events.token.TokenCreatedEvent;
import com.atlassian.pats.events.token.TokenDeletedEvent;
import com.atlassian.pats.events.token.TokenEvent;
import com.atlassian.pats.events.token.TokenExpireSoonEvent;
import com.atlassian.pats.events.token.TokenExpiredEvent;
import com.atlassian.pats.notifications.mail.MailSendingException;
import com.atlassian.pats.notifications.mail.MailStyleLoader;
import com.atlassian.pats.notifications.mail.TokenMail;
import com.atlassian.pats.utils.ProductHelper;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailRenderer {
    private static final Logger logger = LoggerFactory.getLogger(MailRenderer.class);
    static final String TOKEN_CREATED_SUBJECT_KEY = "personal.access.tokens.notifications.mail.created.subject";
    static final String TOKEN_DELETED_SUBJECT_KEY = "personal.access.tokens.notifications.mail.deleted.subject";
    static final String TOKEN_EXPIRE_SOON_SUBJECT_KEY = "personal.access.tokens.notifications.mail.expire.soon.subject";
    static final String TOKEN_EXPIRED_SUBJECT_KEY = "personal.access.tokens.notifications.mail.expired.subject";
    private static final String EMAIL_TEMPLATES_FILE_KEY = "com.atlassian.pats.pats-plugin:personal-access-tokens-plugin-email-templates";
    private static final String EMAIL_TEMPLATES_NAMESPACE = "Personal.Access.Tokens.Templates.Email.";
    private final I18nResolver i18nResolver;
    private final SoyTemplateRenderer templateRenderer;
    private final UserManager userManager;
    private final MailStyleLoader mailStyleLoader;
    private final ProductHelper productHelper;

    public MailRenderer(I18nResolver i18nResolver, SoyTemplateRenderer templateRenderer, UserManager userManager, MailStyleLoader mailStyleLoader, ProductHelper productHelper) {
        this.i18nResolver = i18nResolver;
        this.templateRenderer = templateRenderer;
        this.userManager = userManager;
        this.mailStyleLoader = mailStyleLoader;
        this.productHelper = productHelper;
    }

    public TokenMail tokenEvent(@Nonnull TokenEvent event) {
        if (event instanceof TokenCreatedEvent) {
            return this.getTokenMail(event, "created", TOKEN_CREATED_SUBJECT_KEY);
        }
        if (event instanceof TokenDeletedEvent) {
            return this.getTokenMail(event, event.getTriggeredBy() == null ? "deletedAutomatically" : "deletedManually", TOKEN_DELETED_SUBJECT_KEY);
        }
        if (event instanceof TokenExpireSoonEvent) {
            return this.getTokenMail(event, "expireSoon", TOKEN_EXPIRE_SOON_SUBJECT_KEY);
        }
        if (event instanceof TokenExpiredEvent) {
            return this.getTokenMail(event, "expired", TOKEN_EXPIRED_SUBJECT_KEY);
        }
        throw new IllegalArgumentException("Unknown event to handle: " + event);
    }

    private TokenMail getTokenMail(@Nonnull TokenEvent event, @Nonnull String templateSuffix, @Nonnull String subjectKey) {
        UserProfile userProfile = this.getUserProfile(event.getTokenOwnerId());
        logger.trace("For user: [{}], got profile: [{}]", (Object)event.getTokenOwnerId(), (Object)userProfile);
        StringWriter tempWriter = new StringWriter();
        this.templateRenderer.render((Appendable)tempWriter, EMAIL_TEMPLATES_FILE_KEY, EMAIL_TEMPLATES_NAMESPACE + templateSuffix, this.getContext(event));
        TokenMail tokenMail = new TokenMail(userProfile.getEmail(), this.i18nResolver.getText(subjectKey), this.mailStyleLoader.applyStyles(tempWriter.toString()));
        logger.trace("For event: [{}] created mail: [{}]", (Object)event, (Object)tokenMail);
        return tokenMail;
    }

    @VisibleForTesting
    Map<String, Object> getContext(TokenEvent event) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("userName", this.getUserProfile(event.getTokenOwnerId()).getFullName());
        result.put("triggeredBy", Optional.ofNullable(event.getTriggeredBy()).map(user -> this.getUserProfile((String)user).getFullName()).orElse(null));
        result.put("tokenName", event.getTokenName());
        result.put("manageUrl", this.productHelper.getTokensManageUrl());
        result.put("expiryDays", event.getExpiryDays());
        result.put("productType", this.productHelper.getProductName());
        result.put("baseUrl", StringUtils.removeEnd((String)this.productHelper.getBaseUrl(), (String)"/"));
        result.put("image", "/plugins/servlet/personal-tokens/product-logo.png");
        result.put("pruningDelayDays", SystemProperty.PRUNING_DELAY_DAYS.getValue());
        return result;
    }

    private UserProfile getUserProfile(@Nonnull String userKey) {
        return Optional.ofNullable(this.userManager.getUserProfile(new UserKey(userKey))).orElseThrow(() -> new MailSendingException("User profile does not exist for user: " + userKey));
    }
}

