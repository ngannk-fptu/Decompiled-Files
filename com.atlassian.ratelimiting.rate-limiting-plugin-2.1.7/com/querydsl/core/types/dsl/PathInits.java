/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 */
package com.querydsl.core.types.dsl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.querydsl.core.types.PathMetadata;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PathInits
implements Serializable {
    private static final long serialVersionUID = -2173980858324141095L;
    public static final PathInits DEFAULT = new PathInits(new String[0]);
    public static final PathInits DIRECT = new PathInits("*");
    public static final PathInits DIRECT2 = new PathInits("*.*");
    private final boolean initAllProps;
    private final PathInits defaultValue;
    private final Map<String, PathInits> propertyToInits = new HashMap<String, PathInits>();

    public PathInits(String ... initStrs) {
        boolean initAllProps = false;
        PathInits defaultValue = DEFAULT;
        HashMap properties = Maps.newHashMap();
        for (String initStr : initStrs) {
            ArrayList values;
            if (initStr.equals("*")) {
                initAllProps = true;
                continue;
            }
            if (initStr.startsWith("*.")) {
                initAllProps = true;
                defaultValue = new PathInits(initStr.substring(2));
                continue;
            }
            String key = initStr;
            ImmutableList inits = Collections.emptyList();
            if (initStr.contains(".")) {
                key = initStr.substring(0, initStr.indexOf(46));
                inits = ImmutableList.of((Object)initStr.substring(key.length() + 1));
            }
            if ((values = (ArrayList)properties.get(key)) == null) {
                values = new ArrayList();
                properties.put(key, values);
            }
            values.addAll(inits);
        }
        for (Map.Entry entry : properties.entrySet()) {
            PathInits inits = new PathInits((String[])Iterables.toArray((Iterable)((Iterable)entry.getValue()), String.class));
            this.propertyToInits.put((String)entry.getKey(), inits);
        }
        this.initAllProps = initAllProps;
        this.defaultValue = defaultValue;
    }

    public PathInits get(String property) {
        if (this.propertyToInits.containsKey(property)) {
            return this.propertyToInits.get(property);
        }
        if (this.initAllProps) {
            return this.defaultValue;
        }
        throw new IllegalArgumentException(property + " is not initialized");
    }

    public boolean isInitialized(String property) {
        return this.initAllProps || this.propertyToInits.containsKey(property);
    }

    public static PathInits getFor(PathMetadata metadata, PathInits root) {
        if (metadata.isRoot()) {
            return root;
        }
        if (metadata.getParent().getMetadata().isRoot()) {
            return DIRECT;
        }
        return DEFAULT;
    }
}

