/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.signer.internal;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.signer.internal.AsyncSigV4SubscriberAdapter;
import software.amazon.awssdk.auth.signer.internal.Aws4SignerRequestParams;
import software.amazon.awssdk.auth.signer.internal.Aws4SignerUtils;
import software.amazon.awssdk.auth.signer.internal.BaseAsyncAws4Signer;
import software.amazon.awssdk.auth.signer.internal.SigningAlgorithm;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.eventstream.HeaderValue;
import software.amazon.eventstream.Message;

@SdkInternalApi
public abstract class BaseEventStreamAsyncAws4Signer
extends BaseAsyncAws4Signer {
    public static final String EVENT_STREAM_SIGNATURE = ":chunk-signature";
    public static final String EVENT_STREAM_DATE = ":date";
    private static final Logger LOG = Logger.loggerFor(BaseEventStreamAsyncAws4Signer.class);
    private static final String HTTP_CONTENT_SHA_256 = "STREAMING-AWS4-HMAC-SHA256-EVENTS";
    private static final String EVENT_STREAM_PAYLOAD = "AWS4-HMAC-SHA256-PAYLOAD";
    private static final int PAYLOAD_TRUNCATE_LENGTH = 32;

    protected BaseEventStreamAsyncAws4Signer() {
    }

    @Override
    public SdkHttpFullRequest sign(SdkHttpFullRequest request, ExecutionAttributes executionAttributes) {
        request = this.addContentSha256Header(request);
        return super.sign(request, executionAttributes);
    }

    @Override
    public SdkHttpFullRequest sign(SdkHttpFullRequest request, Aws4SignerParams signingParams) {
        request = this.addContentSha256Header(request);
        return super.sign(request, signingParams);
    }

    @Override
    protected AsyncRequestBody transformRequestProvider(String headerSignature, Aws4SignerRequestParams signerRequestParams, Aws4SignerParams signerParams, AsyncRequestBody asyncRequestBody) {
        Publisher<ByteBuffer> publisherWithTrailingEmptyFrame = BaseEventStreamAsyncAws4Signer.appendEmptyFrame(asyncRequestBody);
        Publisher<ByteBuffer> publisherWithSignedFrame = this.transformRequestBodyPublisher(publisherWithTrailingEmptyFrame, headerSignature, signerParams.awsCredentials(), signerRequestParams);
        AsyncRequestBody transformedRequestBody = AsyncRequestBody.fromPublisher(publisherWithSignedFrame);
        return new SigningRequestBodyProvider(transformedRequestBody);
    }

    @Override
    protected String calculateContentHash(SdkHttpFullRequest.Builder mutableRequest, Aws4SignerParams signerParams, SdkChecksum contentFlexibleChecksum) {
        return HTTP_CONTENT_SHA_256;
    }

    private static Publisher<ByteBuffer> appendEmptyFrame(Publisher<ByteBuffer> publisher) {
        return s -> {
            AsyncSigV4SubscriberAdapter adaptedSubscriber = new AsyncSigV4SubscriberAdapter(s);
            publisher.subscribe(adaptedSubscriber);
        };
    }

    private Publisher<ByteBuffer> transformRequestBodyPublisher(Publisher<ByteBuffer> publisher, String headerSignature, AwsCredentials credentials, Aws4SignerRequestParams signerRequestParams) {
        return SdkPublisher.adapt(publisher).map(this.getDataFrameSigner(headerSignature, credentials, signerRequestParams));
    }

    private Function<ByteBuffer, ByteBuffer> getDataFrameSigner(final String headerSignature, final AwsCredentials credentials, final Aws4SignerRequestParams signerRequestParams) {
        return new Function<ByteBuffer, ByteBuffer>(){
            final Aws4SignerRequestParams requestParams;
            String priorSignature;
            {
                this.requestParams = signerRequestParams;
                this.priorSignature = headerSignature;
            }

            @Override
            public ByteBuffer apply(ByteBuffer byteBuffer) {
                HashMap<String, HeaderValue> nonSignatureHeaders = new HashMap<String, HeaderValue>();
                Instant signingInstant = this.requestParams.getSigningClock().instant();
                nonSignatureHeaders.put(BaseEventStreamAsyncAws4Signer.EVENT_STREAM_DATE, HeaderValue.fromTimestamp(signingInstant));
                AwsCredentials sanitizedCredentials = BaseEventStreamAsyncAws4Signer.this.sanitizeCredentials(credentials);
                byte[] signingKey = BaseEventStreamAsyncAws4Signer.this.deriveSigningKey(sanitizedCredentials, signingInstant, this.requestParams.getRegionName(), this.requestParams.getServiceSigningName());
                byte[] payload = byteBuffer.array();
                byte[] signatureBytes = BaseEventStreamAsyncAws4Signer.this.signEventStream(this.priorSignature, signingKey, signingInstant, this.requestParams, nonSignatureHeaders, payload);
                this.priorSignature = BinaryUtils.toHex(signatureBytes);
                HashMap<String, HeaderValue> headers = new HashMap<String, HeaderValue>(nonSignatureHeaders);
                headers.put(BaseEventStreamAsyncAws4Signer.EVENT_STREAM_SIGNATURE, HeaderValue.fromByteArray(signatureBytes));
                Message signedMessage = new Message(BaseEventStreamAsyncAws4Signer.this.sortHeaders(headers), payload);
                if (LOG.isLoggingLevelEnabled("trace")) {
                    LOG.trace(() -> "Signed message: " + BaseEventStreamAsyncAws4Signer.toDebugString(signedMessage, false));
                } else {
                    LOG.debug(() -> "Signed message: " + BaseEventStreamAsyncAws4Signer.toDebugString(signedMessage, true));
                }
                return signedMessage.toByteBuffer();
            }
        };
    }

    private byte[] signEventStream(String priorSignature, byte[] signingKey, Instant signingInstant, Aws4SignerRequestParams requestParams, Map<String, HeaderValue> nonSignatureHeaders, byte[] payload) {
        String stringToSign = "AWS4-HMAC-SHA256-PAYLOAD\n" + Aws4SignerUtils.formatTimestamp(signingInstant) + "\n" + this.computeScope(signingInstant, requestParams) + "\n" + priorSignature + "\n" + BinaryUtils.toHex(this.hash(Message.encodeHeaders(this.sortHeaders(nonSignatureHeaders).entrySet()))) + "\n" + BinaryUtils.toHex(this.hash(payload));
        return this.sign(stringToSign.getBytes(StandardCharsets.UTF_8), signingKey, SigningAlgorithm.HmacSHA256);
    }

    private String computeScope(Instant signingInstant, Aws4SignerRequestParams requestParams) {
        return Aws4SignerUtils.formatDateStamp(signingInstant) + "/" + requestParams.getRegionName() + "/" + requestParams.getServiceSigningName() + "/" + "aws4_request";
    }

    private TreeMap<String, HeaderValue> sortHeaders(Map<String, HeaderValue> headers) {
        TreeMap<String, HeaderValue> sortedHeaders = new TreeMap<String, HeaderValue>((header1, header2) -> {
            if (header1.equals(EVENT_STREAM_SIGNATURE)) {
                return 1;
            }
            if (header2.equals(EVENT_STREAM_SIGNATURE)) {
                return -1;
            }
            return header1.compareTo((String)header2);
        });
        sortedHeaders.putAll(headers);
        return sortedHeaders;
    }

    private SdkHttpFullRequest addContentSha256Header(SdkHttpFullRequest request) {
        return request.toBuilder().putHeader("x-amz-content-sha256", HTTP_CONTENT_SHA_256).build();
    }

    static String toDebugString(Message m, boolean truncatePayload) {
        StringBuilder sb = new StringBuilder("Message = {headers={");
        Map<String, HeaderValue> headers = m.getHeaders();
        Iterator<Map.Entry<String, HeaderValue>> headersIter = headers.entrySet().iterator();
        while (headersIter.hasNext()) {
            Map.Entry<String, HeaderValue> h = headersIter.next();
            sb.append(h.getKey()).append("={").append(h.getValue().toString()).append("}");
            if (!headersIter.hasNext()) continue;
            sb.append(", ");
        }
        sb.append("}, payload=");
        byte[] payload = m.getPayload();
        truncatePayload = truncatePayload && payload.length > 32;
        byte[] payloadToLog = truncatePayload ? Arrays.copyOf(payload, 32) : payload;
        sb.append(BinaryUtils.toHex(payloadToLog));
        if (truncatePayload) {
            sb.append("...");
        }
        sb.append("}");
        return sb.toString();
    }

    private static class SigningRequestBodyProvider
    implements AsyncRequestBody {
        private AsyncRequestBody transformedRequestBody;

        SigningRequestBodyProvider(AsyncRequestBody transformedRequestBody) {
            this.transformedRequestBody = transformedRequestBody;
        }

        @Override
        public void subscribe(Subscriber<? super ByteBuffer> s) {
            this.transformedRequestBody.subscribe(s);
        }

        @Override
        public Optional<Long> contentLength() {
            return this.transformedRequestBody.contentLength();
        }

        @Override
        public String contentType() {
            return this.transformedRequestBody.contentType();
        }
    }
}

