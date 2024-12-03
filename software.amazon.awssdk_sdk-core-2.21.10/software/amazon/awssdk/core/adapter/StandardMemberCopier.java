/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.utils.BinaryUtils
 */
package software.amazon.awssdk.core.adapter;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Instant;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.utils.BinaryUtils;

@SdkProtectedApi
public final class StandardMemberCopier {
    private StandardMemberCopier() {
    }

    public static String copy(String s) {
        return s;
    }

    public static Short copy(Short s) {
        return s;
    }

    public static Integer copy(Integer i) {
        return i;
    }

    public static Long copy(Long l) {
        return l;
    }

    public static Float copy(Float f) {
        return f;
    }

    public static Double copy(Double d) {
        return d;
    }

    public static BigDecimal copy(BigDecimal bd) {
        return bd;
    }

    public static Boolean copy(Boolean b) {
        return b;
    }

    public static InputStream copy(InputStream is) {
        return is;
    }

    public static Instant copy(Instant i) {
        return i;
    }

    public static SdkBytes copy(SdkBytes bytes) {
        return bytes;
    }

    public static ByteBuffer copy(ByteBuffer bb) {
        if (bb == null) {
            return null;
        }
        return ByteBuffer.wrap(BinaryUtils.copyBytesFrom((ByteBuffer)bb)).asReadOnlyBuffer();
    }
}

