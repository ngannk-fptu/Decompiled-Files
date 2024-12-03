/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.utils.CollectionUtils
 *  software.amazon.awssdk.utils.IoUtils
 *  software.amazon.eventstream.HeaderValue
 *  software.amazon.eventstream.Message
 */
package software.amazon.awssdk.awscore.client.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.List;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.http.ContentStreamProvider;
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
        LinkedHashMap headers = new LinkedHashMap();
        request.forEachHeader((name, value) -> headers.put(name, HeaderValue.fromString((String)((String)CollectionUtils.firstIfPresent((List)value)))));
        byte[] payload = null;
        if (request.contentStreamProvider().isPresent()) {
            try {
                payload = IoUtils.toByteArray((InputStream)((ContentStreamProvider)request.contentStreamProvider().get()).newStream());
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return new Message(headers, payload).toByteBuffer();
    }
}

