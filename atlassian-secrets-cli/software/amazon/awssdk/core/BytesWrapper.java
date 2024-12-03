/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public abstract class BytesWrapper {
    private final byte[] bytes;

    @SdkInternalApi
    BytesWrapper() {
        this(new byte[0]);
    }

    @SdkInternalApi
    BytesWrapper(byte[] bytes) {
        this.bytes = Validate.paramNotNull(bytes, "bytes");
    }

    public final ByteBuffer asByteBuffer() {
        return ByteBuffer.wrap(this.bytes).asReadOnlyBuffer();
    }

    public final byte[] asByteArray() {
        return Arrays.copyOf(this.bytes, this.bytes.length);
    }

    public final byte[] asByteArrayUnsafe() {
        return this.bytes;
    }

    public final String asString(Charset charset) throws UncheckedIOException {
        return StringUtils.fromBytes(this.bytes, charset);
    }

    public final String asUtf8String() throws UncheckedIOException {
        return this.asString(StandardCharsets.UTF_8);
    }

    public final InputStream asInputStream() {
        return new ByteArrayInputStream(this.bytes);
    }

    public final ContentStreamProvider asContentStreamProvider() {
        return this::asInputStream;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BytesWrapper sdkBytes = (BytesWrapper)o;
        return Arrays.equals(this.bytes, sdkBytes.bytes);
    }

    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }
}

