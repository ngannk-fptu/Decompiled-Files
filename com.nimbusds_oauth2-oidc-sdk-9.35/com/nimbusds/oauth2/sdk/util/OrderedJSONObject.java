/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.NotThreadSafe
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.NotThreadSafe;
import net.minidev.json.JSONObject;

@NotThreadSafe
public class OrderedJSONObject
extends JSONObject {
    private static final long serialVersionUID = -8682025379611131137L;
    private final Map<String, Object> orderedMap = new LinkedHashMap<String, Object>();

    public Object put(String s, Object o) {
        this.orderedMap.put(s, o);
        return super.put((Object)s, o);
    }

    public void putAll(Map<? extends String, ?> map) {
        this.orderedMap.putAll(map);
        super.putAll(map);
    }

    public Set<String> keySet() {
        return this.orderedMap.keySet();
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return this.orderedMap.entrySet();
    }

    public Object remove(Object o) {
        this.orderedMap.remove(o);
        return super.remove(o);
    }

    public void clear() {
        this.orderedMap.clear();
        super.clear();
    }

    public String toJSONString() {
        return JSONObject.toJSONString(this.orderedMap);
    }
}

