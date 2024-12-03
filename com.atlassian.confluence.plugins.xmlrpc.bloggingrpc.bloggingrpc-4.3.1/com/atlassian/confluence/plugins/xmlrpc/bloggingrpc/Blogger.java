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

public interface Blogger {
    public Vector<Hashtable<String, String>> getUsersBlogs(String var1, String var2, String var3) throws RemoteException;

    public String newPost(String var1, String var2, String var3, String var4, String var5, boolean var6) throws RemoteException;

    public boolean editPost(String var1, String var2, String var3, String var4, String var5, boolean var6) throws RemoteException;

    public Vector<Hashtable<String, Object>> getRecentPosts(String var1, String var2, String var3, String var4, int var5) throws RemoteException;

    public boolean deletePost(String var1, String var2, String var3, String var4, boolean var5) throws RemoteException;

    public boolean setTemplate(String var1, String var2, String var3, String var4, String var5, String var6) throws RemoteException;

    public String getTemplate(String var1, String var2, String var3, String var4, String var5) throws RemoteException;

    public Hashtable<String, Object> getPost(String var1, String var2, String var3, String var4) throws RemoteException;

    public Hashtable<String, String> getUserInfo(String var1, String var2, String var3) throws RemoteException;
}

