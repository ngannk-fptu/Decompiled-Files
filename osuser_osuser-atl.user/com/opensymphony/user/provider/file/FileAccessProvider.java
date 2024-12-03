/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.file;

import com.opensymphony.user.Entity;
import com.opensymphony.user.provider.AccessProvider;
import com.opensymphony.user.provider.file.FileGroup;
import com.opensymphony.user.provider.file.FileGroupsCache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class FileAccessProvider
implements AccessProvider {
    protected static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$file$FileAccessProvider == null ? (class$com$opensymphony$user$provider$file$FileAccessProvider = FileAccessProvider.class$("com.opensymphony.user.provider.file.FileAccessProvider")) : class$com$opensymphony$user$provider$file$FileAccessProvider));
    protected FileGroupsCache groupCache;
    static /* synthetic */ Class class$com$opensymphony$user$provider$file$FileAccessProvider;

    public boolean addToGroup(String username, String groupname) {
        if (!this.inGroup(username, groupname)) {
            boolean rv = this.getGroup((String)groupname).users.add(username);
            return rv && this.groupCache.store();
        }
        return false;
    }

    public boolean create(String name) {
        if (this.groupCache.groups.containsKey(name)) {
            return false;
        }
        FileGroup group = new FileGroup();
        group.name = name;
        this.groupCache.groups.put(name, group);
        return this.groupCache.store();
    }

    public void flushCaches() {
        this.groupCache.store();
    }

    public boolean handles(String name) {
        if (this.groupCache == null) {
            return false;
        }
        return this.groupCache.groups.containsKey(name);
    }

    public boolean inGroup(String username, String groupname) {
        FileGroup group = this.getGroup(groupname);
        return group != null && group.users.contains(username);
    }

    public boolean init(Properties properties) {
        return true;
    }

    public List list() {
        return Collections.unmodifiableList(new ArrayList(this.groupCache.groups.keySet()));
    }

    public List listGroupsContainingUser(String username) {
        ArrayList<String> result = new ArrayList<String>();
        Iterator i = this.groupCache.groups.keySet().iterator();
        while (i.hasNext()) {
            String currentGroup = (String)i.next();
            if (!this.inGroup(username, currentGroup)) continue;
            result.add(currentGroup);
        }
        return Collections.unmodifiableList(result);
    }

    public List listUsersInGroup(String groupname) {
        FileGroup g = this.getGroup(groupname);
        if (g == null) {
            return Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableList(this.getGroup((String)groupname).users);
    }

    public boolean load(String name, Entity.Accessor accessor) {
        accessor.setMutable(true);
        return true;
    }

    public boolean remove(String name) {
        boolean rv = this.groupCache.groups.remove(name) != null;
        return rv && this.groupCache.store();
    }

    public boolean removeFromGroup(String username, String groupname) {
        boolean rv = this.getGroup((String)groupname).users.remove(username);
        return rv && this.groupCache.store();
    }

    public boolean store(String name, Entity.Accessor accessor) {
        return this.groupCache.store();
    }

    private FileGroup getGroup(String groupname) {
        FileGroup group = (FileGroup)this.groupCache.groups.get(groupname);
        return group;
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

