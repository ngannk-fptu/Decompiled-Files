/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.rest.resources.disableall;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.PluginEnablementService;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.async.AsyncTask;
import com.atlassian.upm.core.async.AsyncTaskStage;
import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.async.AsyncTaskStatusUpdater;
import com.atlassian.upm.core.async.AsyncTaskType;
import com.atlassian.upm.core.async.TaskSubitemFailure;
import com.atlassian.upm.core.async.TaskSubitemSuccess;
import com.atlassian.upm.pac.IncompatiblePluginData;
import com.atlassian.upm.pac.PacClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisableAllIncompatibleTask
implements AsyncTask {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PacClient pacClient;
    private final PluginEnablementService enabler;
    private final PluginRetriever pluginRetriever;
    private final UpmInformation upm;

    public DisableAllIncompatibleTask(PacClient pacClient, PluginEnablementService enabler, PluginRetriever pluginRetriever, UpmInformation upm) {
        this.pacClient = Objects.requireNonNull(pacClient, "pacClient");
        this.upm = Objects.requireNonNull(upm, "upm");
        this.enabler = Objects.requireNonNull(enabler, "enabler");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
    }

    @Override
    public AsyncTaskStatus getInitialStatus() {
        return AsyncTaskStatus.builder().stage(Option.some(AsyncTaskStage.FINDING)).build();
    }

    @Override
    public AsyncTaskType getType() {
        return AsyncTaskType.DISABLE_ALL_INCOMPATIBLE;
    }

    @Override
    public AsyncTaskStatus run(AsyncTaskStatusUpdater statusUpdater) throws Exception {
        try {
            return this.disable(this.findIncompatibles(), statusUpdater);
        }
        catch (MpacException pe) {
            this.logger.error("Failed to find incompatible plugins: " + pe);
            return AsyncTaskStatus.builder().errorByCode("err.finding.incompatibles").build();
        }
        catch (Throwable t) {
            this.logger.error("Failed to disable all incompatible plugins", t);
            return AsyncTaskStatus.builder().errorByCode("unexpected.exception").build();
        }
    }

    private List<IncompatiblePluginData> findIncompatibles() throws MpacException {
        return this.pacClient.getIncompatiblePlugins(Collections.singletonList(this.upm.getPluginKey())).stream().filter(plugin -> this.pluginRetriever.isPluginEnabled(plugin.getKey())).filter(IncompatiblePluginData::isIncompatibleWithHostProduct).collect(Collectors.toList());
    }

    static AsyncTaskStatus disabling(IncompatiblePluginData plugin, int numberComplete, int totalIncompatibles) {
        return AsyncTaskStatus.builder().stage(Option.some(AsyncTaskStage.APPLYING_ALL)).resourceName(Option.some(plugin.getName())).resourceVersion(Option.some(plugin.getVersion())).itemsDone(Option.some(numberComplete)).itemsTotal(Option.some(totalIncompatibles)).build();
    }

    private AsyncTaskStatus disable(List<IncompatiblePluginData> incompatiblePlugins, AsyncTaskStatusUpdater statusUpdater) {
        ArrayList<TaskSubitemSuccess> successes = new ArrayList<TaskSubitemSuccess>();
        ArrayList<TaskSubitemFailure> failures = new ArrayList<TaskSubitemFailure>();
        for (IncompatiblePluginData plugin : incompatiblePlugins) {
            try {
                int numberComplete = successes.size() + failures.size();
                statusUpdater.updateStatus(DisableAllIncompatibleTask.disabling(plugin, numberComplete, incompatiblePlugins.size()));
                boolean disabled = this.enabler.disablePlugin(plugin.getKey());
                if (disabled) {
                    successes.add(new TaskSubitemSuccess(plugin.getName(), plugin.getKey(), plugin.getVersion(), null));
                    continue;
                }
                failures.add(this.makeFailure("disable.failed", plugin));
            }
            catch (Exception e) {
                failures.add(this.makeFailure("disable.error", plugin, e.getMessage()));
            }
        }
        return AsyncTaskStatus.builder().successes(Option.some(Collections.unmodifiableList(successes))).failures(Option.some(Collections.unmodifiableList(failures))).build();
    }

    private TaskSubitemFailure makeFailure(String errorCode, IncompatiblePluginData p) {
        return this.makeFailure(errorCode, p, null);
    }

    private TaskSubitemFailure makeFailure(String errorCode, IncompatiblePluginData p, String message) {
        return new TaskSubitemFailure((TaskSubitemFailure.Type)null, p.getName(), p.getKey(), p.getVersion(), errorCode, message, "");
    }
}

