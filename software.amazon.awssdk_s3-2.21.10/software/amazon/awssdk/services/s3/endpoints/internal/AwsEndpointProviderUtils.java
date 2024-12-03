/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.AwsExecutionAttribute
 *  software.amazon.awssdk.awscore.endpoints.AwsEndpointAttribute
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.SdkExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute
 *  software.amazon.awssdk.endpoints.Endpoint
 *  software.amazon.awssdk.endpoints.Endpoint$Builder
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.FunctionalUtils
 *  software.amazon.awssdk.utils.HostnameValidator
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.http.SdkHttpUtils
 */
package software.amazon.awssdk.services.s3.endpoints.internal;

import java.net.URI;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.AwsExecutionAttribute;
import software.amazon.awssdk.awscore.endpoints.AwsEndpointAttribute;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.endpoints.internal.AuthSchemeUtils;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.HostnameValidator;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
public final class AwsEndpointProviderUtils {
    private static final Logger LOG = Logger.loggerFor(AwsEndpointProviderUtils.class);

    private AwsEndpointProviderUtils() {
    }

    public static Region regionBuiltIn(ExecutionAttributes executionAttributes) {
        return (Region)executionAttributes.getAttribute(AwsExecutionAttribute.AWS_REGION);
    }

    public static Boolean dualStackEnabledBuiltIn(ExecutionAttributes executionAttributes) {
        return (Boolean)executionAttributes.getAttribute(AwsExecutionAttribute.DUALSTACK_ENDPOINT_ENABLED);
    }

    public static Boolean fipsEnabledBuiltIn(ExecutionAttributes executionAttributes) {
        return (Boolean)executionAttributes.getAttribute(AwsExecutionAttribute.FIPS_ENDPOINT_ENABLED);
    }

    public static String endpointBuiltIn(ExecutionAttributes executionAttributes) {
        if (AwsEndpointProviderUtils.endpointIsOverridden(executionAttributes)) {
            return (String)FunctionalUtils.invokeSafely(() -> {
                URI endpointOverride = (URI)executionAttributes.getAttribute(SdkExecutionAttribute.CLIENT_ENDPOINT);
                return new URI(endpointOverride.getScheme(), null, endpointOverride.getHost(), endpointOverride.getPort(), endpointOverride.getPath(), null, endpointOverride.getFragment()).toString();
            });
        }
        return null;
    }

    public static Boolean useGlobalEndpointBuiltIn(ExecutionAttributes executionAttributes) {
        return (Boolean)executionAttributes.getAttribute(AwsExecutionAttribute.USE_GLOBAL_ENDPOINT);
    }

    public static boolean endpointIsOverridden(ExecutionAttributes attrs) {
        return attrs.getOptionalAttribute(SdkExecutionAttribute.ENDPOINT_OVERRIDDEN).orElse(false);
    }

    public static boolean endpointIsDiscovered(ExecutionAttributes attrs) {
        return attrs.getOptionalAttribute(SdkInternalExecutionAttribute.IS_DISCOVERED_ENDPOINT).orElse(false);
    }

    public static boolean disableHostPrefixInjection(ExecutionAttributes attrs) {
        return attrs.getOptionalAttribute(SdkInternalExecutionAttribute.DISABLE_HOST_PREFIX_INJECTION).orElse(false);
    }

    public static Endpoint addHostPrefix(Endpoint endpoint, String prefix) {
        if (StringUtils.isBlank((CharSequence)prefix)) {
            return endpoint;
        }
        AwsEndpointProviderUtils.validatePrefixIsHostNameCompliant(prefix);
        URI originalUrl = endpoint.url();
        String newHost = prefix + endpoint.url().getHost();
        URI newUrl = (URI)FunctionalUtils.invokeSafely(() -> new URI(originalUrl.getScheme(), null, newHost, originalUrl.getPort(), originalUrl.getPath(), originalUrl.getQuery(), originalUrl.getFragment()));
        return endpoint.toBuilder().url(newUrl).build();
    }

    public static Endpoint valueAsEndpointOrThrow(Value value) {
        if (value instanceof Value.Endpoint) {
            Value.Endpoint endpoint = value.expectEndpoint();
            Endpoint.Builder builder = Endpoint.builder();
            builder.url(URI.create(endpoint.getUrl()));
            Map<String, List<String>> headers = endpoint.getHeaders();
            if (headers != null) {
                headers.forEach((name, values) -> values.forEach(v -> builder.putHeader(name, v)));
            }
            AwsEndpointProviderUtils.addKnownProperties(builder, endpoint.getProperties());
            return builder.build();
        }
        if (value instanceof Value.Str) {
            String errorMsg = value.expectString();
            if (errorMsg.contains("Invalid ARN") && errorMsg.contains(":s3:::")) {
                errorMsg = errorMsg + ". Use the bucket name instead of simple bucket ARNs in GetBucketLocationRequest.";
            }
            throw SdkClientException.create((String)errorMsg);
        }
        throw SdkClientException.create((String)("Rule engine return neither an endpoint result or error value. Returned value was:" + value));
    }

    public static SdkHttpRequest setUri(SdkHttpRequest request, URI clientEndpoint, URI resolvedUri) {
        String clientEndpointPath = clientEndpoint.getRawPath();
        String requestPath = request.encodedPath();
        String resolvedUriPath = resolvedUri.getRawPath();
        String finalPath = requestPath;
        if (!resolvedUriPath.equals(clientEndpointPath)) {
            finalPath = AwsEndpointProviderUtils.combinePath(clientEndpointPath, requestPath, resolvedUriPath);
        }
        return (SdkHttpRequest)((SdkHttpRequest.Builder)request.toBuilder()).protocol(resolvedUri.getScheme()).host(resolvedUri.getHost()).port(Integer.valueOf(resolvedUri.getPort())).encodedPath(finalPath).build();
    }

    private static String combinePath(String clientEndpointPath, String requestPath, String resolvedUriPath) {
        String requestPathWithClientPathRemoved = StringUtils.replaceOnce((String)requestPath, (String)clientEndpointPath, (String)"");
        String finalPath = SdkHttpUtils.appendUri((String)resolvedUriPath, (String)requestPathWithClientPathRemoved);
        return finalPath;
    }

    private static void addKnownProperties(Endpoint.Builder builder, Map<String, Value> properties) {
        properties.forEach((n, v) -> {
            switch (n) {
                case "authSchemes": {
                    builder.putAttribute(AwsEndpointAttribute.AUTH_SCHEMES, AuthSchemeUtils.createAuthSchemes(v));
                    break;
                }
                default: {
                    LOG.debug(() -> "Ignoring unknown endpoint property: " + n);
                }
            }
        });
    }

    private static void validatePrefixIsHostNameCompliant(String prefix) {
        String[] components;
        for (String component : components = AwsEndpointProviderUtils.splitHostLabelOnDots(prefix)) {
            HostnameValidator.validateHostnameCompliant((String)component, (String)component, (String)"request");
        }
    }

    private static String[] splitHostLabelOnDots(String label) {
        return label.split("\\.");
    }
}

