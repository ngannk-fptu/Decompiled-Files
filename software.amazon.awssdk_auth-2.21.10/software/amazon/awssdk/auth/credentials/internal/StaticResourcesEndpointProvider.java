/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.regions.util.ResourcesEndpointProvider
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.auth.credentials.internal;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.regions.util.ResourcesEndpointProvider;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class StaticResourcesEndpointProvider
implements ResourcesEndpointProvider {
    private final URI endpoint;
    private final Map<String, String> headers;

    public StaticResourcesEndpointProvider(URI endpoint, Map<String, String> additionalHeaders) {
        this.endpoint = (URI)Validate.paramNotNull((Object)endpoint, (String)"endpoint");
        this.headers = super.headers();
        if (additionalHeaders != null) {
            this.headers.putAll(additionalHeaders);
        }
    }

    public URI endpoint() throws IOException {
        return this.endpoint;
    }

    public Map<String, String> headers() {
        return Collections.unmodifiableMap(this.headers);
    }
}

