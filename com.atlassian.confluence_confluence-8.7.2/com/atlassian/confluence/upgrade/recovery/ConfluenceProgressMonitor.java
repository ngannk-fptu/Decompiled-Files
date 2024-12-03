/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dbexporter.progress.ProgressMonitor
 *  com.atlassian.dbexporter.progress.ProgressMonitor$Task
 *  com.google.common.base.Joiner
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.recovery;

import com.atlassian.dbexporter.progress.ProgressMonitor;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceProgressMonitor
implements ProgressMonitor {
    private final Logger log = LoggerFactory.getLogger(ConfluenceProgressMonitor.class);

    public void begin(Object ... args) {
        this.log.info("Starting {}", (Object)Joiner.on((String)",").join(args));
    }

    public void end(Object ... args) {
        this.log.info("Finishing {}", (Object)Joiner.on((String)",").join(args));
    }

    public void begin(ProgressMonitor.Task task, Object ... args) {
        this.logTask("Starting", task, args);
    }

    public void end(ProgressMonitor.Task task, Object ... args) {
        this.logTask("Finishing", task, args);
    }

    private void logTask(String prefix, ProgressMonitor.Task task, Object[] args) {
        switch (task) {
            case TABLE_DATA: {
                this.log.debug(String.join((CharSequence)" ", prefix, task.name(), Joiner.on((String)",").join(args)));
                return;
            }
            case TABLE_ROW: {
                return;
            }
        }
        this.log.info(String.join((CharSequence)" ", prefix, task.name(), Joiner.on((String)",").join(args)));
    }

    public void totalNumberOfTables(int size) {
        this.log.info("Total number of tables: {}", (Object)size);
    }
}

