/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.parser;

import com.hazelcast.org.snakeyaml.engine.v2.common.SpecVersion;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

class VersionTagsTuple {
    private Optional<SpecVersion> specVersion;
    private Map<String, String> tags;

    public VersionTagsTuple(Optional<SpecVersion> specVersion, Map<String, String> tags) {
        Objects.requireNonNull(specVersion);
        this.specVersion = specVersion;
        this.tags = tags;
    }

    public Optional<SpecVersion> getSpecVersion() {
        return this.specVersion;
    }

    public Map<String, String> getTags() {
        return this.tags;
    }

    public String toString() {
        return String.format("VersionTagsTuple<%s, %s>", this.specVersion, this.tags);
    }
}

