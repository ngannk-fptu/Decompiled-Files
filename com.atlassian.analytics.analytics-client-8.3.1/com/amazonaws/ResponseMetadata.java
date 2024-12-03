/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws;

import java.util.Map;

public class ResponseMetadata {
    public static final String AWS_REQUEST_ID = "AWS_REQUEST_ID";
    public static final String AWS_EXTENDED_REQUEST_ID = "AWS_EXTENDED_REQUEST_ID";
    protected final Map<String, String> metadata;

    public ResponseMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public ResponseMetadata(ResponseMetadata originalResponseMetadata) {
        this(originalResponseMetadata.metadata);
    }

    public String getRequestId() {
        return this.metadata.get(AWS_REQUEST_ID);
    }

    public String toString() {
        if (this.metadata == null) {
            return "{}";
        }
        return this.metadata.toString();
    }
}

