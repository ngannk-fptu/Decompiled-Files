/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  okhttp3.MediaType
 */
package com.atlassian.migration.agent.okhttp;

import java.util.Objects;
import okhttp3.MediaType;

public final class MediaTypes {
    public static final String APPLICATION_JSON_VALUE = "application/json";
    public static final MediaType APPLICATION_JSON_TYPE = Objects.requireNonNull(MediaType.parse((String)"application/json"));
    public static final MediaType APPLICATION_STREAM_TYPE = Objects.requireNonNull(MediaType.parse((String)"application/octet-stream"));

    private MediaTypes() {
    }
}

