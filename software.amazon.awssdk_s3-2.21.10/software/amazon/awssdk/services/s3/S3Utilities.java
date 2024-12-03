/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.awscore.AwsExecutionAttribute
 *  software.amazon.awssdk.awscore.client.config.AwsClientOption
 *  software.amazon.awssdk.awscore.defaultsmode.DefaultsMode
 *  software.amazon.awssdk.awscore.endpoint.DefaultServiceEndpointBuilder
 *  software.amazon.awssdk.awscore.endpoint.DualstackEnabledProvider
 *  software.amazon.awssdk.awscore.endpoint.FipsEnabledProvider
 *  software.amazon.awssdk.awscore.internal.defaultsmode.DefaultsModeConfiguration
 *  software.amazon.awssdk.core.ClientType
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.client.config.ClientOption
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.core.client.config.SdkClientOption
 *  software.amazon.awssdk.core.exception.SdkException
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptorChain
 *  software.amazon.awssdk.core.interceptor.InterceptorContext
 *  software.amazon.awssdk.core.interceptor.SdkExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpFullRequest$Builder
 *  software.amazon.awssdk.http.SdkHttpMethod
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.profiles.ProfileFileSupplier
 *  software.amazon.awssdk.protocols.core.OperationInfo
 *  software.amazon.awssdk.protocols.core.PathMarshaller
 *  software.amazon.awssdk.protocols.core.ProtocolUtils
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.regions.ServiceMetadataAdvancedOption
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.AttributeMap$Builder
 *  software.amazon.awssdk.utils.AttributeMap$Key
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.http.SdkHttpUtils
 */
package software.amazon.awssdk.services.s3;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.AwsExecutionAttribute;
import software.amazon.awssdk.awscore.client.config.AwsClientOption;
import software.amazon.awssdk.awscore.defaultsmode.DefaultsMode;
import software.amazon.awssdk.awscore.endpoint.DefaultServiceEndpointBuilder;
import software.amazon.awssdk.awscore.endpoint.DualstackEnabledProvider;
import software.amazon.awssdk.awscore.endpoint.FipsEnabledProvider;
import software.amazon.awssdk.awscore.internal.defaultsmode.DefaultsModeConfiguration;
import software.amazon.awssdk.core.ClientType;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptorChain;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSupplier;
import software.amazon.awssdk.protocols.core.OperationInfo;
import software.amazon.awssdk.protocols.core.PathMarshaller;
import software.amazon.awssdk.protocols.core.ProtocolUtils;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.ServiceMetadataAdvancedOption;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.S3Uri;
import software.amazon.awssdk.services.s3.endpoints.S3ClientContextParams;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointProvider;
import software.amazon.awssdk.services.s3.endpoints.internal.S3RequestSetEndpointInterceptor;
import software.amazon.awssdk.services.s3.endpoints.internal.S3ResolveEndpointInterceptor;
import software.amazon.awssdk.services.s3.internal.endpoints.UseGlobalEndpointResolver;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@Immutable
@SdkPublicApi
public final class S3Utilities {
    private static final String SERVICE_NAME = "s3";
    private static final Pattern ENDPOINT_PATTERN = Pattern.compile("^(.+\\.)?s3[.-]([a-z0-9-]+)\\.");
    private final Region region;
    private final URI endpoint;
    private final S3Configuration s3Configuration;
    private final Supplier<ProfileFile> profileFile;
    private final String profileName;
    private final boolean fipsEnabled;
    private final ExecutionInterceptorChain interceptorChain;
    private final UseGlobalEndpointResolver useGlobalEndpointResolver;

    private S3Utilities(Builder builder) {
        this.region = (Region)Validate.paramNotNull((Object)builder.region, (String)"Region");
        this.endpoint = builder.endpoint;
        this.profileFile = Optional.ofNullable(builder.profileFile).orElse(ProfileFile::defaultProfileFile);
        this.profileName = builder.profileName;
        this.s3Configuration = builder.s3Configuration == null ? (S3Configuration)S3Configuration.builder().dualstackEnabled(builder.dualstackEnabled).build() : (S3Configuration)((S3Configuration.Builder)builder.s3Configuration.toBuilder().applyMutation(b -> this.resolveDualstackSetting((S3Configuration.Builder)b, builder))).build();
        this.fipsEnabled = builder.fipsEnabled != null ? builder.fipsEnabled.booleanValue() : FipsEnabledProvider.builder().profileFile(this.profileFile).profileName(this.profileName).build().isFipsEnabled().orElse(false).booleanValue();
        this.interceptorChain = this.createEndpointInterceptorChain();
        this.useGlobalEndpointResolver = this.createUseGlobalEndpointResolver();
    }

