/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.file;

import com.opensymphony.user.Entity;
import com.opensymphony.user.provider.CredentialsProvider;
import com.opensymphony.user.provider.file.FileUser;
import com.opensymphony.user.provider.file.FileUsersCache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileCredentialsProvider
implements CredentialsProvider {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$file$FileCredentialsProvider == null ? (class$com$opensymphony$user$provider$file$FileCredentialsProvider = FileCredentialsProvider.class$("com.opensymphony.user.provider.file.FileCredentialsProvider")) : class$com$opensymphony$user$provider$file$FileCredentialsProvider));
    protected FileUsersCache userCache;
    static /* synthetic */ Class class$com$opensymphony$user$provider$file$FileCredentialsProvider;

    public boolean authenticate(String name, String password) {
        FileUser user = (FileUser)this.userCache.users.get(name);
        if (user == null) {
            return false;
        }
        return password.equals(user.password);
    }

    public boolean changePassword(String name, String password) {
        FileUser user = (FileUser)this.userCache.users.get(name);
        if (user == null) {
            return false;
        }
        user.password = password;
        return this.userCache.store();
    }

    public boolean create(String name) {
        if (this.userCache.users.containsKey(name)) {
            return false;
        }
        FileUser user = new FileUser();
        user.name = name;
        this.userCache.users.put(name, user);
        return this.userCache.store();
    }

    public void flushCaches() {
        this.userCache.store();
    }

    public boolean handles(String name) {
        if (this.userCache == null) {
            return false;
        }
        return this.userCache.users.containsKey(name);
    }

    public boolean init(Properties properties) {
        return true;
    }

    public List list() {
        return Collections.unmodifiableList(new ArrayList(this.userCache.users.keySet()));
    }

    public boolean load(String name, Entity.Accessor accessor) {
        accessor.setMutable(true);
        return true;
    }

    public boolean remove(String name) {
        boolean rv = this.userCache.users.remove(name) != null;
        return rv && this.userCache.store();
    }

    public boolean store(String name, Entity.Accessor accessor) {
        return this.userCache.store();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

