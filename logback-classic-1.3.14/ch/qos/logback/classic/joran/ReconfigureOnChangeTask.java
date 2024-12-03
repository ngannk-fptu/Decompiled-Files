/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.Context
 *  ch.qos.logback.core.joran.spi.ConfigurationWatchList
 *  ch.qos.logback.core.joran.spi.JoranException
 *  ch.qos.logback.core.joran.util.ConfigurationWatchListUtil
 *  ch.qos.logback.core.model.Model
 *  ch.qos.logback.core.model.ModelUtil
 *  ch.qos.logback.core.spi.ConfigurationEvent
 *  ch.qos.logback.core.spi.ContextAwareBase
 *  ch.qos.logback.core.status.StatusUtil
 */
package ch.qos.logback.classic.joran;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.joran.ReconfigureOnChangeTaskListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.ConfigurationWatchList;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ModelUtil;
import ch.qos.logback.core.spi.ConfigurationEvent;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.StatusUtil;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public class ReconfigureOnChangeTask
extends ContextAwareBase
implements Runnable {
    public static final String DETECTED_CHANGE_IN_CONFIGURATION_FILES = "Detected change in configuration files.";
    static final String RE_REGISTERING_PREVIOUS_SAFE_CONFIGURATION = "Re-registering previous fallback configuration once more as a fallback configuration point";
    static final String FALLING_BACK_TO_SAFE_CONFIGURATION = "Given previous errors, falling back to previously registered safe configuration.";
    long birthdate = System.currentTimeMillis();
    List<ReconfigureOnChangeTaskListener> listeners = null;
    ScheduledFuture<?> scheduledFuture;

    @Override
    public void run() {
        this.context.fireConfigurationEvent(ConfigurationEvent.newConfigurationChangeDetectorRunningEvent((Object)this));
        ConfigurationWatchList configurationWatchList = ConfigurationWatchListUtil.getConfigurationWatchList((Context)this.context);
        if (configurationWatchList == null) {
            this.addWarn("Empty ConfigurationWatchList in context");
            return;
        }
        List filesToWatch = configurationWatchList.getCopyOfFileWatchList();
        if (filesToWatch == null || filesToWatch.isEmpty()) {
            this.addInfo("Empty watch file list. Disabling ");
            return;
        }
        if (!configurationWatchList.changeDetected()) {
            return;
        }
        this.context.fireConfigurationEvent(ConfigurationEvent.newConfigurationChangeDetectedEvent((Object)this));
        this.cancelFutureInvocationsOfThisTaskInstance();
        URL mainConfigurationURL = configurationWatchList.getMainURL();
        this.addInfo(DETECTED_CHANGE_IN_CONFIGURATION_FILES);
        this.addInfo("Will reset and reconfigure context named [" + this.context.getName() + "]");
        LoggerContext lc = (LoggerContext)this.context;
        if (mainConfigurationURL.toString().endsWith("xml")) {
            this.performXMLConfiguration(lc, mainConfigurationURL);
        } else if (mainConfigurationURL.toString().endsWith("groovy")) {
            this.addError("Groovy configuration disabled due to Java 9 compilation issues.");
        }
    }

    private void cancelFutureInvocationsOfThisTaskInstance() {
        boolean result = this.scheduledFuture.cancel(false);
        if (!result) {
            this.addWarn("could not cancel " + this.toString());
        }
    }

    private void performXMLConfiguration(LoggerContext lc, URL mainConfigurationURL) {
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(this.context);
        StatusUtil statusUtil = new StatusUtil(this.context);
        Model failsafeTop = jc.recallSafeConfiguration();
        URL mainURL = ConfigurationWatchListUtil.getMainWatchURL((Context)this.context);
        lc.reset();
        long threshold = System.currentTimeMillis();
        try {
            jc.doConfigure(mainConfigurationURL);
            if (statusUtil.hasXMLParsingErrors(threshold)) {
                this.fallbackConfiguration(lc, failsafeTop, mainURL);
            }
        }
        catch (JoranException e) {
            this.addWarn("Exception occurred during reconfiguration", e);
            this.fallbackConfiguration(lc, failsafeTop, mainURL);
        }
    }

    private void fallbackConfiguration(LoggerContext lc, Model failsafeTop, URL mainURL) {
        JoranConfigurator joranConfigurator = new JoranConfigurator();
        joranConfigurator.setContext(this.context);
        ConfigurationWatchList oldCWL = ConfigurationWatchListUtil.getConfigurationWatchList((Context)this.context);
        ConfigurationWatchList newCWL = oldCWL.buildClone();
        if (failsafeTop == null) {
            this.addWarn("No previous configuration to fall back on.");
            return;
        }
        this.addWarn(FALLING_BACK_TO_SAFE_CONFIGURATION);
        this.addInfo("Safe model " + failsafeTop);
        try {
            lc.reset();
            ConfigurationWatchListUtil.registerConfigurationWatchList((Context)this.context, (ConfigurationWatchList)newCWL);
            ModelUtil.resetForReuse((Model)failsafeTop);
            joranConfigurator.processModel(failsafeTop);
            this.addInfo(RE_REGISTERING_PREVIOUS_SAFE_CONFIGURATION);
            joranConfigurator.registerSafeConfiguration(failsafeTop);
            this.context.fireConfigurationEvent(ConfigurationEvent.newConfigurationEndedEvent((Object)this));
            this.addInfo("after registerSafeConfiguration");
        }
        catch (Exception e) {
            this.addError("Unexpected exception thrown by a configuration considered safe.", e);
        }
    }

    public String toString() {
        return "ReconfigureOnChangeTask(born:" + this.birthdate + ")";
    }

    public void setScheduredFuture(ScheduledFuture<?> aScheduledFuture) {
        this.scheduledFuture = aScheduledFuture;
    }
}

