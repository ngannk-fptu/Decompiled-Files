/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.client.handler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.eventstream.HeaderValue;
import software.amazon.eventstream.Message;

@SdkProtectedApi
public final class AwsClientHandlerUtils {
    private AwsClientHandlerUtils() {
    }

    public static ByteBuffer encodeEventStreamRequestToByteBuffer(SdkHttpFullRequest request) {
        LinkedHashMap<String, HeaderValue> headers = new LinkedHashMap<String, HeaderValue>();
        request.forEachHeader((name, value) -> headers.put((String)name, HeaderValue.fromString((String)CollectionUtils.firstIfPresent(value))));
        byte[] payload = null;
        if (request.contentStreamProvider().isPresent()) {
            try {
                payload = IoUtils.toByteArray(request.contentStreamProvider().get().newStream());
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return new Message(headers, payload).toByteBuffer();
    }
}

