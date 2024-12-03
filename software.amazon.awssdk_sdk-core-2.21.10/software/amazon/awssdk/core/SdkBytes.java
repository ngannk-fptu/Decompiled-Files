/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.BinaryUtils
 *  software.amazon.awssdk.utils.FunctionalUtils
 *  software.amazon.awssdk.utils.IoUtils
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
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
        Validate.paramNotNull((Object)byteBuffer, (String)"byteBuffer");
        return new SdkBytes(BinaryUtils.copyBytesFrom((ByteBuffer)byteBuffer));
    }

    public static SdkBytes fromByteArray(byte[] bytes) {
        Validate.paramNotNull((Object)bytes, (String)"bytes");
        return new SdkBytes(Arrays.copyOf(bytes, bytes.length));
    }

    public static SdkBytes fromByteArrayUnsafe(byte[] bytes) {
        Validate.paramNotNull((Object)bytes, (String)"bytes");
        return new SdkBytes(bytes);
    }

    public static SdkBytes fromString(String string, Charset charset) {
        Validate.paramNotNull((Object)string, (String)"string");
        Validate.paramNotNull((Object)charset, (String)"charset");
        return new SdkBytes(string.getBytes(charset));
    }

    public static SdkBytes fromUtf8String(String string) {
        return SdkBytes.fromString(string, StandardCharsets.UTF_8);
    }

    public static SdkBytes fromInputStream(InputStream inputStream) {
        Validate.paramNotNull((Object)inputStream, (String)"inputStream");
        return new SdkBytes((byte[])FunctionalUtils.invokeSafely(() -> IoUtils.toByteArray((InputStream)inputStream)));
    }

    public String toString() {
        return ToString.builder((String)"SdkBytes").add("bytes", (Object)this.asByteArrayUnsafe()).build();
    }
}

