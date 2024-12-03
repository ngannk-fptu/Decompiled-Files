/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.LifecycleException
 *  org.apache.catalina.tribes.ChannelListener
 */
package org.apache.catalina.ha;

import java.io.File;
import java.io.IOException;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.catalina.tribes.ChannelListener;

public interface ClusterDeployer
extends ChannelListener {
    public void start() throws Exception;

    public void stop() throws LifecycleException;

    public void install(String var1, File var2) throws IOException;

    public void remove(String var1, boolean var2) throws IOException;

    public void backgroundProcess();

    public CatalinaCluster getCluster();

    public void setCluster(CatalinaCluster var1);
}

