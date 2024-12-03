/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import org.apache.catalina.Container;
import org.apache.catalina.Service;

public interface Engine
extends Container {
    public String getDefaultHost();

    public void setDefaultHost(String var1);

    public String getJvmRoute();

    public void setJvmRoute(String var1);

    public Service getService();

    public void setService(Service var1);
}

