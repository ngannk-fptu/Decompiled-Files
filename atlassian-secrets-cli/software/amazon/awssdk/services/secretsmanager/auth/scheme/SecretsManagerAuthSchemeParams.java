/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.auth.scheme;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.auth.scheme.internal.DefaultSecretsManagerAuthSchemeParams;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public interface SecretsManagerAuthSchemeParams
extends ToCopyableBuilder<Builder, SecretsManagerAuthSchemeParams> {
    public static Builder builder() {
        return DefaultSecretsManagerAuthSchemeParams.builder();
    }

    public String operation();

    public Region region();

    @Override
    public Builder toBuilder();

    public static interface Builder
    extends CopyableBuilder<Builder, SecretsManagerAuthSchemeParams> {
        public Builder operation(String var1);

        public Builder region(Region var1);

        @Override
        public SecretsManagerAuthSchemeParams build();
    }
}

