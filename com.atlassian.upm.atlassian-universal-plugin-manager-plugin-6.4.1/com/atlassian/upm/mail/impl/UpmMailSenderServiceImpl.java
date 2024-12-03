/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.mail.impl;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.upm.Pairs;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmSettings;
import com.atlassian.upm.UserSettings;
import com.atlassian.upm.UserSettingsStore;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.mail.EmailType;
import com.atlassian.upm.mail.MailRenderer;
import com.atlassian.upm.mail.ProductMailService;
import com.atlassian.upm.mail.UpmEmail;
import com.atlassian.upm.mail.UpmMailHeaderGenerator;
import com.atlassian.upm.mail.UpmMailSenderService;
import com.atlassian.upm.rest.UpmUriBuilder;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpmMailSenderServiceImpl
implements UpmMailSenderService {
    private static final Logger logger = LoggerFactory.getLogger(UpmMailSenderServiceImpl.class);
    private final ProductMailService mailService;
    private final MailRenderer renderer;
    private final UserManager userManager;
    private final SysPersisted sysPersisted;
    private final UserSettingsStore userSettingsStore;
    private final UpmUriBuilder uriBuilder;
    private final ApplicationProperties applicationProperties;
    private final I18nResolver i18nResolver;

    public UpmMailSenderServiceImpl(ProductMailService mailService, MailRenderer renderer, UserManager userManager, SysPersisted sysPersisted, UserSettingsStore userSettingsStore, UpmUriBuilder uriBuilder, ApplicationProperties applicationProperties, I18nResolver i18nResolver) {
        this.mailService = Objects.requireNonNull(mailService, "mailService");
        this.renderer = Objects.requireNonNull(renderer, "renderer");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.sysPersisted = Objects.requireNonNull(sysPersisted, "sysPersisted");
        this.userSettingsStore = Objects.requireNonNull(userSettingsStore, "userSettingsStore");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
    }

    @Override
    public boolean canSendEmail() {
        return !this.sysPersisted.is(UpmSettings.EMAIL_DISABLED) && this.mailService.isConfigured() && !this.mailService.isDisabled();
    }

    @Override
    public void sendUpmEmail(EmailType mailType, Pairs.Pair<String, String> pluginKeyAndName, Set<UserKey> recipients, List<String> subjectArgs, Map<String, Object> bodyContext) {
        Set filteredRecipients = recipients.stream().map(arg_0 -> ((UserManager)this.userManager).getUserProfile(arg_0)).filter(Objects::nonNull).filter(user -> !StringUtils.isBlank((CharSequence)user.getEmail()) && !this.userSettingsStore.getBoolean(user.getUserKey(), UserSettings.DISABLE_EMAIL)).collect(Collectors.toSet());
        String subject = this.renderer.renderEmailSubject(mailType, subjectArgs);
        Option<UserProfile> senderProfile = this.getSenderProfile();
        for (UserProfile recipient : filteredRecipients) {
            UpmEmail.Format emailFormat = this.mailService.getUserEmailFormatPreference(recipient.getUserKey());
            String body = this.renderer.renderEmailBody(mailType, emailFormat, this.buildContext(mailType, senderProfile, recipient, pluginKeyAndName, bodyContext));
            UpmEmail.Builder email = UpmEmail.builder(subject, body).fromName(Option.some(this.i18nResolver.getText("upm.marketplace.title", new Serializable[]{this.applicationProperties.getDisplayName()}))).addTo(recipient.getEmail()).addHeaders(UpmMailHeaderGenerator.generateMailHeader(mailType, pluginKeyAndName.getFirst())).mimeType(emailFormat.getMimeType()).encoding("UTF-8");
            for (UserProfile sender : senderProfile) {
                if (StringUtils.isBlank((CharSequence)sender.getEmail())) continue;
                email.addReplyTo(sender.getEmail());
            }
            try {
                this.mailService.sendMail(email.build());
            }
            catch (Exception e) {
                logger.warn("Failed to send email {} : {}", (Object)mailType, (Object)e.toString());
                logger.debug(e.toString(), (Throwable)e);
            }
        }
    }

    private Option<UserProfile> getSenderProfile() {
        return Option.option(this.userManager.getRemoteUser());
    }

    private Map<String, Object> buildContext(EmailType emailType, Option<UserProfile> senderProfile, UserProfile recipientProfile, Pairs.Pair<String, String> pluginKeyAndName, Map<String, Object> moreContext) {
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("recipient", recipientProfile);
        context.put("pluginKey", pluginKeyAndName.getFirst());
        context.put("pluginName", pluginKeyAndName.getSecond());
        context.put("userSettingsLink", this.uriBuilder.emailUri(this.uriBuilder.buildUpmUserSettingsUri(), emailType));
        context.put("singlePluginViewLink", this.uriBuilder.emailUri(this.uriBuilder.buildUpmSinglePluginViewUri(pluginKeyAndName.getFirst()), emailType));
        context.put("managePluginLink", this.uriBuilder.emailUri(this.uriBuilder.buildUpmTabPluginUri("manage", pluginKeyAndName.getFirst()), emailType));
        context.put("isOnDemand", false);
        for (String instanceName : this.mailService.getInstanceName()) {
            context.put("instanceName", instanceName);
        }
        for (UserProfile sender : senderProfile) {
            context.put("sender", sender);
        }
        context.putAll(moreContext);
        return Collections.unmodifiableMap(context);
    }
}

