/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.NotThreadSafe
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.auth.credentials.AwsSessionCredentials
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.sts.auth;

import java.util.function.Consumer;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsCredentialsProvider;
import software.amazon.awssdk.services.sts.internal.StsAuthUtils;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithSamlRequest;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@ThreadSafe
public final class StsAssumeRoleWithSamlCredentialsProvider
extends StsCredentialsProvider
implements ToCopyableBuilder<Builder, StsAssumeRoleWithSamlCredentialsProvider> {
    private final Supplier<AssumeRoleWithSamlRequest> assumeRoleWithSamlRequestSupplier;

    private StsAssumeRoleWithSamlCredentialsProvider(Builder builder) {
        super(builder, "sts-assume-role-with-saml-credentials-provider");
        Validate.notNull((Object)builder.assumeRoleWithSamlRequestSupplier, (String)"Assume role with SAML request must not be null.", (Object[])new Object[0]);
        this.assumeRoleWithSamlRequestSupplier = builder.assumeRoleWithSamlRequestSupplier;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected AwsSessionCredentials getUpdatedCredentials(StsClient stsClient) {
        AssumeRoleWithSamlRequest assumeRoleWithSamlRequest = this.assumeRoleWithSamlRequestSupplier.get();
        Validate.notNull((Object)((Object)assumeRoleWithSamlRequest), (String)"Assume role with saml request must not be null.", (Object[])new Object[0]);
        return StsAuthUtils.toAwsSessionCredentials(stsClient.assumeRoleWithSAML(assumeRoleWithSamlRequest).credentials());
    }

    public String toString() {
        return ToString.create((String)"StsAssumeRoleWithSamlCredentialsProvider");
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @NotThreadSafe
    public static final class Builder
    extends StsCredentialsProvider.BaseBuilder<Builder, StsAssumeRoleWithSamlCredentialsProvider> {
        private Supplier<AssumeRoleWithSamlRequest> assumeRoleWithSamlRequestSupplier;

        private Builder() {
            super((B x$0) -> new StsAssumeRoleWithSamlCredentialsProvider((Builder)x$0));
        }

        public Builder(StsAssumeRoleWithSamlCredentialsProvider provider) {
            super(x$0 -> new StsAssumeRoleWithSamlCredentialsProvider((Builder)x$0), provider);
            this.assumeRoleWithSamlRequestSupplier = provider.assumeRoleWithSamlRequestSupplier;
        }

        public Builder refreshRequest(AssumeRoleWithSamlRequest assumeRoleWithSamlRequest) {
            return this.refreshRequest(() -> assumeRoleWithSamlRequest);
        }

        public Builder refreshRequest(Supplier<AssumeRoleWithSamlRequest> assumeRoleWithSamlRequestSupplier) {
            this.assumeRoleWithSamlRequestSupplier = assumeRoleWithSamlRequestSupplier;
            return this;
        }

        public Builder refreshRequest(Consumer<AssumeRoleWithSamlRequest.Builder> assumeRoleWithSamlRequest) {
            return this.refreshRequest((AssumeRoleWithSamlRequest)((Object)((AssumeRoleWithSamlRequest.Builder)AssumeRoleWithSamlRequest.builder().applyMutation(assumeRoleWithSamlRequest)).build()));
        }

        @Override
        public StsAssumeRoleWithSamlCredentialsProvider build() {
            return (StsAssumeRoleWithSamlCredentialsProvider)super.build();
        }
    }
}

