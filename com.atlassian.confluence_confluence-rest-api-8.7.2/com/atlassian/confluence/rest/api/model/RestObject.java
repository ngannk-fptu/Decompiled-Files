/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.util.FugueConversionUtil
 *  com.atlassian.fugue.Pair
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  io.atlassian.fugue.Pair
 */
package com.atlassian.confluence.rest.api.model;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.util.FugueConversionUtil;
import com.atlassian.fugue.Pair;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;

@ExperimentalApi
public class RestObject {
    protected final Map<String, Object> jsonProperties = Maps.newLinkedHashMap();

    @Deprecated
    public ImmutableMap<String, Object> getProperties() {
        return ImmutableMap.copyOf(this.jsonProperties);
    }

    public Map<String, Object> properties() {
        return this.getProperties();
    }

    public boolean hasProperty(String key) {
        return this.jsonProperties.containsKey(key);
    }

    public Object getProperty(String key) {
        return this.jsonProperties.get(key);
    }

    public Object removeProperty(String key) {
        return this.jsonProperties.remove(key);
    }

    public final Object putProperty(String key, Object value) {
        return this.jsonProperties.put(key, value);
    }

    @Deprecated
    public final Pair putProperty(Pair<String, Object> keyValuePair) {
        return FugueConversionUtil.toComPair((io.atlassian.fugue.Pair)this.addProperty((io.atlassian.fugue.Pair<String, Object>)FugueConversionUtil.toIoPair(keyValuePair)));
    }

    @Deprecated
    public final io.atlassian.fugue.Pair addProperty(io.atlassian.fugue.Pair<String, Object> keyValuePair) {
        return io.atlassian.fugue.Pair.pair((Object)((String)keyValuePair.left()), (Object)this.jsonProperties.put((String)keyValuePair.left(), keyValuePair.right()));
    }

    public final void putProperties(Map<String, Object> properties) {
        this.jsonProperties.putAll(properties);
    }

    public static class Properties {
        public static final String EXPANDABLE_PROPERTIES_KEY = "_expandable";
        public static final String LINKS_PROPERTIES_KEY = "_links";
        public static final String GRAPHQL_LINKS_PROPERTIES_KEY = "links";
    }
}

