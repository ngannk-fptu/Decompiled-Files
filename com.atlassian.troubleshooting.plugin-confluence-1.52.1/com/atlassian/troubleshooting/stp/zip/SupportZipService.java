/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.zip;

import com.atlassian.troubleshooting.stp.zip.ClusterMessagingException;
import com.atlassian.troubleshooting.stp.zip.ClusteredZipTaskStart;
import com.atlassian.troubleshooting.stp.zip.CreateSupportZipMonitor;
import com.atlassian.troubleshooting.stp.zip.NotClusteredException;
import com.atlassian.troubleshooting.stp.zip.SupportZipRequest;
import com.atlassian.troubleshooting.stp.zip.TaskNotFoundException;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface SupportZipService {
    @Nonnull
    public CreateSupportZipMonitor createLocalSupportZipWithPermissionCheck(SupportZipRequest var1);

    public CreateSupportZipMonitor createLocalSupportZipWithoutPermissionCheck(SupportZipRequest var1);

    @Nonnull
    public ClusteredZipTaskStart createSupportZipsForCluster(SupportZipRequest var1) throws ClusterMessagingException, NotClusteredException;

    public Optional<CreateSupportZipMonitor> getMonitor(String var1);

    public Optional<CreateSupportZipMonitor> getMonitorWithoutPermissionCheck(String var1);

    public Collection<CreateSupportZipMonitor> getMonitors(boolean var1);

    public Collection<CreateSupportZipMonitor> getClusteredMonitors(String var1);

    public void cancelSupportZipTask(String var1) throws TaskNotFoundException;

    public void cancelSupportZipTaskOnThisNode(CreateSupportZipMonitor var1);

    public boolean isClusterSupportZipSupported();
}

