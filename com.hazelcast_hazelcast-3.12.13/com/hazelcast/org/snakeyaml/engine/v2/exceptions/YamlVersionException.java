/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.exceptions;

import com.hazelcast.org.snakeyaml.engine.v2.common.SpecVersion;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.YamlEngineException;

public class YamlVersionException
extends YamlEngineException {
    private final SpecVersion specVersion;

    public YamlVersionException(SpecVersion specVersion) {
        super(specVersion.toString());
        this.specVersion = specVersion;
    }

    public SpecVersion getSpecVersion() {
        return this.specVersion;
    }
}

