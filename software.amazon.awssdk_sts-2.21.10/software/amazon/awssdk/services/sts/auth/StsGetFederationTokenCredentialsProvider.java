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
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsCredentialsProvider;
import software.amazon.awssdk.services.sts.internal.StsAuthUtils;
import software.amazon.awssdk.services.sts.model.GetFederationTokenRequest;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@ThreadSafe
public class StsGetFederationTokenCredentialsProvider
extends StsCredentialsProvider
implements ToCopyableBuilder<Builder, StsGetFederationTokenCredentialsProvider> {
    private final GetFederationTokenRequest getFederationTokenRequest;

    private StsGetFederationTokenCredentialsProvider(Builder builder) {
        super(builder, "sts-get-federation-token-credentials-provider");
        Validate.notNull((Object)((Object)builder.getFederationTokenRequest), (String)"Get session token request must not be null.", (Object[])new Object[0]);
        this.getFederationTokenRequest = builder.getFederationTokenRequest;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected AwsSessionCredentials getUpdatedCredentials(StsClient stsClient) {
        return StsAuthUtils.toAwsSessionCredentials(stsClient.getFederationToken(this.getFederationTokenRequest).credentials());
    }

    public String toString() {
        return ToString.create((String)"StsGetFederationTokenCredentialsProvider");
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @NotThreadSafe
    public static final class Builder
    extends StsCredentialsProvider.BaseBuilder<Builder, StsGetFederationTokenCredentialsProvider> {
        private GetFederationTokenRequest getFederationTokenRequest;

        private Builder() {
            super((B x$0) -> new StsGetFederationTokenCredentialsProvider((Builder)x$0));
        }

        public Builder(StsGetFederationTokenCredentialsProvider provider) {
            super(x$0 -> new StsGetFederationTokenCredentialsProvider((Builder)x$0), provider);
            this.getFederationTokenRequest = provider.getFederationTokenRequest;
        }

        public Builder refreshRequest(GetFederationTokenRequest getFederationTokenRequest) {
            this.getFederationTokenRequest = getFederationTokenRequest;
            return this;
        }

        public Builder refreshRequest(Consumer<GetFederationTokenRequest.Builder> getFederationTokenRequest) {
            return this.refreshRequest((GetFederationTokenRequest)((Object)((GetFederationTokenRequest.Builder)GetFederationTokenRequest.builder().applyMutation(getFederationTokenRequest)).build()));
        }

        @Override
        public StsGetFederationTokenCredentialsProvider build() {
            return (StsGetFederationTokenCredentialsProvider)super.build();
        }
    }
}

