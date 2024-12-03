/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import org.apache.catalina.Contained;
import org.apache.catalina.Manager;

public interface Cluster
extends Contained {
    public String getClusterName();

    public void setClusterName(String var1);

    public Manager createManager(String var1);

    public void registerManager(Manager var1);

    public void removeManager(Manager var1);

    public void backgroundProcess();
}

