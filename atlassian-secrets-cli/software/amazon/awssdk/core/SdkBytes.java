/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.BytesWrapper;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class SdkBytes
extends BytesWrapper
implements Serializable {
    private static final long serialVersionUID = 1L;

    private SdkBytes() {
    }

    private SdkBytes(byte[] bytes) {
        super(bytes);
    }

    public static SdkBytes fromByteBuffer(ByteBuffer byteBuffer) {
        Validate.paramNotNull(byteBuffer, "byteBuffer");
        return new SdkBytes(BinaryUtils.copyBytesFrom(byteBuffer));
    }

    public static SdkBytes fromByteArray(byte[] bytes) {
        Validate.paramNotNull(bytes, "bytes");
        return new SdkBytes(Arrays.copyOf(bytes, bytes.length));
    }

    public static SdkBytes fromByteArrayUnsafe(byte[] bytes) {
        Validate.paramNotNull(bytes, "bytes");
        return new SdkBytes(bytes);
    }

    public static SdkBytes fromString(String string, Charset charset) {
        Validate.paramNotNull(string, "string");
        Validate.paramNotNull(charset, "charset");
        return new SdkBytes(string.getBytes(charset));
    }

    public static SdkBytes fromUtf8String(String string) {
        return SdkBytes.fromString(string, StandardCharsets.UTF_8);
    }

    public static SdkBytes fromInputStream(InputStream inputStream) {
        Validate.paramNotNull(inputStream, "inputStream");
        return new SdkBytes(FunctionalUtils.invokeSafely(() -> IoUtils.toByteArray(inputStream)));
    }

    public String toString() {
        return ToString.builder("SdkBytes").add("bytes", this.asByteArrayUnsafe()).build();
    }
}

