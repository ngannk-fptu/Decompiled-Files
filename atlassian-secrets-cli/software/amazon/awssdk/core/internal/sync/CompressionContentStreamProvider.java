/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.sync;

import java.io.InputStream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.compression.Compressor;
import software.amazon.awssdk.core.internal.io.AwsCompressionInputStream;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.utils.IoUtils;

@SdkInternalApi
public class CompressionContentStreamProvider
implements ContentStreamProvider {
    private final ContentStreamProvider underlyingInputStreamProvider;
    private InputStream currentStream;
    private final Compressor compressor;

    public CompressionContentStreamProvider(ContentStreamProvider underlyingInputStreamProvider, Compressor compressor) {
        this.underlyingInputStreamProvider = underlyingInputStreamProvider;
        this.compressor = compressor;
    }

    @Override
    public InputStream newStream() {
        this.closeCurrentStream();
        this.currentStream = AwsCompressionInputStream.builder().inputStream(this.underlyingInputStreamProvider.newStream()).compressor(this.compressor).build();
        return this.currentStream;
    }

    private void closeCurrentStream() {
        if (this.currentStream != null) {
            IoUtils.closeQuietly(this.currentStream, null);
            this.currentStream = null;
        }
    }
}

