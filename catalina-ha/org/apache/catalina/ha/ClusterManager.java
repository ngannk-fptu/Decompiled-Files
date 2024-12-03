/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Manager
 *  org.apache.catalina.tribes.io.ReplicationStream
 */
package org.apache.catalina.ha;

import java.io.IOException;
import org.apache.catalina.Manager;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.catalina.ha.ClusterMessage;
import org.apache.catalina.tribes.io.ReplicationStream;

public interface ClusterManager
extends Manager {
    public void messageDataReceived(ClusterMessage var1);

    public ClusterMessage requestCompleted(String var1);

    public String[] getInvalidatedSessions();

    public String getName();

    public void setName(String var1);

    public CatalinaCluster getCluster();

    public void setCluster(CatalinaCluster var1);

    public ReplicationStream getReplicationStream(byte[] var1) throws IOException;

    public ReplicationStream getReplicationStream(byte[] var1, int var2, int var3) throws IOException;

    public boolean isNotifyListenersOnReplication();

    public ClusterManager cloneFromTemplate();
}

