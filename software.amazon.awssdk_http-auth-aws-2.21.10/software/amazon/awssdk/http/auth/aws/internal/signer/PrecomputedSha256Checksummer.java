/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 */
package software.amazon.awssdk.http.auth.aws.internal.signer;

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.internal.signer.Checksummer;

@SdkInternalApi
public final class PrecomputedSha256Checksummer
implements Checksummer {
    private final Callable<String> computation;

    public PrecomputedSha256Checksummer(Callable<String> computation) {
        this.computation = computation;
    }

    @Override
    public void checksum(ContentStreamProvider payload, SdkHttpRequest.Builder request) {
        try {
            String checksum = this.computation.call();
            request.putHeader("x-amz-content-sha256", checksum);
        }
        catch (Exception e) {
            throw new RuntimeException("Could not retrieve checksum: ", e);
        }
    }

    @Override
    public CompletableFuture<Publisher<ByteBuffer>> checksum(Publisher<ByteBuffer> payload, SdkHttpRequest.Builder request) {
        try {
            String checksum = this.computation.call();
            request.putHeader("x-amz-content-sha256", checksum);
            return CompletableFuture.completedFuture(payload);
        }
        catch (Exception e) {
            throw new RuntimeException("Could not retrieve checksum: ", e);
        }
    }
}

