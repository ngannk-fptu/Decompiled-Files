/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.endpoints;

import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.awscore.endpoints.authscheme.EndpointAuthScheme;
import software.amazon.awssdk.endpoints.EndpointAttributeKey;

@SdkProtectedApi
public final class AwsEndpointAttribute {
    public static final EndpointAttributeKey<List<EndpointAuthScheme>> AUTH_SCHEMES = EndpointAttributeKey.forList("AuthSchemes");

    private AwsEndpointAttribute() {
    }

    public static List<EndpointAttributeKey<?>> values() {
        return Collections.singletonList(AUTH_SCHEMES);
    }
}

