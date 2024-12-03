/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.Assertions
 *  net.jcip.annotations.Immutable
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.gadgets.spec;

import com.atlassian.gadgets.spec.DataType;
import com.atlassian.plugin.util.Assertions;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.jcip.annotations.Immutable;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Immutable
public final class UserPrefSpec {
    private final String name;
    private final String displayName;
    private final boolean required;
    private final DataType dataType;
    private final Map<String, String> enumValues;
    private final String defaultValue;

    private UserPrefSpec(Builder builder) {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.required = builder.required;
        this.dataType = builder.dataType;
        LinkedHashMap enumValuesCopy = new LinkedHashMap();
        for (String key : builder.enumValues.keySet()) {
            enumValuesCopy.put(key, builder.enumValues.get(key));
        }
        this.enumValues = Collections.unmodifiableMap(enumValuesCopy);
        this.defaultValue = builder.defaultValue;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean isRequired() {
        return this.required;
    }

    public DataType getDataType() {
        return this.dataType;
    }

    public Map<String, String> getEnumValues() {
        return this.enumValues;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public static Builder userPrefSpec(String name) {
        return new Builder(name);
    }

    public static Builder userPrefSpec(UserPrefSpec userPrefSpec) {
        return new Builder(userPrefSpec);
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("name", (Object)this.getName()).append("dataType", (Object)this.getDataType()).toString();
    }

    public static class Builder {
        private final String name;
        private String displayName;
        private boolean required;
        private DataType dataType;
        private Map<String, String> enumValues = Collections.emptyMap();
        private String defaultValue;

        private Builder(String name) {
            this.name = (String)Assertions.notNull((String)"name", (Object)name);
        }

        private Builder(UserPrefSpec spec) {
            Assertions.notNull((String)"spec", (Object)spec);
            this.name = spec.name;
            this.displayName = spec.displayName;
            this.required = spec.required;
            this.dataType = spec.dataType;
            this.enumValues = spec.enumValues;
            this.defaultValue = spec.defaultValue;
        }

        public Builder displayName(String displayName) {
            Assertions.notNull((String)"displayName", (Object)displayName);
            this.displayName = displayName;
            return this;
        }

        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        public Builder dataType(DataType dataType) {
            Assertions.notNull((String)"dataType", (Object)((Object)dataType));
            this.dataType = dataType;
            return this;
        }

        public Builder enumValues(Map<String, String> enumValues) {
            this.enumValues = (Map)Assertions.notNull((String)"enumValues", enumValues);
            return this;
        }

        public Builder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public UserPrefSpec build() {
            return new UserPrefSpec(this);
        }
    }
}

