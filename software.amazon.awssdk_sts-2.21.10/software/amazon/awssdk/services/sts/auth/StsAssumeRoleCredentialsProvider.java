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
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@ThreadSafe
public final class StsAssumeRoleCredentialsProvider
extends StsCredentialsProvider
implements ToCopyableBuilder<Builder, StsAssumeRoleCredentialsProvider> {
    private Supplier<AssumeRoleRequest> assumeRoleRequestSupplier;

    private StsAssumeRoleCredentialsProvider(Builder builder) {
        super(builder, "sts-assume-role-credentials-provider");
        Validate.notNull((Object)builder.assumeRoleRequestSupplier, (String)"Assume role request must not be null.", (Object[])new Object[0]);
        this.assumeRoleRequestSupplier = builder.assumeRoleRequestSupplier;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected AwsSessionCredentials getUpdatedCredentials(StsClient stsClient) {
        AssumeRoleRequest assumeRoleRequest = this.assumeRoleRequestSupplier.get();
        Validate.notNull((Object)((Object)assumeRoleRequest), (String)"Assume role request must not be null.", (Object[])new Object[0]);
        return StsAuthUtils.toAwsSessionCredentials(stsClient.assumeRole(assumeRoleRequest).credentials());
    }

    public String toString() {
        return ToString.create((String)"StsAssumeRoleCredentialsProvider");
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @NotThreadSafe
    public static final class Builder
    extends StsCredentialsProvider.BaseBuilder<Builder, StsAssumeRoleCredentialsProvider> {
        private Supplier<AssumeRoleRequest> assumeRoleRequestSupplier;

        private Builder() {
            super((B x$0) -> new StsAssumeRoleCredentialsProvider((Builder)x$0));
        }

        private Builder(StsAssumeRoleCredentialsProvider provider) {
            super((B x$0) -> new StsAssumeRoleCredentialsProvider((Builder)x$0), provider);
            this.assumeRoleRequestSupplier = provider.assumeRoleRequestSupplier;
        }

        public Builder refreshRequest(AssumeRoleRequest assumeRoleRequest) {
            return this.refreshRequest(() -> assumeRoleRequest);
        }

        public Builder refreshRequest(Supplier<AssumeRoleRequest> assumeRoleRequestSupplier) {
            this.assumeRoleRequestSupplier = assumeRoleRequestSupplier;
            return this;
        }

        public Builder refreshRequest(Consumer<AssumeRoleRequest.Builder> assumeRoleRequest) {
            return this.refreshRequest((AssumeRoleRequest)((Object)((AssumeRoleRequest.Builder)AssumeRoleRequest.builder().applyMutation(assumeRoleRequest)).build()));
        }

        @Override
        public StsAssumeRoleCredentialsProvider build() {
            return (StsAssumeRoleCredentialsProvider)super.build();
        }
    }
}

