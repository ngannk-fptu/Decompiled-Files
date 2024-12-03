/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.protocol.json.JsonClientMetadata;
import com.amazonaws.protocol.json.JsonContentTypeResolver;

@SdkInternalApi
class JsonContentTypeResolverImpl
implements JsonContentTypeResolver {
    private final String prefix;

    JsonContentTypeResolverImpl(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String resolveContentType(JsonClientMetadata metadata) {
        return metadata.getContentTypeOverride() != null ? metadata.getContentTypeOverride() : this.prefix + metadata.getProtocolVersion();
    }
}

