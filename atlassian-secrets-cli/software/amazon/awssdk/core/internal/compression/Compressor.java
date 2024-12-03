/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.compression;

import java.io.InputStream;
import java.nio.ByteBuffer;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkBytes;

@SdkInternalApi
public interface Compressor {
    public String compressorType();

    public SdkBytes compress(SdkBytes var1);

    default public byte[] compress(byte[] content) {
        return this.compress(SdkBytes.fromByteArray(content)).asByteArray();
    }

    default public InputStream compress(InputStream content) {
        return this.compress(SdkBytes.fromInputStream(content)).asInputStream();
    }

    default public ByteBuffer compress(ByteBuffer content) {
        return this.compress(SdkBytes.fromByteBuffer(content)).asByteBuffer();
    }
}

