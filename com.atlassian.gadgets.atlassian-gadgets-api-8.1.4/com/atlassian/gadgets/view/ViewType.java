/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.view;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ViewType
implements Serializable {
    private static final Map<String, ViewType> allViewTypes = new HashMap<String, ViewType>();
    private static final ReentrantReadWriteLock viewTypeRegistrationLock = new ReentrantReadWriteLock();
    private static final Lock readLock = viewTypeRegistrationLock.readLock();
    private static final Lock writeLock = viewTypeRegistrationLock.writeLock();
    public static final ViewType DEFAULT = ViewType.createViewType("default", "DEFAULT", "DASHBOARD", "profile", "home");
    public static final ViewType DIRECTORY = ViewType.createViewType("directory", new String[0]);
    public static final ViewType CANVAS = ViewType.createViewType("canvas", new String[0]);
    private final String name;
    private final List<String> aliases;

    private ViewType(String name, String ... aliases) {
        this.name = name;
        this.aliases = Collections.unmodifiableList(Arrays.asList(aliases));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ViewType createViewType(String name, String ... aliases) {
        writeLock.lock();
        try {
            if (allViewTypes.containsKey(name)) {
                throw new IllegalArgumentException("Failed to create ViewType; an existing ViewType with name  " + name + " already exists");
            }
            for (String alias : aliases) {
                if (!allViewTypes.containsKey(alias)) continue;
                throw new IllegalArgumentException("Failed to create ViewType; an existing ViewType with alias  " + alias + " already exists");
            }
            ViewType viewType = new ViewType(name, aliases);
            allViewTypes.put(name, viewType);
            for (String alias : aliases) {
                allViewTypes.put(alias, viewType);
            }
            ViewType viewType2 = viewType;
            return viewType2;
        }
        finally {
            writeLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean removeViewType(ViewType viewType) {
        writeLock.lock();
        try {
            boolean result;
            boolean bl = result = allViewTypes.remove(viewType.getCanonicalName()) != null;
            if (result) {
                for (String alias : viewType.getAliases()) {
                    ViewType aliasedView = allViewTypes.remove(alias);
                    assert (viewType.equals(aliasedView));
                }
            }
            boolean bl2 = result;
            return bl2;
        }
        finally {
            writeLock.unlock();
        }
    }

    public String getCanonicalName() {
        return this.name;
    }

    public Collection<String> getAliases() {
        return this.aliases;
    }

    public static ViewType valueOf(String value) {
        readLock.lock();
        try {
            ViewType result = allViewTypes.get(value);
            if (result == null) {
                throw new IllegalArgumentException("No such ViewType: " + value);
            }
            ViewType viewType = result;
            return viewType;
        }
        finally {
            readLock.unlock();
        }
    }

    public String toString() {
        return this.getCanonicalName();
    }
}

