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
import software.amazon.awssdk.services.sts.model.AssumeRoleWithWebIdentityRequest;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@ThreadSafe
public final class StsAssumeRoleWithWebIdentityCredentialsProvider
extends StsCredentialsProvider
implements ToCopyableBuilder<Builder, StsAssumeRoleWithWebIdentityCredentialsProvider> {
    private final Supplier<AssumeRoleWithWebIdentityRequest> assumeRoleWithWebIdentityRequest;

    private StsAssumeRoleWithWebIdentityCredentialsProvider(Builder builder) {
        super(builder, "sts-assume-role-with-web-identity-credentials-provider");
        Validate.notNull((Object)builder.assumeRoleWithWebIdentityRequestSupplier, (String)"Assume role with web identity request must not be null.", (Object[])new Object[0]);
        this.assumeRoleWithWebIdentityRequest = builder.assumeRoleWithWebIdentityRequestSupplier;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected AwsSessionCredentials getUpdatedCredentials(StsClient stsClient) {
        AssumeRoleWithWebIdentityRequest request = this.assumeRoleWithWebIdentityRequest.get();
        Validate.notNull((Object)((Object)request), (String)"AssumeRoleWithWebIdentityRequest can't be null", (Object[])new Object[0]);
        return StsAuthUtils.toAwsSessionCredentials(stsClient.assumeRoleWithWebIdentity(request).credentials());
    }

    public String toString() {
        return ToString.create((String)"StsAssumeRoleWithWebIdentityCredentialsProvider");
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @NotThreadSafe
    public static final class Builder
    extends StsCredentialsProvider.BaseBuilder<Builder, StsAssumeRoleWithWebIdentityCredentialsProvider> {
        private Supplier<AssumeRoleWithWebIdentityRequest> assumeRoleWithWebIdentityRequestSupplier;

        private Builder() {
            super((B x$0) -> new StsAssumeRoleWithWebIdentityCredentialsProvider((Builder)x$0));
        }

        public Builder(StsAssumeRoleWithWebIdentityCredentialsProvider provider) {
            super(x$0 -> new StsAssumeRoleWithWebIdentityCredentialsProvider((Builder)x$0), provider);
            this.assumeRoleWithWebIdentityRequestSupplier = provider.assumeRoleWithWebIdentityRequest;
        }

        public Builder refreshRequest(AssumeRoleWithWebIdentityRequest assumeRoleWithWebIdentityRequest) {
            return this.refreshRequest(() -> assumeRoleWithWebIdentityRequest);
        }

        public Builder refreshRequest(Supplier<AssumeRoleWithWebIdentityRequest> assumeRoleWithWebIdentityRequest) {
            this.assumeRoleWithWebIdentityRequestSupplier = assumeRoleWithWebIdentityRequest;
            return this;
        }

        public Builder refreshRequest(Consumer<AssumeRoleWithWebIdentityRequest.Builder> assumeRoleWithWebIdentityRequest) {
            return this.refreshRequest((AssumeRoleWithWebIdentityRequest)((Object)((AssumeRoleWithWebIdentityRequest.Builder)AssumeRoleWithWebIdentityRequest.builder().applyMutation(assumeRoleWithWebIdentityRequest)).build()));
        }

        @Override
        public StsAssumeRoleWithWebIdentityCredentialsProvider build() {
            return (StsAssumeRoleWithWebIdentityCredentialsProvider)super.build();
        }
    }
}

