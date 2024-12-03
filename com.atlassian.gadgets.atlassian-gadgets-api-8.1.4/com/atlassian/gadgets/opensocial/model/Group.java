/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.gadgets.opensocial.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.jcip.annotations.Immutable;

@Immutable
public final class Group {
    private final String name;
    private static final ConcurrentMap<String, Group> allGroups = new ConcurrentHashMap<String, Group>();
    public static final Group ALL = Group.of("ALL");
    public static final Group FRIENDS = Group.of("FRIENDS");
    public static final Group SELF = Group.of("SELF");

    private Group(String name) {
        this.name = name;
    }

    public static Group of(String name) {
        Group freshGroup;
        if (name == null) {
            throw new NullPointerException("name parameter to Group must not be null");
        }
        Group existingGroup = allGroups.putIfAbsent(name = name.intern(), freshGroup = new Group(name));
        return existingGroup == null ? freshGroup : existingGroup;
    }

    public String valueOf() {
        return this.name;
    }

    public String toString() {
        return this.valueOf();
    }
}

