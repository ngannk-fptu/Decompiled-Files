/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption
 */
package software.amazon.awssdk.services.s3.auth.scheme.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.services.s3.auth.scheme.S3AuthSchemeParams;
import software.amazon.awssdk.services.s3.auth.scheme.S3AuthSchemeProvider;

@SdkInternalApi
public final class ModeledS3AuthSchemeProvider
implements S3AuthSchemeProvider {
    private static final ModeledS3AuthSchemeProvider DEFAULT = new ModeledS3AuthSchemeProvider();

    private ModeledS3AuthSchemeProvider() {
    }

    public static ModeledS3AuthSchemeProvider create() {
        return DEFAULT;
    }

    @Override
    public List<AuthSchemeOption> resolveAuthScheme(S3AuthSchemeParams params) {
        ArrayList<Object> options = new ArrayList<Object>();
        switch (params.operation()) {
            case "WriteGetObjectResponse": {
                options.add(AuthSchemeOption.builder().schemeId("aws.auth#sigv4").putSignerProperty(AwsV4HttpSigner.SERVICE_SIGNING_NAME, (Object)"s3").putSignerProperty(AwsV4HttpSigner.REGION_NAME, (Object)params.region().id()).putSignerProperty(AwsV4HttpSigner.PAYLOAD_SIGNING_ENABLED, (Object)false).build());
                break;
            }
            default: {
                options.add(AuthSchemeOption.builder().schemeId("aws.auth#sigv4").putSignerProperty(AwsV4HttpSigner.SERVICE_SIGNING_NAME, (Object)"s3").putSignerProperty(AwsV4HttpSigner.REGION_NAME, (Object)params.region().id()).putSignerProperty(AwsV4HttpSigner.DOUBLE_URL_ENCODE, (Object)false).putSignerProperty(AwsV4HttpSigner.NORMALIZE_PATH, (Object)false).putSignerProperty(AwsV4HttpSigner.PAYLOAD_SIGNING_ENABLED, (Object)false).build());
            }
        }
        return Collections.unmodifiableList(options);
    }
}

