/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.memory;

import com.opensymphony.user.Entity;
import com.opensymphony.user.provider.CredentialsProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MemoryCredentialsProvider
implements CredentialsProvider {
    public static Map users;

    public boolean authenticate(String name, String password) {
        MemoryCredentials user = (MemoryCredentials)users.get(name);
        if (user == null) {
            return false;
        }
        return password.equals(user.password);
    }

    public boolean changePassword(String name, String password) {
        MemoryCredentials user = (MemoryCredentials)users.get(name);
        if (user == null) {
            return false;
        }
        user.password = password;
        return true;
    }

    public boolean create(String name) {
        if (users.containsKey(name)) {
            return false;
        }
        MemoryCredentials user = new MemoryCredentials();
        user.name = name;
        users.put(name, user);
        return true;
    }

    public void flushCaches() {
    }

    public boolean handles(String name) {
        return users.containsKey(name);
    }

    public boolean init(Properties properties) {
        users = new HashMap();
        return true;
    }

    public List list() {
        return Collections.unmodifiableList(new ArrayList(users.keySet()));
    }

    public boolean load(String name, Entity.Accessor accessor) {
        accessor.setMutable(true);
        return true;
    }

    public boolean remove(String name) {
        return users.remove(name) != null;
    }

    public boolean store(String name, Entity.Accessor accessor) {
        return true;
    }

    class MemoryCredentials {
        String name;
        String password;

        MemoryCredentials() {
        }
    }
}

