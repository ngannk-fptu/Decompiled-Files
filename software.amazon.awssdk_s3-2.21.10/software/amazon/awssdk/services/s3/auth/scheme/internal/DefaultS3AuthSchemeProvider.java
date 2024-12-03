/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.endpoints.AwsEndpointAttribute
 *  software.amazon.awssdk.awscore.endpoints.authscheme.EndpointAuthScheme
 *  software.amazon.awssdk.awscore.endpoints.authscheme.SigV4AuthScheme
 *  software.amazon.awssdk.awscore.endpoints.authscheme.SigV4aAuthScheme
 *  software.amazon.awssdk.endpoints.Endpoint
 *  software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner
 *  software.amazon.awssdk.http.auth.aws.signer.AwsV4aHttpSigner
 *  software.amazon.awssdk.http.auth.aws.signer.RegionSet
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.s3.auth.scheme.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.endpoints.AwsEndpointAttribute;
import software.amazon.awssdk.awscore.endpoints.authscheme.EndpointAuthScheme;
import software.amazon.awssdk.awscore.endpoints.authscheme.SigV4AuthScheme;
import software.amazon.awssdk.awscore.endpoints.authscheme.SigV4aAuthScheme;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4aHttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.RegionSet;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.services.s3.auth.scheme.S3AuthSchemeParams;
import software.amazon.awssdk.services.s3.auth.scheme.S3AuthSchemeProvider;
import software.amazon.awssdk.services.s3.auth.scheme.internal.ModeledS3AuthSchemeProvider;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointParams;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointProvider;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class DefaultS3AuthSchemeProvider
implements S3AuthSchemeProvider {
    private static final DefaultS3AuthSchemeProvider DEFAULT = new DefaultS3AuthSchemeProvider();
    private static final S3AuthSchemeProvider MODELED_RESOLVER = ModeledS3AuthSchemeProvider.create();
    private static final S3EndpointProvider DELEGATE = S3EndpointProvider.defaultProvider();

    private DefaultS3AuthSchemeProvider() {
    }

    public static S3AuthSchemeProvider create() {
        return DEFAULT;
    }

    @Override
    public List<AuthSchemeOption> resolveAuthScheme(S3AuthSchemeParams params) {
        S3EndpointParams endpointParameters = S3EndpointParams.builder().bucket(params.bucket()).region(params.region()).useFips(params.useFips()).useDualStack(params.useDualStack()).endpoint(params.endpoint()).forcePathStyle(params.forcePathStyle()).accelerate(params.accelerate()).useGlobalEndpoint(params.useGlobalEndpoint()).useObjectLambdaEndpoint(params.useObjectLambdaEndpoint()).disableAccessPoints(params.disableAccessPoints()).disableMultiRegionAccessPoints(params.disableMultiRegionAccessPoints()).useArnRegion(params.useArnRegion()).build();
        Endpoint endpoint = (Endpoint)CompletableFutureUtils.joinLikeSync(DELEGATE.resolveEndpoint(endpointParameters));
        List authSchemes = (List)endpoint.attribute(AwsEndpointAttribute.AUTH_SCHEMES);
        if (authSchemes == null) {
            return MODELED_RESOLVER.resolveAuthScheme(params);
        }
        ArrayList<Object> options = new ArrayList<Object>();
        block8: for (EndpointAuthScheme authScheme : authSchemes) {
            String name;
            switch (name = authScheme.name()) {
                case "sigv4": {
                    SigV4AuthScheme sigv4AuthScheme = (SigV4AuthScheme)Validate.isInstanceOf(SigV4AuthScheme.class, (Object)authScheme, (String)"Expecting auth scheme of class SigV4AuthScheme, got instead object of class %s", (Object[])new Object[]{authScheme.getClass().getName()});
                    options.add(AuthSchemeOption.builder().schemeId("aws.auth#sigv4").putSignerProperty(AwsV4HttpSigner.SERVICE_SIGNING_NAME, (Object)sigv4AuthScheme.signingName()).putSignerProperty(AwsV4HttpSigner.REGION_NAME, (Object)sigv4AuthScheme.signingRegion()).putSignerProperty(AwsV4HttpSigner.DOUBLE_URL_ENCODE, (Object)(!sigv4AuthScheme.disableDoubleEncoding() ? 1 : 0)).build());
                    continue block8;
                }
                case "sigv4a": {
                    SigV4aAuthScheme sigv4aAuthScheme = (SigV4aAuthScheme)Validate.isInstanceOf(SigV4aAuthScheme.class, (Object)authScheme, (String)"Expecting auth scheme of class SigV4AuthScheme, got instead object of class %s", (Object[])new Object[]{authScheme.getClass().getName()});
                    RegionSet regionSet = RegionSet.create((Collection)sigv4aAuthScheme.signingRegionSet());
                    options.add(AuthSchemeOption.builder().schemeId("aws.auth#sigv4a").putSignerProperty(AwsV4aHttpSigner.SERVICE_SIGNING_NAME, (Object)sigv4aAuthScheme.signingName()).putSignerProperty(AwsV4aHttpSigner.REGION_SET, (Object)regionSet).putSignerProperty(AwsV4aHttpSigner.DOUBLE_URL_ENCODE, (Object)(!sigv4aAuthScheme.disableDoubleEncoding() ? 1 : 0)).build());
                    continue block8;
                }
            }
            throw new IllegalArgumentException("Unknown auth scheme: " + name);
        }
        return Collections.unmodifiableList(options);
    }
}

