/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Context
 *  org.apache.catalina.Host
 *  org.apache.catalina.Server
 *  org.apache.catalina.Service
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.storeconfig.StoreRegistry;

public interface IStoreConfig {
    public StoreRegistry getRegistry();

    public void setRegistry(StoreRegistry var1);

    public Server getServer();

    public void setServer(Server var1);

    public void storeConfig();

    public boolean store(Server var1);

    public void store(PrintWriter var1, int var2, Server var3) throws Exception;

    public void store(PrintWriter var1, int var2, Service var3) throws Exception;

    public void store(PrintWriter var1, int var2, Host var3) throws Exception;

    public boolean store(Context var1);

    public void store(PrintWriter var1, int var2, Context var3) throws Exception;
}

