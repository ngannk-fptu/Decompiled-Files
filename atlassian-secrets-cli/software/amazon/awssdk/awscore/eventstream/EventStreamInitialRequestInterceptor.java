/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.eventstream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Optional;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.internal.async.AsyncStreamPrepender;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.eventstream.HeaderValue;
import software.amazon.eventstream.Message;

@SdkProtectedApi
public class EventStreamInitialRequestInterceptor
implements ExecutionInterceptor {
    @Override
    public SdkHttpRequest modifyHttpRequest(Context.ModifyHttpRequest context, ExecutionAttributes executionAttributes) {
        if (!Boolean.TRUE.equals(executionAttributes.getAttribute(SdkInternalExecutionAttribute.HAS_INITIAL_REQUEST_EVENT))) {
            return context.httpRequest();
        }
        return (SdkHttpRequest)((SdkHttpRequest.Builder)context.httpRequest().toBuilder()).removeHeader("Content-Type").putHeader("Content-Type", "application/vnd.amazon.eventstream").build();
    }

    @Override
    public Optional<AsyncRequestBody> modifyAsyncHttpContent(Context.ModifyHttpRequest context, ExecutionAttributes executionAttributes) {
        if (!Boolean.TRUE.equals(executionAttributes.getAttribute(SdkInternalExecutionAttribute.HAS_INITIAL_REQUEST_EVENT))) {
            return context.asyncRequestBody();
        }
        byte[] payload = this.getInitialRequestPayload(context);
        String contentType = context.httpRequest().firstMatchingHeader("Content-Type").orElseThrow(() -> new IllegalStateException("Content-Type header not defined."));
        HashMap<String, HeaderValue> initialRequestEventHeaders = new HashMap<String, HeaderValue>();
        initialRequestEventHeaders.put(":message-type", HeaderValue.fromString("event"));
        initialRequestEventHeaders.put(":event-type", HeaderValue.fromString("initial-request"));
        initialRequestEventHeaders.put(":content-type", HeaderValue.fromString(contentType));
        ByteBuffer initialRequest = new Message(initialRequestEventHeaders, payload).toByteBuffer();
        Publisher asyncRequestBody = context.asyncRequestBody().orElseThrow(() -> new IllegalStateException("This request is an event streaming request and thus should have an asyncRequestBody"));
        AsyncStreamPrepender<ByteBuffer> withInitialRequest = new AsyncStreamPrepender<ByteBuffer>(asyncRequestBody, initialRequest);
        return Optional.of(AsyncRequestBody.fromPublisher(withInitialRequest));
    }

    private byte[] getInitialRequestPayload(Context.ModifyHttpRequest context) {
        byte[] payload;
        RequestBody requestBody = context.requestBody().orElseThrow(() -> new IllegalStateException("This request should have a requestBody"));
        try (InputStream inputStream = requestBody.contentStreamProvider().newStream();){
            payload = new byte[inputStream.available()];
            int bytesRead = inputStream.read(payload);
            if (bytesRead != payload.length) {
                throw new IllegalStateException("Expected " + payload.length + " bytes, but only got " + bytesRead + " bytes");
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Unable to read serialized request payload", ex);
        }
        return payload;
    }
}

