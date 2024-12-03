/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.interceptor;

import java.util.Map;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkProtocolMetadata;
import software.amazon.awssdk.core.SelectedAuthScheme;
import software.amazon.awssdk.core.checksums.ChecksumSpecs;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.trait.HttpChecksum;
import software.amazon.awssdk.core.interceptor.trait.HttpChecksumRequired;
import software.amazon.awssdk.core.internal.interceptor.trait.RequestCompression;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.http.SdkHttpExecutionAttributes;
import software.amazon.awssdk.http.auth.spi.scheme.AuthScheme;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeProvider;
import software.amazon.awssdk.identity.spi.IdentityProviders;
import software.amazon.awssdk.utils.AttributeMap;

@SdkProtectedApi
public final class SdkInternalExecutionAttribute
extends SdkExecutionAttribute {
    public static final ExecutionAttribute<Boolean> IS_FULL_DUPLEX = new ExecutionAttribute("IsFullDuplex");
    public static final ExecutionAttribute<Boolean> HAS_INITIAL_REQUEST_EVENT = new ExecutionAttribute("HasInitialRequestEvent");
    public static final ExecutionAttribute<HttpChecksumRequired> HTTP_CHECKSUM_REQUIRED = new ExecutionAttribute("HttpChecksumRequired");
    public static final ExecutionAttribute<Boolean> DISABLE_HOST_PREFIX_INJECTION = new ExecutionAttribute("DisableHostPrefixInjection");
    public static final ExecutionAttribute<HttpChecksum> HTTP_CHECKSUM = new ExecutionAttribute("HttpChecksum");
    public static final ExecutionAttribute<SdkHttpExecutionAttributes> SDK_HTTP_EXECUTION_ATTRIBUTES = new ExecutionAttribute("SdkHttpExecutionAttributes");
    public static final ExecutionAttribute<Boolean> IS_NONE_AUTH_TYPE_REQUEST = new ExecutionAttribute("IsNoneAuthTypeRequest");
    public static final ExecutionAttribute<EndpointProvider> ENDPOINT_PROVIDER = new ExecutionAttribute("EndpointProvider");
    public static final ExecutionAttribute<Endpoint> RESOLVED_ENDPOINT = new ExecutionAttribute("ResolvedEndpoint");
    public static final ExecutionAttribute<AttributeMap> CLIENT_CONTEXT_PARAMS = new ExecutionAttribute("ClientContextParams");
    public static final ExecutionAttribute<Boolean> IS_DISCOVERED_ENDPOINT = new ExecutionAttribute("IsDiscoveredEndpoint");
    public static final ExecutionAttribute<AuthSchemeProvider> AUTH_SCHEME_RESOLVER = new ExecutionAttribute("AuthSchemeProvider");
    public static final ExecutionAttribute<Map<String, AuthScheme<?>>> AUTH_SCHEMES = new ExecutionAttribute("AuthSchemes");
    public static final ExecutionAttribute<IdentityProviders> IDENTITY_PROVIDERS = new ExecutionAttribute("IdentityProviders");
    public static final ExecutionAttribute<SelectedAuthScheme<?>> SELECTED_AUTH_SCHEME = new ExecutionAttribute("SelectedAuthScheme");
    public static final ExecutionAttribute<RequestCompression> REQUEST_COMPRESSION = new ExecutionAttribute("RequestCompression");
    public static final ExecutionAttribute<SdkProtocolMetadata> PROTOCOL_METADATA = new ExecutionAttribute("ProtocolMetadata");
    static final ExecutionAttribute<ChecksumSpecs> INTERNAL_RESOLVED_CHECKSUM_SPECS = new ExecutionAttribute("InternalResolvedChecksumSpecs");

    private SdkInternalExecutionAttribute() {
    }
}

