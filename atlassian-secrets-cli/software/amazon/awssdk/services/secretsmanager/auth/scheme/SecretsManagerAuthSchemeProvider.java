/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.auth.scheme;

import java.util.List;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeProvider;
import software.amazon.awssdk.services.secretsmanager.auth.scheme.SecretsManagerAuthSchemeParams;
import software.amazon.awssdk.services.secretsmanager.auth.scheme.internal.DefaultSecretsManagerAuthSchemeProvider;

@SdkPublicApi
public interface SecretsManagerAuthSchemeProvider
extends AuthSchemeProvider {
    public List<AuthSchemeOption> resolveAuthScheme(SecretsManagerAuthSchemeParams var1);

    default public List<AuthSchemeOption> resolveAuthScheme(Consumer<SecretsManagerAuthSchemeParams.Builder> consumer) {
        SecretsManagerAuthSchemeParams.Builder builder = SecretsManagerAuthSchemeParams.builder();
        consumer.accept(builder);
        return this.resolveAuthScheme(builder.build());
    }

    public static SecretsManagerAuthSchemeProvider defaultProvider() {
        return DefaultSecretsManagerAuthSchemeProvider.create();
    }
}

