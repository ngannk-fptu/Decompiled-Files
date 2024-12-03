/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.longrunning.AbstractLongRunningTask
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.longrunning;

import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.core.task.longrunning.AbstractLongRunningTask;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConfluenceAbstractLongRunningTask
extends AbstractLongRunningTask {
    private static final Logger CALRTLog = LoggerFactory.getLogger(ConfluenceAbstractLongRunningTask.class);

    public void run() {
        super.run();
        try {
            this.runInternal();
        }
        catch (Error | RuntimeException e) {
            CALRTLog.error("Long running task \"" + this.getName() + "\" failed to run.", e);
            throw e;
        }
        this.stopTimer();
    }

    protected abstract void runInternal();

    protected ResourceBundle getResourceBundle() {
        return GeneralUtil.getI18n().getResourceBundle();
    }
}

