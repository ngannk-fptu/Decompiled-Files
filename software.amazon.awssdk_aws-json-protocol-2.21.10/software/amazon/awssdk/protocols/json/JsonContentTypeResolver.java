/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.protocols.json;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.protocols.json.AwsJsonProtocolMetadata;

@SdkProtectedApi
public interface JsonContentTypeResolver {
    public String resolveContentType(AwsJsonProtocolMetadata var1);
}

