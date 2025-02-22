/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.api;

import com.hazelcast.org.snakeyaml.engine.v2.api.ConstructNode;
import com.hazelcast.org.snakeyaml.engine.v2.api.LoadSettingsBuilder;
import com.hazelcast.org.snakeyaml.engine.v2.api.SettingKey;
import com.hazelcast.org.snakeyaml.engine.v2.common.SpecVersion;
import com.hazelcast.org.snakeyaml.engine.v2.env.EnvConfig;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Tag;
import com.hazelcast.org.snakeyaml.engine.v2.resolver.ScalarResolver;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

public final class LoadSettings {
    private final String label;
    private final Map<Tag, ConstructNode> tagConstructors;
    private final ScalarResolver scalarResolver;
    private final IntFunction<List> defaultList;
    private final IntFunction<Set> defaultSet;
    private final IntFunction<Map> defaultMap;
    private final UnaryOperator<SpecVersion> versionFunction;
    private final Integer bufferSize;
    private final boolean allowDuplicateKeys;
    private final boolean allowRecursiveKeys;
    private final int maxAliasesForCollections;
    private final boolean useMarks;
    private final Optional<EnvConfig> envConfig;
    private final Map<SettingKey, Object> customProperties;

    LoadSettings(String label, Map<Tag, ConstructNode> tagConstructors, ScalarResolver scalarResolver, IntFunction<List> defaultList, IntFunction<Set> defaultSet, IntFunction<Map> defaultMap, UnaryOperator<SpecVersion> versionFunction, Integer bufferSize, boolean allowDuplicateKeys, boolean allowRecursiveKeys, int maxAliasesForCollections, boolean useMarks, Map<SettingKey, Object> customProperties, Optional<EnvConfig> envConfig) {
        this.label = label;
        this.tagConstructors = tagConstructors;
        this.scalarResolver = scalarResolver;
        this.defaultList = defaultList;
        this.defaultSet = defaultSet;
        this.defaultMap = defaultMap;
        this.versionFunction = versionFunction;
        this.bufferSize = bufferSize;
        this.allowDuplicateKeys = allowDuplicateKeys;
        this.allowRecursiveKeys = allowRecursiveKeys;
        this.maxAliasesForCollections = maxAliasesForCollections;
        this.useMarks = useMarks;
        this.customProperties = customProperties;
        this.envConfig = envConfig;
    }

    public static final LoadSettingsBuilder builder() {
        return new LoadSettingsBuilder();
    }

    public String getLabel() {
        return this.label;
    }

    public Map<Tag, ConstructNode> getTagConstructors() {
        return this.tagConstructors;
    }

    public ScalarResolver getScalarResolver() {
        return this.scalarResolver;
    }

    public IntFunction<List> getDefaultList() {
        return this.defaultList;
    }

    public IntFunction<Set> getDefaultSet() {
        return this.defaultSet;
    }

    public IntFunction<Map> getDefaultMap() {
        return this.defaultMap;
    }

    public Integer getBufferSize() {
        return this.bufferSize;
    }

    public boolean getAllowDuplicateKeys() {
        return this.allowDuplicateKeys;
    }

    public boolean getAllowRecursiveKeys() {
        return this.allowRecursiveKeys;
    }

    public boolean getUseMarks() {
        return this.useMarks;
    }

    public Function<SpecVersion, SpecVersion> getVersionFunction() {
        return this.versionFunction;
    }

    public Object getCustomProperty(SettingKey key) {
        return this.customProperties.get(key);
    }

    public int getMaxAliasesForCollections() {
        return this.maxAliasesForCollections;
    }

    public Optional<EnvConfig> getEnvConfig() {
        return this.envConfig;
    }
}

