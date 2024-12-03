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
import software.amazon.awssdk.services.sts.model.GetSessionTokenRequest;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@ThreadSafe
public class StsGetSessionTokenCredentialsProvider
extends StsCredentialsProvider
implements ToCopyableBuilder<Builder, StsGetSessionTokenCredentialsProvider> {
    private final GetSessionTokenRequest getSessionTokenRequest;

    private StsGetSessionTokenCredentialsProvider(Builder builder) {
        super(builder, "sts-get-token-credentials-provider");
        Validate.notNull((Object)((Object)builder.getSessionTokenRequest), (String)"Get session token request must not be null.", (Object[])new Object[0]);
        this.getSessionTokenRequest = builder.getSessionTokenRequest;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected AwsSessionCredentials getUpdatedCredentials(StsClient stsClient) {
        return StsAuthUtils.toAwsSessionCredentials(stsClient.getSessionToken(this.getSessionTokenRequest).credentials());
    }

    public String toString() {
        return ToString.create((String)"StsGetSessionTokenCredentialsProvider");
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @NotThreadSafe
    public static final class Builder
    extends StsCredentialsProvider.BaseBuilder<Builder, StsGetSessionTokenCredentialsProvider> {
        private GetSessionTokenRequest getSessionTokenRequest = (GetSessionTokenRequest)((Object)GetSessionTokenRequest.builder().build());

        private Builder() {
            super((B x$0) -> new StsGetSessionTokenCredentialsProvider((Builder)x$0));
        }

        public Builder(StsGetSessionTokenCredentialsProvider provider) {
            super(x$0 -> new StsGetSessionTokenCredentialsProvider((Builder)x$0), provider);
            this.getSessionTokenRequest = provider.getSessionTokenRequest;
        }

        public Builder refreshRequest(GetSessionTokenRequest getSessionTokenRequest) {
            this.getSessionTokenRequest = getSessionTokenRequest;
            return this;
        }

        public Builder refreshRequest(Consumer<GetSessionTokenRequest.Builder> getFederationTokenRequest) {
            return this.refreshRequest((GetSessionTokenRequest)((Object)((GetSessionTokenRequest.Builder)GetSessionTokenRequest.builder().applyMutation(getFederationTokenRequest)).build()));
        }

        @Override
        public StsGetSessionTokenCredentialsProvider build() {
            return (StsGetSessionTokenCredentialsProvider)super.build();
        }
    }
}

