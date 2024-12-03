/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 */
package software.amazon.awssdk.core.util;

import java.util.UUID;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;

@SdkProtectedApi
public final class IdempotentUtils {
    private static Supplier<String> generator = () -> UUID.randomUUID().toString();

    private IdempotentUtils() {
    }

    @Deprecated
    @SdkProtectedApi
    public static String resolveString(String token) {
        return token != null ? token : generator.get();
    }

    @SdkProtectedApi
    public static Supplier<String> getGenerator() {
        return generator;
    }

    @SdkTestInternalApi
    public static void setGenerator(Supplier<String> newGenerator) {
        generator = newGenerator;
    }
}