    private void resolveDualstackSetting(S3Configuration.Builder s3ConfigBuilder, Builder s3UtiltiesBuilder) {
        Validate.validState((s3ConfigBuilder.dualstackEnabled() == null || s3UtiltiesBuilder.dualstackEnabled == null ? 1 : 0) != 0, (String)"Only one of S3Configuration.Builder's dualstackEnabled or S3Utilities.Builder's dualstackEnabled should be set.", (Object[])new Object[0]);
        if (s3ConfigBuilder.dualstackEnabled() != null) {
            return;
        }
        if (s3UtiltiesBuilder.dualstackEnabled != null) {
            s3ConfigBuilder.dualstackEnabled(s3UtiltiesBuilder.dualstackEnabled);
            return;
        }
        s3ConfigBuilder.dualstackEnabled(DualstackEnabledProvider.builder().profileFile(this.profileFile).profileName(this.profileName).build().isDualstackEnabled().orElse(false));
    }

    public static Builder builder() {
        return new Builder();
    }

    @SdkInternalApi
    static S3Utilities create(SdkClientConfiguration clientConfiguration) {
        Builder builder = S3Utilities.builder().region((Region)clientConfiguration.option((ClientOption)AwsClientOption.AWS_REGION)).s3Configuration((S3Configuration)clientConfiguration.option((ClientOption)SdkClientOption.SERVICE_CONFIGURATION)).profileFile((Supplier)clientConfiguration.option((ClientOption)SdkClientOption.PROFILE_FILE_SUPPLIER)).profileName((String)clientConfiguration.option((ClientOption)SdkClientOption.PROFILE_NAME));
        if (Boolean.TRUE.equals(clientConfiguration.option((ClientOption)SdkClientOption.ENDPOINT_OVERRIDDEN))) {
            builder.endpoint((URI)clientConfiguration.option((ClientOption)SdkClientOption.ENDPOINT));
        }
        return builder.build();
    }

    public URL getUrl(Consumer<GetUrlRequest.Builder> getUrlRequest) {
        return this.getUrl((GetUrlRequest)((GetUrlRequest.Builder)GetUrlRequest.builder().applyMutation(getUrlRequest)).build());
    }

    public URL getUrl(GetUrlRequest getUrlRequest) {
        Region resolvedRegion = this.resolveRegionForGetUrl(getUrlRequest);
        URI endpointOverride = this.getEndpointOverride(getUrlRequest);
        URI resolvedEndpoint = this.resolveEndpoint(endpointOverride, resolvedRegion);
        SdkHttpFullRequest marshalledRequest = this.createMarshalledRequest(getUrlRequest, resolvedEndpoint);
        GetObjectRequest getObjectRequest = (GetObjectRequest)((Object)GetObjectRequest.builder().bucket(getUrlRequest.bucket()).key(getUrlRequest.key()).versionId(getUrlRequest.versionId()).build());
        InterceptorContext interceptorContext = InterceptorContext.builder().httpRequest((SdkHttpRequest)marshalledRequest).request((SdkRequest)getObjectRequest).build();
        ExecutionAttributes executionAttributes = this.createExecutionAttributes(resolvedEndpoint, resolvedRegion, endpointOverride != null);
        SdkHttpRequest modifiedRequest = this.runInterceptors(interceptorContext, executionAttributes).httpRequest();
        try {
            return modifiedRequest.getUri().toURL();
        }
        catch (MalformedURLException exception) {
            throw SdkException.create((String)("Generated URI is malformed: " + modifiedRequest.getUri()), (Throwable)exception);
        }
    }

    public S3Uri parseUri(URI uri) {
        this.validateUri(uri);
        if (SERVICE_NAME.equalsIgnoreCase(uri.getScheme())) {
            return this.parseAwsCliStyleUri(uri);
        }
        return this.parseStandardUri(uri);
    }

