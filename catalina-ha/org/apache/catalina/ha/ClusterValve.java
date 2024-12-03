/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Valve
 */
package org.apache.catalina.ha;

import org.apache.catalina.Valve;
import org.apache.catalina.ha.CatalinaCluster;

public interface ClusterValve
extends Valve {
    public CatalinaCluster getCluster();

    public void setCluster(CatalinaCluster var1);
}

