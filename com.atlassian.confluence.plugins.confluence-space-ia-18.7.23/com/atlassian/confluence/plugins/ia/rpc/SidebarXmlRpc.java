/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rpc.RemoteException
 */
package com.atlassian.confluence.plugins.ia.rpc;

import com.atlassian.confluence.rpc.RemoteException;

public interface SidebarXmlRpc {
    public static final String SIDEBAR_RPC_PATH = "space-sidebar";

    public boolean addSidebarQuickLink(String var1, String var2, String var3, String var4, String var5) throws RemoteException;

    public boolean addSidebarQuickLink(String var1, String var2, String var3, String var4, String var5, String var6) throws RemoteException;

    public boolean removeSidebarQuickLink(String var1, String var2, String var3) throws RemoteException;

    public boolean removeSidebarQuickLinks(String var1, String var2) throws RemoteException;

    public String getOption(String var1, String var2, String var3) throws RemoteException;

    public boolean setOption(String var1, String var2, String var3, String var4) throws RemoteException;
}