    private S3Uri parseStandardUri(URI uri) {
        if (uri.getHost() == null) {
            throw new IllegalArgumentException("Invalid S3 URI: no hostname: " + uri);
        }
        Matcher matcher = ENDPOINT_PATTERN.matcher(uri.getHost());
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid S3 URI: hostname does not appear to be a valid S3 endpoint: " + uri);
        }
        S3Uri.Builder builder = S3Uri.builder().uri(uri);
        this.addRegionIfNeeded(builder, matcher.group(2));
        this.addQueryParamsIfNeeded(builder, uri);
        String prefix = matcher.group(1);
        if (StringUtils.isEmpty((CharSequence)prefix)) {
            return this.parsePathStyleUri(builder, uri);
        }
        return this.parseVirtualHostedStyleUri(builder, uri, matcher);
    }

    private S3Uri.Builder addRegionIfNeeded(S3Uri.Builder builder, String region) {
        if (!"amazonaws".equals(region)) {
            return builder.region(Region.of((String)region));
        }
        return builder;
    }

    private S3Uri.Builder addQueryParamsIfNeeded(S3Uri.Builder builder, URI uri) {
        if (uri.getQuery() != null) {
            return builder.queryParams(SdkHttpUtils.uriParams((URI)uri));
        }
        return builder;
    }

    private S3Uri parsePathStyleUri(S3Uri.Builder builder, URI uri) {
        String bucket = null;
        String key = null;
        String path = uri.getPath();
        if (!StringUtils.isEmpty((CharSequence)path) && !"/".equals(path)) {
            int index = path.indexOf(47, 1);
            if (index == -1) {
                bucket = path.substring(1);
            } else {
                bucket = path.substring(1, index);
                if (index != path.length() - 1) {
                    key = path.substring(index + 1);
                }
            }
        }
        return builder.key(key).bucket(bucket).isPathStyle(true).build();
    }

    private S3Uri parseVirtualHostedStyleUri(S3Uri.Builder builder, URI uri, Matcher matcher) {
        String key = null;
        String path = uri.getPath();
        String prefix = matcher.group(1);
        String bucket = prefix.substring(0, prefix.length() - 1);
        if (!StringUtils.isEmpty((CharSequence)path) && !"/".equals(path)) {
            key = path.substring(1);
        }
        return builder.key(key).bucket(bucket).build();
    }

    private S3Uri parseAwsCliStyleUri(URI uri) {
        String key = null;
        String bucket = uri.getAuthority();
        Region region = null;
        boolean isPathStyle = false;
        HashMap<String, List<String>> queryParams = new HashMap<String, List<String>>();
        String path = uri.getPath();
        if (bucket == null) {
            throw new IllegalArgumentException("Invalid S3 URI: bucket not included: " + uri);
        }
        if (path.length() > 1) {
            key = path.substring(1);
        }
        return S3Uri.builder().uri(uri).bucket(bucket).key(key).region(region).isPathStyle(isPathStyle).queryParams(queryParams).build();
    }

    private void validateUri(URI uri) {
        Validate.paramNotNull((Object)uri, (String)"uri");
        if (uri.toString().contains(".s3-accesspoint")) {
            throw new IllegalArgumentException("AccessPoints URI parsing is not supported: " + uri);
        }
        if (uri.toString().contains(".s3-outposts")) {
            throw new IllegalArgumentException("Outposts URI parsing is not supported: " + uri);
        }
    }

    private Region resolveRegionForGetUrl(GetUrlRequest getUrlRequest) {
        if (getUrlRequest.region() == null && this.region == null) {
            throw new IllegalArgumentException("Region should be provided either in GetUrlRequest object or S3Utilities object");
        }
        return getUrlRequest.region() != null ? getUrlRequest.region() : this.region;
    }

    private URI resolveEndpoint(URI overrideEndpoint, Region region) {
        return overrideEndpoint != null ? overrideEndpoint : new DefaultServiceEndpointBuilder(SERVICE_NAME, "https").withRegion(region).withProfileFile(this.profileFile).withProfileName(this.profileName).withDualstackEnabled(Boolean.valueOf(this.s3Configuration.dualstackEnabled())).withFipsEnabled(Boolean.valueOf(this.fipsEnabled)).getServiceEndpoint();
    }

    private URI getEndpointOverride(GetUrlRequest request) {
        URI requestOverrideEndpoint = request.endpoint();
        return requestOverrideEndpoint != null ? requestOverrideEndpoint : this.endpoint;
    }

    private SdkHttpFullRequest createMarshalledRequest(GetUrlRequest getUrlRequest, URI endpoint) {
        OperationInfo operationInfo = OperationInfo.builder().requestUri("/{Key+}").httpMethod(SdkHttpMethod.HEAD).build();
        SdkHttpFullRequest.Builder builder = ProtocolUtils.createSdkHttpRequest((OperationInfo)operationInfo, (URI)endpoint);
        builder.encodedPath(PathMarshaller.NON_GREEDY.marshall(builder.encodedPath(), "Bucket", getUrlRequest.bucket()));
        builder.encodedPath(PathMarshaller.GREEDY.marshall(builder.encodedPath(), "Key", getUrlRequest.key()));
        if (getUrlRequest.versionId() != null) {
            builder.appendRawQueryParameter("versionId", getUrlRequest.versionId());
        }
        return builder.build();
    }

    private ExecutionAttributes createExecutionAttributes(URI clientEndpoint, Region region, boolean isEndpointOverridden) {
        ExecutionAttributes executionAttributes = new ExecutionAttributes().putAttribute(AwsExecutionAttribute.AWS_REGION, (Object)region).putAttribute(SdkExecutionAttribute.CLIENT_TYPE, (Object)ClientType.SYNC).putAttribute(SdkExecutionAttribute.SERVICE_NAME, (Object)SERVICE_NAME).putAttribute(SdkExecutionAttribute.OPERATION_NAME, (Object)"GetObject").putAttribute(SdkExecutionAttribute.SERVICE_CONFIG, (Object)this.s3Configuration).putAttribute(AwsExecutionAttribute.FIPS_ENDPOINT_ENABLED, (Object)this.fipsEnabled).putAttribute(AwsExecutionAttribute.DUALSTACK_ENDPOINT_ENABLED, (Object)this.s3Configuration.dualstackEnabled()).putAttribute(SdkInternalExecutionAttribute.ENDPOINT_PROVIDER, (Object)S3EndpointProvider.defaultProvider()).putAttribute(SdkInternalExecutionAttribute.CLIENT_CONTEXT_PARAMS, (Object)this.createClientContextParams()).putAttribute(SdkExecutionAttribute.CLIENT_ENDPOINT, (Object)clientEndpoint).putAttribute(AwsExecutionAttribute.USE_GLOBAL_ENDPOINT, (Object)this.useGlobalEndpointResolver.resolve(region));
        if (isEndpointOverridden) {
            executionAttributes.putAttribute(SdkExecutionAttribute.ENDPOINT_OVERRIDDEN, (Object)true);
        }
        return executionAttributes;
    }

    private AttributeMap createClientContextParams() {
        AttributeMap.Builder params = AttributeMap.builder();
        params.put(S3ClientContextParams.USE_ARN_REGION, (Object)this.s3Configuration.useArnRegionEnabled());
        params.put(S3ClientContextParams.DISABLE_MULTI_REGION_ACCESS_POINTS, (Object)(!this.s3Configuration.multiRegionEnabled() ? 1 : 0));
        params.put(S3ClientContextParams.FORCE_PATH_STYLE, (Object)this.s3Configuration.pathStyleAccessEnabled());
        params.put(S3ClientContextParams.ACCELERATE, (Object)this.s3Configuration.accelerateModeEnabled());
        return params.build();
    }

    private InterceptorContext runInterceptors(InterceptorContext context, ExecutionAttributes executionAttributes) {
        context = this.interceptorChain.modifyRequest(context, executionAttributes);
        return this.interceptorChain.modifyHttpRequestAndHttpContent(context, executionAttributes);
    }

    private ExecutionInterceptorChain createEndpointInterceptorChain() {
        ArrayList<Object> interceptors = new ArrayList<Object>();
        interceptors.add(new S3ResolveEndpointInterceptor());
        interceptors.add(new S3RequestSetEndpointInterceptor());
        return new ExecutionInterceptorChain(interceptors);
    }

    private UseGlobalEndpointResolver createUseGlobalEndpointResolver() {
        String standardOption = (String)DefaultsModeConfiguration.defaultConfig((DefaultsMode)DefaultsMode.LEGACY).get((AttributeMap.Key)ServiceMetadataAdvancedOption.DEFAULT_S3_US_EAST_1_REGIONAL_ENDPOINT);
        SdkClientConfiguration config = SdkClientConfiguration.builder().option((ClientOption)ServiceMetadataAdvancedOption.DEFAULT_S3_US_EAST_1_REGIONAL_ENDPOINT, (Object)standardOption).option((ClientOption)SdkClientOption.PROFILE_FILE_SUPPLIER, this.profileFile).option((ClientOption)SdkClientOption.PROFILE_NAME, (Object)this.profileName).build();
        return new UseGlobalEndpointResolver(config);
    }

    public static final class Builder {
        private Region region;
        private URI endpoint;
        private S3Configuration s3Configuration;
        private Supplier<ProfileFile> profileFile;
        private String profileName;
        private Boolean dualstackEnabled;
        private Boolean fipsEnabled;

        private Builder() {
        }

        public Builder region(Region region) {
            this.region = region;
            return this;
        }

        public Builder endpoint(URI endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder dualstackEnabled(Boolean dualstackEnabled) {
            this.dualstackEnabled = dualstackEnabled;
            return this;
        }

        public Builder fipsEnabled(Boolean fipsEnabled) {
            this.fipsEnabled = fipsEnabled;
            return this;
        }

        public Builder s3Configuration(S3Configuration s3Configuration) {
            this.s3Configuration = s3Configuration;
            return this;
        }

        private Builder profileFile(ProfileFile profileFile) {
            return this.profileFile((Supplier<ProfileFile>)Optional.ofNullable(profileFile).map(ProfileFileSupplier::fixedProfileFile).orElse(null));
        }

        private Builder profileFile(Supplier<ProfileFile> profileFileSupplier) {
            this.profileFile = profileFileSupplier;
            return this;
        }

        private Builder profileName(String profileName) {
            this.profileName = profileName;
            return this;
        }

        public S3Utilities build() {
            return new S3Utilities(this);
        }
    }
}

