/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.renderer.WikiStyleRenderer
 */
package com.atlassian.confluence.extra.dynamictasklist2;

import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.extra.dynamictasklist2.AbstractTaskListMacro;
import com.atlassian.confluence.extra.dynamictasklist2.TaskListManager;
import com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.renderer.WikiStyleRenderer;

public class TaskListMacro
extends AbstractTaskListMacro {
    public static final String MACRO_NAME = "tasklist";

    public TaskListMacro(WebResourceManager webResourceManager, WritableDownloadResourceManager writableDownloadResourceManager, SettingsManager settingsManager, WikiStyleRenderer wikiStyleRenderer, UserAccessor userAccessor, TaskListManager taskListManager, FormatSettingsManager formatSettingsManager, VelocityHelperService velocityHelperService, LocaleManager localeManager) {
        super(webResourceManager, writableDownloadResourceManager, settingsManager, wikiStyleRenderer, userAccessor, taskListManager, formatSettingsManager, velocityHelperService, localeManager);
    }
}

