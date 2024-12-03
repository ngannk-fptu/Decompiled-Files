/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.api;

import com.hazelcast.org.snakeyaml.engine.v2.api.ConstructNode;
import com.hazelcast.org.snakeyaml.engine.v2.api.LoadSettings;
import com.hazelcast.org.snakeyaml.engine.v2.api.SettingKey;
import com.hazelcast.org.snakeyaml.engine.v2.common.SpecVersion;
import com.hazelcast.org.snakeyaml.engine.v2.env.EnvConfig;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.YamlVersionException;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Tag;
import com.hazelcast.org.snakeyaml.engine.v2.resolver.JsonScalarResolver;
import com.hazelcast.org.snakeyaml.engine.v2.resolver.ScalarResolver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

public final class LoadSettingsBuilder {
    private String label = "reader";
    private Map<Tag, ConstructNode> tagConstructors;
    private ScalarResolver scalarResolver;
    private IntFunction<List> defaultList;
    private IntFunction<Set> defaultSet;
    private IntFunction<Map> defaultMap;
    private UnaryOperator<SpecVersion> versionFunction;
    private Integer bufferSize = 1024;
    private boolean allowDuplicateKeys = false;
    private boolean allowRecursiveKeys = false;
    private int maxAliasesForCollections = 50;
    private boolean useMarks = true;
    private Optional<EnvConfig> envConfig;
    private Map<SettingKey, Object> customProperties = new HashMap<SettingKey, Object>();

    LoadSettingsBuilder() {
        this.tagConstructors = new HashMap<Tag, ConstructNode>();
        this.scalarResolver = new JsonScalarResolver();
        this.defaultList = ArrayList::new;
        this.defaultSet = LinkedHashSet::new;
        this.defaultMap = LinkedHashMap::new;
        this.versionFunction = version -> {
            if (version.getMajor() != 1) {
                throw new YamlVersionException((SpecVersion)version);
            }
            return version;
        };
        this.envConfig = Optional.empty();
    }

    public LoadSettingsBuilder setLabel(String label) {
        Objects.requireNonNull(label, "label cannot be null");
        this.label = label;
        return this;
    }

    public LoadSettingsBuilder setTagConstructors(Map<Tag, ConstructNode> tagConstructors) {
        this.tagConstructors = tagConstructors;
        return this;
    }

    public LoadSettingsBuilder setScalarResolver(ScalarResolver scalarResolver) {
        Objects.requireNonNull(scalarResolver, "scalarResolver cannot be null");
        this.scalarResolver = scalarResolver;
        return this;
    }

    public LoadSettingsBuilder setDefaultList(IntFunction<List> defaultList) {
        Objects.requireNonNull(defaultList, "defaultList cannot be null");
        this.defaultList = defaultList;
        return this;
    }

    public LoadSettingsBuilder setDefaultSet(IntFunction<Set> defaultSet) {
        Objects.requireNonNull(defaultSet, "defaultSet cannot be null");
        this.defaultSet = defaultSet;
        return this;
    }

    public LoadSettingsBuilder setDefaultMap(IntFunction<Map> defaultMap) {
        Objects.requireNonNull(defaultMap, "defaultMap cannot be null");
        this.defaultMap = defaultMap;
        return this;
    }

    public LoadSettingsBuilder setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    public LoadSettingsBuilder setAllowDuplicateKeys(boolean allowDuplicateKeys) {
        this.allowDuplicateKeys = allowDuplicateKeys;
        return this;
    }

    public LoadSettingsBuilder setAllowRecursiveKeys(boolean allowRecursiveKeys) {
        this.allowRecursiveKeys = allowRecursiveKeys;
        return this;
    }

    public LoadSettingsBuilder setMaxAliasesForCollections(int maxAliasesForCollections) {
        this.maxAliasesForCollections = maxAliasesForCollections;
        return this;
    }

    public LoadSettingsBuilder setUseMarks(boolean useMarks) {
        this.useMarks = useMarks;
        return this;
    }

    public LoadSettingsBuilder setVersionFunction(UnaryOperator<SpecVersion> versionFunction) {
        Objects.requireNonNull(versionFunction, "versionFunction cannot be null");
        this.versionFunction = versionFunction;
        return this;
    }

    public LoadSettingsBuilder setEnvConfig(Optional<EnvConfig> envConfig) {
        this.envConfig = envConfig;
        return this;
    }

    public LoadSettingsBuilder setCustomProperty(SettingKey key, Object value) {
        this.customProperties.put(key, value);
        return this;
    }

    public LoadSettings build() {
        return new LoadSettings(this.label, this.tagConstructors, this.scalarResolver, this.defaultList, this.defaultSet, this.defaultMap, this.versionFunction, this.bufferSize, this.allowDuplicateKeys, this.allowRecursiveKeys, this.maxAliasesForCollections, this.useMarks, this.customProperties, this.envConfig);
    }
}

