/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.mail.Email
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  javax.ws.rs.core.UriBuilder
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.impl;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.mail.Email;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.troubleshooting.api.healthcheck.HealthCheckStatus;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.api.HealthCheckUserSettingsService;
import com.atlassian.troubleshooting.healthcheck.event.HealthcheckEmailSentAnalyticsEvent;
import com.atlassian.troubleshooting.healthcheck.event.NewHealthcheckFailureEvent;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthCheckWatcherService;
import com.atlassian.troubleshooting.healthcheck.util.SupportHealthCheckUtils;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.mail.MailUtility;
import com.atlassian.troubleshooting.stp.salext.mail.ProductAwareEmail;
import com.atlassian.troubleshooting.stp.scheduler.utils.RenderingUtils;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@ParametersAreNonnullByDefault
public class HealthCheckEmailNotifier {
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckEmailNotifier.class);
    private final EventPublisher eventPublisher;
    private final MailUtility mailUtility;
    private final HealthCheckWatcherService watcherService;
    private final UserManager userManager;
    private final SupportApplicationInfo info;
    private final HealthCheckUserSettingsService userSettingsService;

    @Autowired
    public HealthCheckEmailNotifier(EventPublisher eventPublisher, MailUtility mailUtility, HealthCheckWatcherService watcherService, UserManager userManager, SupportApplicationInfo info, HealthCheckUserSettingsService userSettingsService) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.mailUtility = Objects.requireNonNull(mailUtility);
        this.watcherService = Objects.requireNonNull(watcherService);
        this.userManager = Objects.requireNonNull(userManager);
        this.info = Objects.requireNonNull(info);
        this.userSettingsService = Objects.requireNonNull(userSettingsService);
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onNewHealthCheckFailure(NewHealthcheckFailureEvent event) throws IOException {
        if (this.userSettingsService.canWatch()) {
            HealthCheckStatus status = event.getStatus();
            String subject = this.info.getText("stp.health.watch.email.fail.subject", new Serializable[]{status.getName()});
            String mailBody = this.renderBody(status);
            List<Email> emails = this.watcherService.getAllWatchers().stream().map(arg_0 -> ((UserManager)this.userManager).getUserProfile(arg_0)).filter(Objects::nonNull).filter(u -> this.shouldNotify(status, u.getUserKey())).map(UserProfile::getEmail).filter(StringUtils::isNotBlank).map(to -> new ProductAwareEmail((String)to).addProductHeader(this.info.getApplicationName()).setFrom(this.info.getFromAddress()).setSubject(subject).setBody(mailBody).setMimeType("text/html")).collect(Collectors.toList());
            this.sendEmails(emails);
        }
    }

    private void sendEmails(List<Email> emails) {
        for (Email email : emails) {
            try {
                this.mailUtility.sendMail(email);
            }
            catch (Exception e) {
                LOG.error("Failed to send healthcheck failure email to {}", (Object)email, (Object)e);
            }
        }
        if (!emails.isEmpty()) {
            this.eventPublisher.publish((Object)new HealthcheckEmailSentAnalyticsEvent(emails.size()));
        }
    }

    private String renderBody(HealthCheckStatus status) throws IOException {
        URI url = UriBuilder.fromUri((String)this.info.getBaseURL(UrlMode.ABSOLUTE)).path("plugins/servlet/troubleshooting/view").queryParam("source", new Object[]{"email"}).queryParam("healthCheck", new Object[]{SupportHealthCheckUtils.getCompactKey(status.getCompleteKey())}).build(new Object[0]);
        String mailBody = RenderingUtils.render(this.info.getTemplateRenderer(), "/templates/email/healthcheck-failure.vm", (Map<String, Object>)ImmutableMap.of((Object)"status", (Object)status, (Object)"healthCheckUrl", (Object)url, (Object)"info", (Object)this.info));
        return mailBody;
    }

    private boolean shouldNotify(HealthCheckStatus status, UserKey userKey) {
        SupportHealthStatus.Severity minSeverity = this.userSettingsService.getUserSettings(userKey).getSeverityThresholdForNotifications();
        return status.getSeverity().compareTo(minSeverity) >= 0;
    }
}

