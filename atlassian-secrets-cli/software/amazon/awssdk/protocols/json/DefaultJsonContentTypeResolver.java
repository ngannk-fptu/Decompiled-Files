/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.protocols.json.AwsJsonProtocol;
import software.amazon.awssdk.protocols.json.AwsJsonProtocolMetadata;
import software.amazon.awssdk.protocols.json.JsonContentTypeResolver;

@SdkProtectedApi
public class DefaultJsonContentTypeResolver
implements JsonContentTypeResolver {
    private static final String REST_JSON_CONTENT_TYPE = "application/json";
    private final String prefix;

    public DefaultJsonContentTypeResolver(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String resolveContentType(AwsJsonProtocolMetadata protocolMetadata) {
        if (AwsJsonProtocol.REST_JSON.equals((Object)protocolMetadata.protocol())) {
            return REST_JSON_CONTENT_TYPE;
        }
        return this.prefix + protocolMetadata.protocolVersion();
    }
}

