/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.core.rest.resources.install;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.PluginDownloadService;
import com.atlassian.upm.core.PluginInstallationService;
import com.atlassian.upm.core.SelfUpdateController;
import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.async.AsyncTaskStatusUpdater;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.atlassian.upm.core.rest.resources.install.DownloadingInstallTask;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import java.io.File;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstallFromUriTask
extends DownloadingInstallTask {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PermissionEnforcer permissionEnforcer;
    private final SysPersisted sysPersisted;

    public InstallFromUriTask(URI uri, Option<String> name, PluginDownloadService downloader, AuditLogService auditLogger, PluginInstallationService pluginInstaller, SelfUpdateController selfUpdateController, BaseUriBuilder uriBuilder, ApplicationPluginsManager applicationPluginsManager, I18nResolver i18nResolver, PermissionEnforcer permissionEnforcer, SysPersisted sysPersisted) {
        super(uri, Option.option(uri.toASCIIString()), name, pluginInstaller, selfUpdateController, auditLogger, downloader, uriBuilder, applicationPluginsManager, i18nResolver);
        this.permissionEnforcer = permissionEnforcer;
        this.sysPersisted = sysPersisted;
    }

    @Override
    protected AsyncTaskStatus executeTask(final AsyncTaskStatusUpdater statusUpdater) throws Exception {
        return this.download(statusUpdater).fold(Functions.identity(), new Function<PluginDownloadService.DownloadResult, AsyncTaskStatus>(){

            public AsyncTaskStatus apply(PluginDownloadService.DownloadResult downloaded) {
                File pluginFile = downloaded.getFile();
                String name = downloaded.getName();
                Option<String> contentType = downloaded.getContentType();
                InstallFromUriTask.this.permissionEnforcer.enforceInProcessInstallationFromUriPermission(InstallFromUriTask.this.getUri());
                return InstallFromUriTask.this.installFromFile(pluginFile, name, contentType, InstallFromUriTask.this.installingStatus(InstallFromUriTask.this.getName(name)), statusUpdater);
            }
        });
    }
}

