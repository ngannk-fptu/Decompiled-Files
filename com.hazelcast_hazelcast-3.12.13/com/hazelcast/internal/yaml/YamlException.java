/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

import com.hazelcast.core.HazelcastException;

public class YamlException
extends HazelcastException {
    public YamlException(String message) {
        super(message);
    }

    public YamlException(String message, Throwable cause) {
        super(message, cause);
    }
}

