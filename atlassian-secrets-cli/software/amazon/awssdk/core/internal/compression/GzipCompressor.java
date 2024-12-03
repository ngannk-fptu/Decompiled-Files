/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.compression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.zip.GZIPOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.internal.compression.Compressor;
import software.amazon.awssdk.utils.IoUtils;

@SdkInternalApi
public final class GzipCompressor
implements Compressor {
    private static final String COMPRESSOR_TYPE = "gzip";
    private static final Logger log = LoggerFactory.getLogger(GzipCompressor.class);

    @Override
    public String compressorType() {
        return COMPRESSOR_TYPE;
    }

    @Override
    public SdkBytes compress(SdkBytes content) {
        SdkBytes sdkBytes;
        GZIPOutputStream gzipOutputStream = null;
        try {
            ByteArrayOutputStream compressedOutputStream = new ByteArrayOutputStream();
            gzipOutputStream = new GZIPOutputStream(compressedOutputStream);
            gzipOutputStream.write(content.asByteArray());
            gzipOutputStream.close();
            sdkBytes = SdkBytes.fromByteArray(compressedOutputStream.toByteArray());
        }
        catch (IOException e) {
            try {
                throw new UncheckedIOException(e);
            }
            catch (Throwable throwable) {
                IoUtils.closeQuietly(gzipOutputStream, log);
                throw throwable;
            }
        }
        IoUtils.closeQuietly(gzipOutputStream, log);
        return sdkBytes;
    }
}

