/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.install;

import com.atlassian.plugin.PluginController;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.upm.Pairs;
import com.atlassian.upm.PluginControlHandlerRegistry;
import com.atlassian.upm.PluginUpdateRequestStore;
import com.atlassian.upm.analytics.event.PluginRequestCompletedAnalyticsEvent;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginFactory;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.PluginWithDependenciesInstallResult;
import com.atlassian.upm.core.SafeModeAccessor;
import com.atlassian.upm.core.analytics.AnalyticsLogger;
import com.atlassian.upm.core.analytics.SenFinder;
import com.atlassian.upm.core.install.DefaultPluginInstallationService;
import com.atlassian.upm.core.install.PluginInstallHandlerRegistry;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.license.LicensedPlugins;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.mail.EmailType;
import com.atlassian.upm.mail.UpmMailSenderService;
import com.atlassian.upm.notification.ManualUpdateRequiredNotificationService;
import com.atlassian.upm.notification.PluginRequestNotificationChecker;
import com.atlassian.upm.request.PluginRequest;
import com.atlassian.upm.request.PluginRequestStore;
import com.atlassian.upm.schedule.PluginUpdateCheckJob;
import com.atlassian.upm.schedule.UpmScheduler;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public final class UpmPluginInstallationService
extends DefaultPluginInstallationService {
    private final PluginUpdateRequestStore pluginUpdateRequestStore;
    private final PluginRequestStore pluginRequestStore;
    private final PluginRequestNotificationChecker pluginRequestNotificationChecker;
    private final UpmMailSenderService mailSenderService;
    private final UserManager userManager;
    private final UpmScheduler upmScheduler;
    private final ManualUpdateRequiredNotificationService manualUpdateNotificationService;
    private final PluginLicenseRepository licenseRepository;

    public UpmPluginInstallationService(AnalyticsLogger analytics, AuditLogService auditLogger, I18nResolver i18nResolver, PluginController pluginController, PluginFactory pluginFactory, PluginInstallHandlerRegistry pluginInstallHandlerRegistry, PluginRetriever pluginRetriever, SafeModeAccessor safeMode, TransactionTemplate txTemplate, PluginUpdateRequestStore pluginUpdateRequestStore, PluginRequestStore pluginRequestStore, PluginRequestNotificationChecker pluginRequestNotificationChecker, UpmMailSenderService mailSenderService, UserManager userManager, UpmScheduler upmScheduler, ManualUpdateRequiredNotificationService manualUpdateNotificationService, PluginControlHandlerRegistry pluginControlHandlerRegistry, DefaultHostApplicationInformation hostApplicationInformation, PluginLicenseRepository licenseRepository, SenFinder senFinder, LicensingUsageVerifier licensingUsageVerifier) {
        super(analytics, auditLogger, i18nResolver, pluginController, pluginFactory, pluginInstallHandlerRegistry, pluginControlHandlerRegistry, pluginRetriever, safeMode, txTemplate, hostApplicationInformation, senFinder, licensingUsageVerifier);
        this.pluginUpdateRequestStore = Objects.requireNonNull(pluginUpdateRequestStore, "pluginUpdateRequestStore");
        this.pluginRequestStore = Objects.requireNonNull(pluginRequestStore, "pluginRequestStore");
        this.pluginRequestNotificationChecker = Objects.requireNonNull(pluginRequestNotificationChecker, "pluginRequestNotificationChecker");
        this.mailSenderService = Objects.requireNonNull(mailSenderService, "mailSenderService");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.upmScheduler = Objects.requireNonNull(upmScheduler, "upmScheduler");
        this.manualUpdateNotificationService = Objects.requireNonNull(manualUpdateNotificationService, "manualUpdateNotificationService");
        this.licenseRepository = Objects.requireNonNull(licenseRepository, "licenseRepository");
    }

    @Override
    public PluginWithDependenciesInstallResult install(File artifactFile, String source, Option<String> contentType, boolean recheckForUpdates) {
        PluginWithDependenciesInstallResult result = super.install(artifactFile, source, contentType, recheckForUpdates);
        this.postSuccessfulInstall(result, recheckForUpdates);
        return result;
    }

    @Override
    public PluginWithDependenciesInstallResult update(File artifactFile, String source, Option<String> contentType, boolean recheckForUpdates) {
        PluginWithDependenciesInstallResult result = super.update(artifactFile, source, contentType, recheckForUpdates);
        this.postSuccessfulInstall(result, recheckForUpdates);
        return result;
    }

    private void postSuccessfulInstall(PluginWithDependenciesInstallResult result, boolean recheckForUpdates) {
        this.postSuccessfulInstall(result.getPlugin());
        for (Plugin dependency : result.getDependencies()) {
            this.postSuccessfulInstall(dependency);
        }
        if (recheckForUpdates) {
            this.upmScheduler.triggerJob(PluginUpdateCheckJob.class, UpmScheduler.RunMode.TRIGGERED_BY_USER);
        }
    }

    private void postSuccessfulInstall(Plugin plugin) {
        List<PluginRequest> requests = this.pluginRequestStore.getRequests(plugin.getKey());
        if (!requests.isEmpty()) {
            this.getAnalyticsLogger().log(new PluginRequestCompletedAnalyticsEvent(plugin.getKey(), true, requests.size()));
            this.pluginRequestStore.removeRequests(plugin.getKey());
            this.pluginRequestNotificationChecker.updatePluginRequestNotifications();
            this.sendRequestAcceptedEmail(plugin, requests);
        }
        this.pluginUpdateRequestStore.resetPluginUpdateRequest(plugin);
        this.manualUpdateNotificationService.clearEmailRecords(plugin.getKey());
        this.licenseRepository.invalidateCacheForPlugin(plugin.getKey());
    }

    private void sendRequestAcceptedEmail(Plugin plugin, Iterable<PluginRequest> requests) {
        if (this.mailSenderService.canSendEmail()) {
            boolean isPaidViaAtlassian = LicensedPlugins.usesLicensing(plugin.getPlugin(), this.licensingUsageVerifier);
            UserProfile profile = this.userManager.getRemoteUser();
            if (profile != null) {
                for (PluginRequest request : requests) {
                    this.mailSenderService.sendUpmEmail(EmailType.ADDON_REQUEST_FULFILLED, Pairs.ImmutablePair.pair(plugin.getKey(), plugin.getName()), Collections.singleton(request.getUser().getUserKey()), this.getSubjectParams(profile, request), this.getBodyContext(isPaidViaAtlassian));
                }
            }
        }
    }

    private List<String> getSubjectParams(UserProfile sender, PluginRequest request) {
        String fullName = sender.getFullName();
        if (StringUtils.isBlank((CharSequence)fullName)) {
            return Arrays.asList(sender.getUsername(), request.getPluginName());
        }
        return Arrays.asList(fullName, request.getPluginName());
    }

    private Map<String, Object> getBodyContext(boolean isPaidViaAtlassian) {
        return Collections.singletonMap("isPaidViaAtlassian", isPaidViaAtlassian);
    }

    @Override
    public void uninstall(Plugin plugin) {
        boolean uninstalled = super.uninstallInternal(plugin);
        if (uninstalled) {
            this.pluginUpdateRequestStore.resetPluginUpdateRequest(plugin);
            this.manualUpdateNotificationService.clearEmailRecords(plugin.getKey());
        }
    }
}

