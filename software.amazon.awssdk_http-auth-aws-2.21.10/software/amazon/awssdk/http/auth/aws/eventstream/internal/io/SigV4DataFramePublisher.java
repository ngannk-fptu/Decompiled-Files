/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.utils.BinaryUtils
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.internal.MappingSubscriber
 *  software.amazon.eventstream.HeaderValue
 *  software.amazon.eventstream.Message
 */
package software.amazon.awssdk.http.auth.aws.eventstream.internal.io;

import java.nio.ByteBuffer;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.eventstream.internal.io.TrailingDataFramePublisher;
import software.amazon.awssdk.http.auth.aws.internal.signer.CredentialScope;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.SignerUtils;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.internal.MappingSubscriber;
import software.amazon.eventstream.HeaderValue;
import software.amazon.eventstream.Message;

@SdkInternalApi
public final class SigV4DataFramePublisher
implements Publisher<ByteBuffer> {
    private static final Logger LOG = Logger.loggerFor(SigV4DataFramePublisher.class);
    private static final String CHUNK_SIGNATURE = ":chunk-signature";
    private static final int PAYLOAD_TRUNCATE_LENGTH = 32;
    private final Publisher<ByteBuffer> sigv4Publisher;

    private SigV4DataFramePublisher(Builder builder) {
        Validate.paramNotNull((Object)builder.publisher, (String)"Publisher");
        Validate.paramNotNull((Object)builder.credentials, (String)"Credentials");
        Validate.paramNotNull((Object)builder.credentialScope, (String)"CredentialScope");
        Validate.paramNotNull((Object)builder.signature, (String)"Signature");
        Validate.paramNotNull((Object)builder.signingClock, (String)"SigningClock");
        TrailingDataFramePublisher trailingPublisher = new TrailingDataFramePublisher((Publisher<ByteBuffer>)builder.publisher);
        this.sigv4Publisher = subscriber -> {
            MappingSubscriber adaptedSubscriber = MappingSubscriber.create((Subscriber)subscriber, SigV4DataFramePublisher.getDataFrameSigner(builder.credentials, builder.credentialScope, builder.signature, builder.signingClock));
            trailingPublisher.subscribe((Subscriber)adaptedSubscriber);
        };
    }

    private static Function<ByteBuffer, ByteBuffer> getDataFrameSigner(final AwsCredentialsIdentity credentials, final CredentialScope credentialScope, final String signature, final Clock signingClock) {
        return new Function<ByteBuffer, ByteBuffer>(){
            String priorSignature;
            {
                this.priorSignature = signature;
            }

            @Override
            public ByteBuffer apply(ByteBuffer byteBuffer) {
                HashMap<String, HeaderValue> eventHeaders = new HashMap<String, HeaderValue>();
                Instant signingInstant = signingClock.instant();
                eventHeaders.put(":date", HeaderValue.fromTimestamp((Instant)signingInstant));
                CredentialScope updatedCredentialScope = new CredentialScope(credentialScope.getRegion(), credentialScope.getService(), signingInstant);
                byte[] signingKey = SignerUtils.deriveSigningKey(credentials, updatedCredentialScope);
                byte[] payload = new byte[byteBuffer.remaining()];
                byteBuffer.get(payload);
                byte[] signatureBytes = SigV4DataFramePublisher.signEvent(this.priorSignature, signingKey, updatedCredentialScope, eventHeaders, payload);
                this.priorSignature = BinaryUtils.toHex((byte[])signatureBytes);
                HashMap<String, HeaderValue> headers = new HashMap<String, HeaderValue>(eventHeaders);
                headers.put(SigV4DataFramePublisher.CHUNK_SIGNATURE, HeaderValue.fromByteArray((byte[])signatureBytes));
                Message signedMessage = new Message((Map)SigV4DataFramePublisher.sortHeaders(headers), payload);
                if (LOG.isLoggingLevelEnabled("trace")) {
                    LOG.trace(() -> "Signed message: " + SigV4DataFramePublisher.toDebugString(signedMessage, false));
                } else {
                    LOG.debug(() -> "Signed message: " + SigV4DataFramePublisher.toDebugString(signedMessage, true));
                }
                return signedMessage.toByteBuffer();
            }
        };
    }

    private static byte[] signEvent(String priorSignature, byte[] signingKey, CredentialScope credentialScope, Map<String, HeaderValue> eventHeaders, byte[] event) {
        String eventHeadersSignature = BinaryUtils.toHex((byte[])SignerUtils.hash(Message.encodeHeaders(SigV4DataFramePublisher.sortHeaders(eventHeaders).entrySet())));
        String eventHash = BinaryUtils.toHex((byte[])SignerUtils.hash(event));
        String stringToSign = "AWS4-HMAC-SHA256-PAYLOAD\n" + credentialScope.getDatetime() + "\n" + credentialScope.scope() + "\n" + priorSignature + "\n" + eventHeadersSignature + "\n" + eventHash;
        return SignerUtils.computeSignature(stringToSign, signingKey);
    }

    private static TreeMap<String, HeaderValue> sortHeaders(Map<String, HeaderValue> headers) {
        TreeMap<String, HeaderValue> sortedHeaders = new TreeMap<String, HeaderValue>((header1, header2) -> {
            if (header1.equals(CHUNK_SIGNATURE)) {
                return 1;
            }
            if (header2.equals(CHUNK_SIGNATURE)) {
                return -1;
            }
            return header1.compareTo((String)header2);
        });
        sortedHeaders.putAll(headers);
        return sortedHeaders;
    }

    private static String toDebugString(Message m, boolean truncatePayload) {
        StringBuilder sb = new StringBuilder("Message = {headers={");
        Map headers = m.getHeaders();
        Iterator headersIter = headers.entrySet().iterator();
        while (headersIter.hasNext()) {
            Map.Entry h = headersIter.next();
            sb.append((String)h.getKey()).append("={").append(((HeaderValue)h.getValue()).toString()).append("}");
            if (!headersIter.hasNext()) continue;
            sb.append(", ");
        }
        sb.append("}, payload=");
        byte[] payload = m.getPayload();
        truncatePayload = truncatePayload && payload.length > 32;
        byte[] payloadToLog = truncatePayload ? Arrays.copyOf(payload, 32) : payload;
        sb.append(BinaryUtils.toHex((byte[])payloadToLog));
        if (truncatePayload) {
            sb.append("...");
        }
        sb.append("}");
        return sb.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public void subscribe(Subscriber<? super ByteBuffer> subscriber) {
        this.sigv4Publisher.subscribe(subscriber);
    }

    public static class Builder {
        private Publisher<ByteBuffer> publisher;
        private AwsCredentialsIdentity credentials;
        private CredentialScope credentialScope;
        private String signature;
        private Clock signingClock;

        public Builder publisher(Publisher<ByteBuffer> publisher) {
            this.publisher = publisher;
            return this;
        }

        public Builder credentials(AwsCredentialsIdentity credentials) {
            this.credentials = credentials;
            return this;
        }

        public Builder credentialScope(CredentialScope credentialScope) {
            this.credentialScope = credentialScope;
            return this;
        }

        public Builder signature(String signature) {
            this.signature = signature;
            return this;
        }

        public Builder signingClock(Clock signingClock) {
            this.signingClock = signingClock;
            return this;
        }

        public SigV4DataFramePublisher build() {
            return new SigV4DataFramePublisher(this);
        }
    }
}

