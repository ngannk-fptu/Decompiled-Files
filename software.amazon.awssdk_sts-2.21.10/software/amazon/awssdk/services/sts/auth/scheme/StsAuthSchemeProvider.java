/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeProvider
 */
package software.amazon.awssdk.services.sts.auth.scheme;

import java.util.List;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeProvider;
import software.amazon.awssdk.services.sts.auth.scheme.StsAuthSchemeParams;
import software.amazon.awssdk.services.sts.auth.scheme.internal.DefaultStsAuthSchemeProvider;

@SdkPublicApi
public interface StsAuthSchemeProvider
extends AuthSchemeProvider {
    public List<AuthSchemeOption> resolveAuthScheme(StsAuthSchemeParams var1);

    default public List<AuthSchemeOption> resolveAuthScheme(Consumer<StsAuthSchemeParams.Builder> consumer) {
        StsAuthSchemeParams.Builder builder = StsAuthSchemeParams.builder();
        consumer.accept(builder);
        return this.resolveAuthScheme(builder.build());
    }

    public static StsAuthSchemeProvider defaultProvider() {
        return DefaultStsAuthSchemeProvider.create();
    }
}

