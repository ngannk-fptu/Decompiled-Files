/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.marketplace.client.http;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public interface RequestDecorator {
    public Map<String, String> getRequestHeaders();

    public static abstract class Instances {
        public static RequestDecorator forHeaders(Map<String, String> headers) {
            return () -> ImmutableMap.copyOf((Map)headers);
        }
    }
}

