/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.ResponseException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.core.rest.resources.install;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.upm.api.util.Either;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.PluginDownloadService;
import com.atlassian.upm.core.PluginInstallationService;
import com.atlassian.upm.core.SelfUpdateController;
import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.async.AsyncTaskStatusUpdater;
import com.atlassian.upm.core.install.AccessDeniedException;
import com.atlassian.upm.core.install.RelativeURIException;
import com.atlassian.upm.core.install.UnsupportedProtocolException;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.atlassian.upm.core.rest.resources.install.InstallTask;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Iterator;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DownloadingInstallTask
extends InstallTask {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private volatile URI uri;
    private final URI originUri;
    private final Option<String> name;
    private final AuditLogService auditLogger;
    private final PluginDownloadService downloader;
    private final String defaultName;

    public DownloadingInstallTask(URI uri, Option<String> source, Option<String> name, PluginInstallationService installer, SelfUpdateController selfUpdateController, AuditLogService auditLogger, PluginDownloadService downloader, BaseUriBuilder uriBuilder, ApplicationPluginsManager applicationPluginsManager, I18nResolver i18nResolver) {
        super(source, installer, selfUpdateController, uriBuilder, applicationPluginsManager, i18nResolver);
        this.uri = Objects.requireNonNull(uri, "uri");
        this.originUri = uri;
        this.name = name;
        this.defaultName = this.getDisplayNameFromUri(uri);
        this.auditLogger = Objects.requireNonNull(auditLogger, "auditLogger");
        this.downloader = Objects.requireNonNull(downloader, "downloader");
    }

    @Override
    public AsyncTaskStatus getInitialStatus() {
        return this.downloadingStatus(this.getName(this.defaultName));
    }

    protected Either<AsyncTaskStatus, PluginDownloadService.DownloadResult> download(AsyncTaskStatusUpdater statusUpdater) {
        return this.download(statusUpdater, Option.none(String[].class));
    }

    protected Either<AsyncTaskStatus, PluginDownloadService.DownloadResult> download(AsyncTaskStatusUpdater statusUpdater, String[] acceptHeaders) {
        return this.download(statusUpdater, Option.some(acceptHeaders));
    }

    private Either<AsyncTaskStatus, PluginDownloadService.DownloadResult> download(AsyncTaskStatusUpdater statusUpdater, Option<String[]> acceptHeaders) {
        try {
            Iterator<String[]> iterator = acceptHeaders.iterator();
            if (iterator.hasNext()) {
                String[] accept = iterator.next();
                return Either.right(this.downloader.downloadPlugin(this.uri, this.name, accept, this.newProgressTracker(statusUpdater)));
            }
            return Either.right(this.downloader.downloadPlugin(this.uri, this.name, this.newProgressTracker(statusUpdater)));
        }
        catch (AccessDeniedException ade) {
            this.logger.error("Access denied while downloading plugin from " + this.getSource());
            this.auditLogger.logI18nMessage("upm.auditLog.install.plugin.failure", this.getSource());
            return Either.left(this.errBySubcode("upm.pluginInstall.error.access.denied"));
        }
        catch (FileNotFoundException fnfe) {
            this.logger.error("Error downloading plugin from " + this.getSource(), (Throwable)fnfe);
            this.auditLogger.logI18nMessage("upm.pluginInstall.error.file.not.found", this.getSource());
            return Either.left(this.errBySubcode("upm.pluginInstall.error.file.not.found"));
        }
        catch (UnsupportedProtocolException e) {
            this.logger.error("Error downloading plugin from " + this.getSource(), (Throwable)((Object)e));
            this.auditLogger.logI18nMessage("upm.auditLog.install.plugin.unsupported.protocol", this.getSource());
            return Either.left(this.errBySubcode("upm.pluginInstall.error.unsupported.protocol"));
        }
        catch (RelativeURIException e) {
            this.logger.error("Error downloading plugin from " + this.getSource(), (Throwable)((Object)e));
            this.auditLogger.logI18nMessage("upm.auditLog.install.plugin.failure", this.getSource());
            return Either.left(this.errBySubcode("upm.pluginInstall.error.invalid.relative.uri"));
        }
        catch (ResponseException e) {
            this.logger.error("Error downloading plugin from " + this.getSource());
            this.logger.debug(e.toString(), (Throwable)e);
            this.auditLogger.logI18nMessage("upm.auditLog.install.plugin.failure", this.getSource());
            return Either.left(this.errBySubcode("upm.pluginInstall.error.response.exception"));
        }
        catch (IllegalStateException ise) {
            this.logUnexpectedError(ise, statusUpdater);
        }
        catch (Exception e) {
            this.logUnexpectedError(e, statusUpdater);
        }
        return Either.left(this.errBySubcode("unexpected.exception"));
    }

    private void logUnexpectedError(Exception e, AsyncTaskStatusUpdater statusUpdater) {
        statusUpdater.updateStatus(this.errBySubcode("upm.plugin.error.unexpected.error"));
        this.logger.error("Failed to install plugin", (Throwable)e);
        this.auditLogger.logI18nMessage("upm.auditLog.install.plugin.failure", this.getSource());
    }

    private PluginDownloadService.ProgressTracker newProgressTracker(final AsyncTaskStatusUpdater statusUpdater) {
        final String displayName = this.getName(this.defaultName);
        return new PluginDownloadService.ProgressTracker(){

            @Override
            public void notify(PluginDownloadService.Progress progress) {
                statusUpdater.updateStatus(DownloadingInstallTask.this.downloadingStatus(displayName, progress));
            }

            @Override
            public void redirectedTo(URI newUri) {
                DownloadingInstallTask.this.uri = newUri;
                statusUpdater.updateStatus(DownloadingInstallTask.this.downloadingStatus(displayName));
            }
        };
    }

    protected URI getUri() {
        return this.uri;
    }

    protected URI getOriginUri() {
        return this.originUri;
    }

    protected String getName(String backupName) {
        return this.name.getOrElse(backupName);
    }

    private String getDisplayNameFromUri(URI uri) {
        String source = uri.toASCIIString();
        int lastSlash = source.lastIndexOf("/") + 1;
        if (source.length() > lastSlash) {
            return source.substring(lastSlash);
        }
        return source;
    }
}

