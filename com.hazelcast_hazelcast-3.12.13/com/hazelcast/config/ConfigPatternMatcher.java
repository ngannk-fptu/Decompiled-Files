/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigurationException;

public interface ConfigPatternMatcher {
    public String matches(Iterable<String> var1, String var2) throws ConfigurationException;
}

