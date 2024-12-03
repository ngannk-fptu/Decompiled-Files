/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableListMultimap
 *  com.google.common.collect.ImmutableListMultimap$Builder
 *  com.google.common.collect.Multimap
 */
package com.atlassian.plugin;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class PluginDependencies {
    private final Set<String> mandatory;
    private final Set<String> optional;
    private final Set<String> dynamic;
    private final Set<String> all;
    private final Multimap<String, Type> byPluginKey;

    public PluginDependencies() {
        this(null, null, null);
    }

    public PluginDependencies(Set<String> mandatory, Set<String> optional, Set<String> dynamic) {
        this.mandatory = mandatory == null ? Collections.emptySet() : Collections.unmodifiableSet(mandatory);
        this.optional = optional == null ? Collections.emptySet() : Collections.unmodifiableSet(optional);
        this.dynamic = dynamic == null ? Collections.emptySet() : Collections.unmodifiableSet(dynamic);
        HashSet<String> combined = new HashSet<String>();
        combined.addAll(this.mandatory);
        combined.addAll(this.optional);
        combined.addAll(this.dynamic);
        this.all = Collections.unmodifiableSet(combined);
        ImmutableListMultimap.Builder byPluginKeyBuilder = ImmutableListMultimap.builder();
        for (String key : this.mandatory) {
            byPluginKeyBuilder.put((Object)key, (Object)Type.MANDATORY);
        }
        for (String key : this.optional) {
            byPluginKeyBuilder.put((Object)key, (Object)Type.OPTIONAL);
        }
        for (String key : this.dynamic) {
            byPluginKeyBuilder.put((Object)key, (Object)Type.DYNAMIC);
        }
        this.byPluginKey = byPluginKeyBuilder.build();
    }

    public Set<String> getMandatory() {
        return this.mandatory;
    }

    public Set<String> getOptional() {
        return this.optional;
    }

    public Set<String> getDynamic() {
        return this.dynamic;
    }

    public Set<String> getAll() {
        return this.all;
    }

    @Deprecated
    public Multimap<String, Type> getByPluginKey() {
        return this.byPluginKey;
    }

    public Map<String, SortedSet<Type>> getTypesByPluginKey() {
        HashMap<String, TreeSet> typesByPluginKeyLocal = new HashMap<String, TreeSet>();
        for (Map.Entry entry : this.byPluginKey.entries()) {
            typesByPluginKeyLocal.computeIfAbsent((String)entry.getKey(), x -> new TreeSet<Type>(Comparator.comparingInt(Enum::ordinal))).add(entry.getValue());
        }
        return Collections.unmodifiableMap(typesByPluginKeyLocal);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        final Set<String> mandatory = new HashSet<String>();
        final Set<String> optional = new HashSet<String>();
        final Set<String> dynamic = new HashSet<String>();

        private Builder() {
        }

        public Builder withMandatory(String ... pluginKey) {
            this.mandatory.addAll(Arrays.asList(pluginKey));
            return this;
        }

        public Builder withOptional(String ... pluginKey) {
            this.optional.addAll(Arrays.asList(pluginKey));
            return this;
        }

        public Builder withDynamic(String ... pluginKey) {
            this.dynamic.addAll(Arrays.asList(pluginKey));
            return this;
        }

        public PluginDependencies build() {
            return new PluginDependencies(Collections.unmodifiableSet(this.mandatory), Collections.unmodifiableSet(this.optional), Collections.unmodifiableSet(this.dynamic));
        }
    }

    public static enum Type {
        MANDATORY,
        OPTIONAL,
        DYNAMIC;


        public boolean lessSignificant(Type other) {
            return this.ordinal() > other.ordinal();
        }
    }
}

