/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.SynchronisableDirectory
 *  com.atlassian.crowd.directory.monitor.poller.DirectoryPoller
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.manager.directory.SynchronisationMode
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.SynchronisableDirectory;
import com.atlassian.crowd.directory.monitor.poller.DirectoryPoller;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.directory.DirectorySynchroniser;
import com.atlassian.crowd.manager.directory.SynchronisationMode;
import java.time.Duration;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbCachingDirectoryPoller
implements DirectoryPoller {
    private static final Logger LOG = LoggerFactory.getLogger(DbCachingDirectoryPoller.class);
    private final DirectorySynchroniser directorySynchroniser;
    private final SynchronisableDirectory remoteDirectory;
    private final Duration pollingInterval;

    public DbCachingDirectoryPoller(DirectorySynchroniser directorySynchroniser, SynchronisableDirectory remoteDirectory) {
        this.directorySynchroniser = directorySynchroniser;
        this.remoteDirectory = remoteDirectory;
        this.pollingInterval = DbCachingDirectoryPoller.getPollingInterval((RemoteDirectory)remoteDirectory);
    }

    public static Duration getPollingInterval(RemoteDirectory remoteDirectory) {
        String intervalStr = remoteDirectory.getValue("directory.cache.synchronise.interval");
        long seconds = NumberUtils.toLong((String)intervalStr, (long)3600L);
        return Duration.ofSeconds(seconds);
    }

    public long getPollingInterval() {
        return this.pollingInterval.getSeconds();
    }

    public void pollChanges(SynchronisationMode syncMode) {
        try {
            this.directorySynchroniser.synchronise(this.remoteDirectory, syncMode);
        }
        catch (DirectoryNotFoundException ex) {
            LOG.error("Error occurred while refreshing the cache for directory [ " + this.getDirectoryID() + " ].", (Throwable)ex);
        }
        catch (OperationFailedException ex) {
            LOG.error("Error occurred while refreshing the cache for directory [ " + this.getDirectoryID() + " ].", (Throwable)ex);
        }
        catch (RuntimeException ex) {
            LOG.error("Error occurred while refreshing the cache for directory [ " + this.getDirectoryID() + " ].", (Throwable)ex);
        }
    }

    public long getDirectoryID() {
        return this.remoteDirectory.getDirectoryId();
    }
}

