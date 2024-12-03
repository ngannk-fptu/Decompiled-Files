/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEventSupplier
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo
 *  com.atlassian.confluence.status.service.systeminfo.UsageInfo
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.UserChecker
 *  com.atlassian.core.util.FileSize
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.periodic.event;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEventSupplier;
import com.atlassian.confluence.plugins.periodic.event.ConfluenceDailyStatisticsAnalyticsEvent;
import com.atlassian.confluence.plugins.periodic.event.provider.UserDirectoryStatisticsProvider;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.confluence.status.service.systeminfo.UsageInfo;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.core.util.FileSize;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfluenceDailyStatisticsAnalyticsEventSupplier
implements PeriodicEventSupplier {
    private final AttachmentManager attachmentManager;
    private final ClusterManager clusterManager;
    private final CommentManager commentManager;
    private final PageManager pageManager;
    private final PluginAccessor pluginAccessor;
    private final PluginMetadataManager pluginMetadataManager;
    private final SystemInformationService systemInformationService;
    private final LicenseService licenseService;
    private final UserChecker userChecker;
    private final UserDirectoryStatisticsProvider userDirectoryStatisticsProvider;
    private final UserAccessor userAccessor;
    private final Logger logger = LoggerFactory.getLogger(ConfluenceDailyStatisticsAnalyticsEventSupplier.class);
    private static String USER_ADDON = "user";
    private static String SYSTEM_ADDON = "system";
    public static final String AFC_PLUGIN_KEY = "com.addonengine.analytics";

    @Autowired
    public ConfluenceDailyStatisticsAnalyticsEventSupplier(@ComponentImport AttachmentManager attachmentManager, @ComponentImport ClusterManager clusterManager, @ComponentImport CommentManager commentManager, @ComponentImport PageManager pageManager, @ComponentImport PluginAccessor pluginAccessor, @ComponentImport PluginMetadataManager pluginMetadataManager, @ComponentImport LicenseService licenseService, @ComponentImport SystemInformationService systemInformationService, @ComponentImport UserChecker userChecker, UserDirectoryStatisticsProvider userDirectoryStatisticsProvider, @ComponentImport UserAccessor userAccessor) {
        this.attachmentManager = Objects.requireNonNull(attachmentManager);
        this.clusterManager = Objects.requireNonNull(clusterManager);
        this.commentManager = Objects.requireNonNull(commentManager);
        this.pageManager = Objects.requireNonNull(pageManager);
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
        this.pluginMetadataManager = Objects.requireNonNull(pluginMetadataManager);
        this.licenseService = Objects.requireNonNull(licenseService);
        this.systemInformationService = Objects.requireNonNull(systemInformationService);
        this.userChecker = Objects.requireNonNull(userChecker);
        this.userDirectoryStatisticsProvider = Objects.requireNonNull(userDirectoryStatisticsProvider);
        this.userAccessor = userAccessor;
    }

    private void checkInterrupted() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    public PeriodicEvent call() {
        this.logger.debug("Gathering Confluence daily statistics...");
        long startTime = System.currentTimeMillis();
        ConfluenceDailyStatisticsAnalyticsEvent.Builder eventBuilder = ConfluenceDailyStatisticsAnalyticsEvent.builder();
        UsageInfo usageInfo = this.systemInformationService.getUsageInfo();
        ConfluenceInfo confluenceInfo = this.systemInformationService.getConfluenceInfo();
        try {
            this.checkInterrupted();
            long startPages = System.currentTimeMillis();
            this.pageManager.getPageStatistics().ifPresent(pageStatisticsDTO -> eventBuilder.allPages(pageStatisticsDTO.getAllPagesCount()).currentPages(pageStatisticsDTO.getCurrentPagesCount()).draftPages(pageStatisticsDTO.getDraftPagesCount()).pagesWithUnpublishedChanges(pageStatisticsDTO.getPagesWithUnpublishedChangesCount()).deletedPages(pageStatisticsDTO.getDeletedPagesCount()));
            this.logger.debug("Finished counting pages in {} ms", (Object)(System.currentTimeMillis() - startPages));
            this.checkInterrupted();
            long startBlogs = System.currentTimeMillis();
            this.pageManager.getBlogStatistics().ifPresent(blogStatisticsDTO -> eventBuilder.allBlogs(blogStatisticsDTO.getAllBlogsCount()).currentBlogs(blogStatisticsDTO.getCurrentBlogsCount()).draftBlogs(blogStatisticsDTO.getDraftBlogsCount()).blogsWithUnpublishedChanges(blogStatisticsDTO.getBlogsWithUnpublishedChangesCount()).deletedBlogs(blogStatisticsDTO.getDeletedBlogsCount()));
            this.logger.debug("Finished counting blogs in {} ms", (Object)(System.currentTimeMillis() - startBlogs));
            this.checkInterrupted();
            long startSpaces = System.currentTimeMillis();
            eventBuilder.personalSpaces(usageInfo.getPersonalSpaces()).globalSpaces(usageInfo.getGlobalSpaces());
            this.logger.debug("Finished counting spaces in {} ms", (Object)(System.currentTimeMillis() - startSpaces));
            this.checkInterrupted();
            long startComments = System.currentTimeMillis();
            eventBuilder.allComments(this.commentManager.countAllCommentVersions());
            this.logger.debug("Finished counting comments in {} ms", (Object)(System.currentTimeMillis() - startComments));
            this.checkInterrupted();
            long startAttachments = System.currentTimeMillis();
            this.attachmentManager.getAttachmentStatistics().ifPresent(attachmentStatisticsDTO -> eventBuilder.allAttachments(attachmentStatisticsDTO.getAllAttachmentsCount()).currentAttachments(attachmentStatisticsDTO.getCurrentAttachmentsCount()).allAttachmentsFileSize((long)FileSize.convertBytesToMB((long)attachmentStatisticsDTO.getAllAttachmentsFileSize())).currentAttachmentsFileSize((long)FileSize.convertBytesToMB((long)attachmentStatisticsDTO.getCurrentAttachmentsFileSize())).deletedAttachmentsFileSize((long)FileSize.convertBytesToMB((long)attachmentStatisticsDTO.getDeletedAttachmentsFileSize())));
            this.logger.debug("Finished counting attachments in {} ms", (Object)(System.currentTimeMillis() - startAttachments));
            this.checkInterrupted();
            long startUsersAndGroups = System.currentTimeMillis();
            eventBuilder.registeredUsers(this.userChecker.getNumberOfRegisteredUsers()).maxUsers(confluenceInfo.getMaxUsers()).allUsers(usageInfo.getLocalUsers()).allGroups(usageInfo.getLocalGroups()).allMemberships(usageInfo.getLocalMemberships());
            this.logger.debug("Finished counting users and groups in {} ms", (Object)(System.currentTimeMillis() - startUsersAndGroups));
            this.checkInterrupted();
            long startAddOns = System.currentTimeMillis();
            Map<String, Long> addOnCount = this.pluginAccessor.getPlugins().stream().collect(Collectors.groupingBy(plugin -> this.pluginMetadataManager.isUserInstalled(plugin) ? USER_ADDON : SYSTEM_ADDON, Collectors.counting()));
            eventBuilder.systemAddOns(addOnCount.getOrDefault(SYSTEM_ADDON, 0L)).userInstalledAddOns(addOnCount.getOrDefault(USER_ADDON, 0L));
            this.logger.debug("Finished counting addons in {} ms", (Object)(System.currentTimeMillis() - startAddOns));
            this.checkInterrupted();
            long startDirectories = System.currentTimeMillis();
            eventBuilder.userDirectory(this.userDirectoryStatisticsProvider.getUserDirectoryStatistics());
            this.logger.debug("Finished counting user directories in {} ms", (Object)(System.currentTimeMillis() - startDirectories));
            if (this.clusterManager.isClustered()) {
                eventBuilder.clusterServerNodes(this.clusterManager.getClusterInformation().getMemberCount()).uptime(this.clusterManager.getClusterUptime());
            } else {
                eventBuilder.uptime(System.currentTimeMillis() - confluenceInfo.getStartTime());
            }
            eventBuilder.isDcLicensed(this.licenseService.isLicensedForDataCenter());
            eventBuilder.afcEnabled(this.pluginAccessor.isPluginEnabled(AFC_PLUGIN_KEY));
            this.systemInformationService.getCloudPlatformMetadata().ifPresent(platformMetadata -> {
                Optional metadataContainer = this.systemInformationService.getClusteredDatabaseInformation(platformMetadata.getCloudPlatform());
                metadataContainer.ifPresent(metadata -> eventBuilder.databaseClusterNodes(metadata.getDatabaseMemberCount()));
            });
            long startCountingUnsyncedUsers = System.currentTimeMillis();
            eventBuilder.unsyncedUserCount(this.userAccessor.countUnsyncedUsers());
            this.logger.debug("Finished counting unsynced users in {} ms", (Object)(System.currentTimeMillis() - startCountingUnsyncedUsers));
            this.logger.debug("Finished building event in {} ms", (Object)(System.currentTimeMillis() - startTime));
        }
        catch (InterruptedException ex) {
            this.logger.error("Thread interrupted before ConfluenceDailyStatisticsAnalyticsEvent could be fully built.");
        }
        return eventBuilder.build();
    }
}

