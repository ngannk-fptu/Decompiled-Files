/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.core.internal.async;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.internal.async.EnvelopeWrappedSdkPublisher;

@SdkInternalApi
public final class SdkPublishers {
    private SdkPublishers() {
    }

    public static SdkPublisher<ByteBuffer> envelopeWrappedPublisher(Publisher<ByteBuffer> publisher, String envelopePrefix, String envelopeSuffix) {
        return EnvelopeWrappedSdkPublisher.of(publisher, SdkPublishers.wrapUtf8(envelopePrefix), SdkPublishers.wrapUtf8(envelopeSuffix), SdkPublishers::concat);
    }

    private static ByteBuffer wrapUtf8(String s) {
        return ByteBuffer.wrap(s.getBytes(StandardCharsets.UTF_8));
    }

    private static ByteBuffer concat(ByteBuffer b1, ByteBuffer b2) {
        ByteBuffer result = ByteBuffer.allocate(b1.remaining() + b2.remaining());
        result.put(b1);
        result.put(b2);
        result.rewind();
        return result;
    }
}

