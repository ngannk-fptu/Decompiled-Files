/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.api;

import java.io.InputStream;
import java.util.Map;

public interface EntityBuilder {
    public Entity build();

    public static interface Entity {
        public Map<String, String> getHeaders();

        public InputStream getInputStream();
    }
}

