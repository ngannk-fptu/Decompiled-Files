/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugin.notifications.api.notification;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class FilterConfiguration {
    @JsonIgnore
    final Map<String, String> paramMap = Maps.newHashMap();

    @JsonCreator
    public FilterConfiguration(@JsonProperty(value="params") List<FilterParam> params) {
        for (FilterParam param : params) {
            this.paramMap.put(param.getName(), param.getValue());
        }
    }

    public String get(String key) {
        return this.paramMap.get(key);
    }

    @JsonProperty(value="params")
    public List<FilterParam> getParameterList() {
        ArrayList ret = Lists.newArrayList();
        for (Map.Entry<String, String> entry : this.paramMap.entrySet()) {
            ret.add(new FilterParam(entry.getKey(), entry.getValue()));
        }
        return ret;
    }

    public Map<String, String> getParams() {
        return Collections.unmodifiableMap(this.paramMap);
    }

    public static class FilterParam {
        @JsonProperty
        private final String name;
        @JsonProperty
        private final String value;

        @JsonCreator
        public FilterParam(@JsonProperty(value="name") String name, @JsonProperty(value="value") String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }
    }
}

