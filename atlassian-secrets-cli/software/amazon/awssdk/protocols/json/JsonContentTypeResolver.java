/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.protocols.json.AwsJsonProtocolMetadata;

@SdkProtectedApi
public interface JsonContentTypeResolver {
    public String resolveContentType(AwsJsonProtocolMetadata var1);
}

