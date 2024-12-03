/*
 * Decompiled with CFR 0.152.
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
    private final Map<String, Object> orderedMap = new LinkedHashMap<String, Object>();

    @Override
    public Object put(String s, Object o) {
        this.orderedMap.put(s, o);
        return super.put(s, o);
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
        this.orderedMap.putAll(map);
        super.putAll(map);
    }

    @Override
    public Set<String> keySet() {
        return this.orderedMap.keySet();
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return this.orderedMap.entrySet();
    }

    @Override
    public Object remove(Object o) {
        this.orderedMap.remove(o);
        return super.remove(o);
    }

    @Override
    public void clear() {
        this.orderedMap.clear();
        super.clear();
    }

    @Override
    public String toJSONString() {
        return JSONObject.toJSONString(this.orderedMap);
    }
}

