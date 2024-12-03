/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.eventstream.internal.signer;

import java.nio.ByteBuffer;
import java.time.Clock;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.auth.aws.eventstream.internal.io.SigV4DataFramePublisher;
import software.amazon.awssdk.http.auth.aws.internal.signer.CredentialScope;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4PayloadSigner;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4RequestSigningResult;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class EventStreamV4PayloadSigner
implements V4PayloadSigner {
    private final AwsCredentialsIdentity credentials;
    private final CredentialScope credentialScope;
    private final Clock signingClock;

    public EventStreamV4PayloadSigner(Builder builder) {
        this.credentials = Validate.paramNotNull(builder.credentials, "Credentials");
        this.credentialScope = Validate.paramNotNull(builder.credentialScope, "CredentialScope");
        this.signingClock = Validate.paramNotNull(builder.signingClock, "SigningClock");
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public ContentStreamProvider sign(ContentStreamProvider payload, V4RequestSigningResult requestSigningResult) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Publisher<ByteBuffer> signAsync(Publisher<ByteBuffer> payload, V4RequestSigningResult requestSigningResult) {
        return SigV4DataFramePublisher.builder().publisher(payload).credentials(this.credentials).credentialScope(this.credentialScope).signature(requestSigningResult.getSignature()).signingClock(this.signingClock).build();
    }

    public static class Builder {
        private AwsCredentialsIdentity credentials;
        private CredentialScope credentialScope;
        private Clock signingClock;

        public Builder credentials(AwsCredentialsIdentity credentials) {
            this.credentials = credentials;
            return this;
        }

        public Builder credentialScope(CredentialScope credentialScope) {
            this.credentialScope = credentialScope;
            return this;
        }

        public Builder signingClock(Clock signingClock) {
            this.signingClock = signingClock;
            return this;
        }

        public EventStreamV4PayloadSigner build() {
            return new EventStreamV4PayloadSigner(this);
        }
    }
}

