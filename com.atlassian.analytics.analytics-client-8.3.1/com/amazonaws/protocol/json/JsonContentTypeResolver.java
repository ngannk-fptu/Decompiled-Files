/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.protocol.json.JsonClientMetadata;
import com.amazonaws.protocol.json.JsonContentTypeResolverImpl;

@SdkInternalApi
interface JsonContentTypeResolver {
    public static final JsonContentTypeResolver ION_BINARY = new JsonContentTypeResolverImpl("application/x-amz-ion-");
    public static final JsonContentTypeResolver ION_TEXT = new JsonContentTypeResolverImpl("text/x-amz-ion-");
    public static final JsonContentTypeResolver CBOR = new JsonContentTypeResolverImpl("application/x-amz-cbor-");
    public static final JsonContentTypeResolver JSON = new JsonContentTypeResolverImpl("application/x-amz-json-");

    public String resolveContentType(JsonClientMetadata var1);
}

