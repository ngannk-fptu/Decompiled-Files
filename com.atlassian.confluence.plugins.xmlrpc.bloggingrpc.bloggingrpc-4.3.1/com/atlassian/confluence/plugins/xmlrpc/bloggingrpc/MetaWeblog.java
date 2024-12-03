/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rpc.RemoteException
 */
package com.atlassian.confluence.plugins.xmlrpc.bloggingrpc;

import com.atlassian.confluence.rpc.RemoteException;
import java.util.Hashtable;
import java.util.Vector;

public interface MetaWeblog {
    public String newPost(String var1, String var2, String var3, Hashtable<String, Object> var4, boolean var5) throws RemoteException;

    public boolean editPost(String var1, String var2, String var3, Hashtable<String, Object> var4, boolean var5) throws RemoteException;

    public Hashtable<String, Object> getPost(String var1, String var2, String var3) throws RemoteException;

    public Hashtable<String, Hashtable<String, String>> getCategories(String var1, String var2, String var3) throws RemoteException;

    public Vector<Hashtable<String, Object>> getRecentPosts(String var1, String var2, String var3, int var4) throws RemoteException;

    public Hashtable<String, Object> newMediaObject(String var1, String var2, String var3, Hashtable var4) throws RemoteException;
}

