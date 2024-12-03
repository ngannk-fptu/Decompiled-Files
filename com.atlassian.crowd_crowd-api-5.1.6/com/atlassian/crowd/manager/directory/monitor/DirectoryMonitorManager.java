/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 */
package com.atlassian.crowd.manager.directory.monitor;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.monitor.DirectoryMonitorCreationException;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.crowd.manager.directory.monitor.DirectoryMonitorAlreadyRegisteredException;
import com.atlassian.crowd.manager.directory.monitor.DirectoryMonitorRegistrationException;
import com.atlassian.crowd.manager.directory.monitor.DirectoryMonitorUnregistrationException;

@Deprecated
public interface DirectoryMonitorManager {
    public void addMonitor(RemoteDirectory var1) throws DirectoryInstantiationException, DirectoryMonitorCreationException, DirectoryMonitorRegistrationException, DirectoryMonitorAlreadyRegisteredException;

    public boolean removeMonitor(long var1) throws DirectoryMonitorUnregistrationException;

    public boolean hasMonitor(long var1);
}

