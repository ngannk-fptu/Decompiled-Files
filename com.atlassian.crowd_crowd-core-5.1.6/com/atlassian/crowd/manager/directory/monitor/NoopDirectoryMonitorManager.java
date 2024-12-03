/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.manager.directory.monitor.DirectoryMonitorManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.directory.monitor;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.manager.directory.monitor.DirectoryMonitorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoopDirectoryMonitorManager
implements DirectoryMonitorManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void addMonitor(RemoteDirectory remoteDirectory) {
        this.logger.info("DirectoryMonitorManager is no longer supported, ignoring request to add monitor for directory " + remoteDirectory.getDirectoryId());
    }

    public boolean removeMonitor(long directoryID) {
        this.logger.info("DirectoryMonitorManager is no longer supported, ignoring request to remove monitor for directory " + directoryID);
        return false;
    }

    public boolean hasMonitor(long directoryID) {
        this.logger.info("DirectoryMonitorManager is no longer supported, returning true on hasMonitor() checked for directory " + directoryID);
        return true;
    }
}

