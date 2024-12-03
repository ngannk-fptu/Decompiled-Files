/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.collections.CompositeMap
 *  org.apache.velocity.context.Context
 */
package com.atlassian.confluence.velocity;

import com.atlassian.confluence.util.collections.CompositeMap;
import com.atlassian.confluence.velocity.ContextMapView;
import java.util.HashMap;
import java.util.Map;
import org.apache.velocity.context.Context;

public final class ContextUtils {
    private ContextUtils() {
    }

    public static Map<String, Object> toMap(Context context) {
        HashMap mutable = new HashMap();
        return CompositeMap.of(mutable, (Map)new ContextMapView(context));
    }

    public static void putAll(Context dest, Context source) {
        for (Object key : source.getKeys()) {
            dest.put(key.toString(), source.get(key.toString()));
        }
    }
}

