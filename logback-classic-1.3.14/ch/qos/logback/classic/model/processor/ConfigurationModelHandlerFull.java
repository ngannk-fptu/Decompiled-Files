/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.Context
 *  ch.qos.logback.core.joran.util.ConfigurationWatchListUtil
 *  ch.qos.logback.core.model.processor.ModelHandlerBase
 *  ch.qos.logback.core.model.processor.ModelInterpretationContext
 *  ch.qos.logback.core.spi.ConfigurationEvent
 *  ch.qos.logback.core.util.Duration
 *  ch.qos.logback.core.util.OptionHelper
 */
package ch.qos.logback.classic.model.processor;

import ch.qos.logback.classic.joran.ReconfigureOnChangeTask;
import ch.qos.logback.classic.model.ConfigurationModel;
import ch.qos.logback.classic.model.processor.ConfigurationModelHandler;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.spi.ConfigurationEvent;
import ch.qos.logback.core.util.Duration;
import ch.qos.logback.core.util.OptionHelper;
import java.net.URL;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ConfigurationModelHandlerFull
extends ConfigurationModelHandler {
    public ConfigurationModelHandlerFull(Context context) {
        super(context);
    }

    public static ModelHandlerBase makeInstance2(Context context, ModelInterpretationContext mic) {
        return new ConfigurationModelHandlerFull(context);
    }

    @Override
    protected void processScanAttrib(ModelInterpretationContext mic, ConfigurationModel configurationModel) {
        String scanStr = mic.subst(configurationModel.getScanStr());
        if (!OptionHelper.isNullOrEmpty((String)scanStr) && !"false".equalsIgnoreCase(scanStr)) {
            ScheduledExecutorService scheduledExecutorService = this.context.getScheduledExecutorService();
            URL mainURL = ConfigurationWatchListUtil.getMainWatchURL((Context)this.context);
            if (mainURL == null) {
                this.addWarn("Due to missing top level configuration file, reconfiguration on change (configuration file scanning) cannot be done.");
                return;
            }
            ReconfigureOnChangeTask rocTask = new ReconfigureOnChangeTask();
            rocTask.setContext(this.context);
            this.addInfo("Registering a new ReconfigureOnChangeTask " + rocTask);
            this.context.fireConfigurationEvent(ConfigurationEvent.newConfigurationChangeDetectorRegisteredEvent((Object)rocTask));
            String scanPeriodStr = mic.subst(configurationModel.getScanPeriodStr());
            Duration duration = this.getDurationOfScanPeriodAttribute(scanPeriodStr, SCAN_PERIOD_DEFAULT);
            this.addInfo("Will scan for changes in [" + mainURL + "] ");
            this.addInfo("Setting ReconfigureOnChangeTask scanning period to " + duration);
            ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(rocTask, duration.getMilliseconds(), duration.getMilliseconds(), TimeUnit.MILLISECONDS);
            rocTask.setScheduredFuture(scheduledFuture);
            this.context.addScheduledFuture(scheduledFuture);
        }
    }

    private Duration getDurationOfScanPeriodAttribute(String scanPeriodAttrib, Duration defaultDuration) {
        Duration duration = null;
        if (!OptionHelper.isNullOrEmpty((String)scanPeriodAttrib)) {
            try {
                duration = Duration.valueOf((String)scanPeriodAttrib);
            }
            catch (IllegalArgumentException | IllegalStateException e) {
                this.addWarn("Failed to parse 'scanPeriod' attribute [" + scanPeriodAttrib + "]", e);
            }
        }
        if (duration == null) {
            this.addInfo("No 'scanPeriod' specified. Defaulting to " + defaultDuration.toString());
            duration = defaultDuration;
        }
        return duration;
    }
}

