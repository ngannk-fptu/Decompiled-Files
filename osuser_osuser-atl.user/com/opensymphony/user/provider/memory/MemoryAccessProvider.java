/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.memory;

import com.opensymphony.user.Entity;
import com.opensymphony.user.provider.AccessProvider;
import com.opensymphony.user.provider.memory.MemoryCredentialsProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MemoryAccessProvider
implements AccessProvider {
    public static Map groups;

    public boolean addToGroup(String username, String groupname) {
        return this.getGroup((String)groupname).users.add(username);
    }

    public boolean create(String name) {
        if (groups.containsKey(name)) {
            return false;
        }
        MemoryGroup group = new MemoryGroup();
        group.name = name;
        groups.put(name, group);
        return true;
    }

    public void flushCaches() {
    }

    public boolean handles(String name) {
        boolean handle = groups.containsKey(name);
        if (!handle) {
            handle = MemoryCredentialsProvider.users.containsKey(name);
        }
        return handle;
    }

    public boolean inGroup(String username, String groupname) {
        MemoryGroup group = this.getGroup(groupname);
        return group != null && group.users.contains(username);
    }

    public boolean init(Properties properties) {
        groups = new HashMap();
        return true;
    }

    public List list() {
        return Collections.unmodifiableList(new ArrayList(groups.keySet()));
    }

    public List listGroupsContainingUser(String username) {
        ArrayList<String> result = new ArrayList<String>();
        Iterator i = groups.keySet().iterator();
        while (i.hasNext()) {
            String currentGroup = (String)i.next();
            if (!this.inGroup(username, currentGroup)) continue;
            result.add(currentGroup);
        }
        return Collections.unmodifiableList(result);
    }

    public List listUsersInGroup(String groupname) {
        MemoryGroup g = this.getGroup(groupname);
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
        return groups.remove(name) != null;
    }

    public boolean removeFromGroup(String username, String groupname) {
        return this.getGroup((String)groupname).users.remove(username);
    }

    public boolean store(String name, Entity.Accessor accessor) {
        return true;
    }

    private MemoryGroup getGroup(String groupname) {
        MemoryGroup group = (MemoryGroup)groups.get(groupname);
        return group;
    }

    class MemoryGroup {
        List users = new ArrayList();
        String name;

        MemoryGroup() {
        }
    }
}

