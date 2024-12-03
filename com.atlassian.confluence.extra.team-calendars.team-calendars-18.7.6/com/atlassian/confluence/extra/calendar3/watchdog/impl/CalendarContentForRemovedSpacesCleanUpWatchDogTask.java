/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.watchdog.impl;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.calendar3.contenttype.CalendarContentTypeManager;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogStatusReporter;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogTask;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalendarContentForRemovedSpacesCleanUpWatchDogTask
implements WatchDogTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarContentForRemovedSpacesCleanUpWatchDogTask.class);
    private static final String CLEANUP_DONE = "cleanupDone";
    private int batchSize = 100;
    private int totalCleaned = 0;
    private final CalendarContentTypeManager calendarContentTypeManager;
    private final CustomContentManager customContentManager;
    private final PluginSettings pluginSettings;
    private final SpaceManager spaceManager;

    @Autowired
    @VisibleForTesting
    public CalendarContentForRemovedSpacesCleanUpWatchDogTask(CalendarContentTypeManager calendarContentTypeManager, @ComponentImport CustomContentManager customContentManager, @ComponentImport PluginSettingsFactory pluginSettingsFactory, @ComponentImport SpaceManager spaceManager) {
        this.calendarContentTypeManager = calendarContentTypeManager;
        this.customContentManager = customContentManager;
        this.pluginSettings = pluginSettingsFactory.createSettingsForKey(this.getClass().getName());
        this.spaceManager = spaceManager;
    }

    @Override
    public boolean shouldRun() {
        return this.pluginSettings.get(CLEANUP_DONE) == null;
    }

    @Override
    public void run(WatchDogStatusReporter reporter) {
        int start = 0;
        boolean hasMore = true;
        while (hasMore) {
            SimplePageRequest request = new SimplePageRequest(start, this.batchSize);
            PageResponse<CustomContentEntityObject> calendars = this.calendarContentTypeManager.getAllCalendarContent((PageRequest)request);
            calendars.forEach(calendar -> {
                if (this.isOrphaned((CustomContentEntityObject)calendar)) {
                    this.customContentManager.removeContentEntity((ContentEntityObject)calendar);
                    ++this.totalCleaned;
                }
            });
            hasMore = calendars.hasMore();
            start += this.batchSize;
        }
        String status = String.format("Cleaned up %s orphaned content entity objects", this.totalCleaned);
        this.pluginSettings.put(CLEANUP_DONE, (Object)"true");
        reporter.report(status);
        LOGGER.debug(status);
    }

    @VisibleForTesting
    public void setBatchSize(int size) {
        this.batchSize = size;
    }

    private boolean isOrphaned(CustomContentEntityObject cceo) {
        String spaceKey = cceo.getProperties().getStringProperty("spaceKey");
        if (spaceKey != null && spaceKey.length() > 0) {
            return this.spaceManager.getSpace(spaceKey) == null;
        }
        return false;
    }
}

