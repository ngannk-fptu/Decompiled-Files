/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Function
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.Duration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.core.rest.resources.install;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginDownloadService;
import com.atlassian.upm.core.PluginInstallationService;
import com.atlassian.upm.core.PluginWithDependenciesInstallResult;
import com.atlassian.upm.core.SafeModeException;
import com.atlassian.upm.core.SelfUpdateController;
import com.atlassian.upm.core.async.AsyncTask;
import com.atlassian.upm.core.async.AsyncTaskStage;
import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.async.AsyncTaskStatusUpdater;
import com.atlassian.upm.core.async.AsyncTaskType;
import com.atlassian.upm.core.async.AutoProgressIncrementer;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.atlassian.upm.core.rest.resources.permission.PermissionException;
import com.atlassian.upm.spi.PluginInstallException;
import com.google.common.base.Function;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.URI;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class InstallTask
implements AsyncTask {
    private static final Logger logger = LoggerFactory.getLogger(InstallTask.class);
    private static final float INSTALL_PROGRESS_FROM = 0.5f;
    private static final float INSTALL_PROGRESS_TO = 0.9f;
    private static final Duration INSTALL_PROGRESS_TOTAL_TIME = Duration.standardSeconds((long)25L);
    private final Option<String> source;
    private final PluginInstallationService installer;
    private final SelfUpdateController selfUpdateController;
    private final BaseUriBuilder uriBuilder;
    private final ApplicationPluginsManager applicationPluginsManager;
    protected final I18nResolver i18nResolver;

    public InstallTask(Option<String> source, PluginInstallationService installer, SelfUpdateController selfUpdateController, BaseUriBuilder uriBuilder, ApplicationPluginsManager applicationPluginsManager, I18nResolver i18nResolver) {
        this.source = source;
        this.installer = Objects.requireNonNull(installer, "installer");
        this.selfUpdateController = Objects.requireNonNull(selfUpdateController, "selfUpdateController");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
        this.applicationPluginsManager = Objects.requireNonNull(applicationPluginsManager, "licensingUsageVerifier");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
    }

    @Override
    public AsyncTaskType getType() {
        return AsyncTaskType.INSTALL;
    }

    @Override
    public AsyncTaskStatus run(AsyncTaskStatusUpdater statusUpdater) throws Exception {
        try {
            return this.executeTask(statusUpdater);
        }
        catch (SafeModeException sme) {
            return this.errByMessage(this.i18nResolver.getText("upm.pluginInstall.error.safe.mode"));
        }
        catch (FileNotFoundException fnfe) {
            return this.errBySubcode("upm.pluginInstall.error.file.not.found");
        }
        catch (PluginInstallException pie) {
            for (Pair<String, Serializable[]> i18nParams : pie.getI18nMessageProperties()) {
                Object[] objectArray;
                if (i18nParams.second().length > 0) {
                    objectArray = i18nParams.second();
                } else {
                    String[] stringArray = new String[1];
                    objectArray = stringArray;
                    stringArray[0] = this.getSource();
                }
                Object[] placeholderValues = objectArray;
                String text = this.i18nResolver.getText(i18nParams.first(), (Serializable[])placeholderValues);
                if (StringUtils.isEmpty((CharSequence)text) || text.equals(i18nParams.first())) continue;
                return this.errByMessage(text);
            }
            return this.errBySubcode("upm.pluginInstall.error.install.failed");
        }
        catch (PermissionException pe) {
            logger.warn("Unpermitted to install app", (Throwable)pe);
            return this.errBySubcode("upm.pluginInstall.error.install.unpermitted");
        }
        catch (RuntimeException re) {
            logger.warn("Unexpected error in install task", (Throwable)re);
            return this.errBySubcode("upm.plugin.error.unexpected.error");
        }
    }

    protected String getSource() {
        return this.source.getOrElse("");
    }

    protected abstract AsyncTaskStatus executeTask(AsyncTaskStatusUpdater var1) throws Exception;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected AsyncTaskStatus installFromFile(File plugin, String name, Option<String> contentType, AsyncTaskStatus startingStatus, AsyncTaskStatusUpdater statusUpdater) {
        AutoProgressIncrementer autoProgress = AutoProgressIncrementer.start(statusUpdater, startingStatus, 0.5f, 0.9f, INSTALL_PROGRESS_TOTAL_TIME);
        try {
            if (this.selfUpdateController.isUpmPlugin(plugin)) {
                AsyncTaskStatus asyncTaskStatus = this.selfUpdateController.prepareSelfUpdate(plugin, false).fold(new Function<String, AsyncTaskStatus>(){

                    public AsyncTaskStatus apply(String error) {
                        return InstallTask.this.errBySubcode(error);
                    }
                }, new Function<URI, AsyncTaskStatus>(){

                    public AsyncTaskStatus apply(URI completionUri) {
                        return AsyncTaskStatus.builder().stage(Option.some(AsyncTaskStage.POST_INSTALL_TASK)).nextStepPostUri(Option.some(completionUri)).build();
                    }
                });
                return asyncTaskStatus;
            }
            PluginWithDependenciesInstallResult result = this.installer.install(plugin, name, contentType, true);
            AsyncTaskStatus.Builder finalStatus = AsyncTaskStatus.builder().completedProgress();
            AsyncTaskStatus asyncTaskStatus = finalStatus.resultUri(Option.some(this.uriBuilder.buildPluginUri(this.getRelevantInstalledPlugin(result).getKey()))).build();
            return asyncTaskStatus;
        }
        finally {
            autoProgress.stop();
        }
    }

    private Plugin getRelevantInstalledPlugin(PluginWithDependenciesInstallResult result) {
        for (Plugin dependency : result.getDependencies()) {
            if (!this.applicationPluginsManager.isApplication(dependency.getPlugin())) continue;
            return dependency;
        }
        return result.getPlugin();
    }

    protected AsyncTaskStatus downloadingStatus(String name) {
        return AsyncTaskStatus.builder().stage(Option.some(AsyncTaskStage.DOWNLOADING)).resourceName(Option.some(name)).build();
    }

    protected AsyncTaskStatus downloadingStatus(String name, PluginDownloadService.Progress progress) {
        return AsyncTaskStatus.builder().stage(Option.some(AsyncTaskStage.DOWNLOADING)).resourceName(Option.some(progress.getSource().getOrElse(name))).progressForDownload(Option.some(progress), 0.0f, 0.5f).build();
    }

    protected AsyncTaskStatus installingStatus(String name) {
        return AsyncTaskStatus.builder().stage(Option.some(AsyncTaskStage.INSTALLING)).resourceName(Option.some(name)).build();
    }

    protected AsyncTaskStatus errBySubcode(String subCode) {
        return AsyncTaskStatus.builder().errorByCode(subCode).resourceName(this.source).done(true).build();
    }

    protected AsyncTaskStatus errByMessage(String message) {
        return AsyncTaskStatus.builder().errorByMessage(message).resourceName(this.source).done(true).build();
    }
}

