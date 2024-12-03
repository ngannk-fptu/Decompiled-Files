/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeProvider
 */
package software.amazon.awssdk.services.s3.auth.scheme;

import java.util.List;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeProvider;
import software.amazon.awssdk.services.s3.auth.scheme.S3AuthSchemeParams;
import software.amazon.awssdk.services.s3.auth.scheme.internal.DefaultS3AuthSchemeProvider;

@SdkPublicApi
public interface S3AuthSchemeProvider
extends AuthSchemeProvider {
    public List<AuthSchemeOption> resolveAuthScheme(S3AuthSchemeParams var1);

    default public List<AuthSchemeOption> resolveAuthScheme(Consumer<S3AuthSchemeParams.Builder> consumer) {
        S3AuthSchemeParams.Builder builder = S3AuthSchemeParams.builder();
        consumer.accept(builder);
        return this.resolveAuthScheme(builder.build());
    }

    public static S3AuthSchemeProvider defaultProvider() {
        return DefaultS3AuthSchemeProvider.create();
    }
}

