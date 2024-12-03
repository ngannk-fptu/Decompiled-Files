/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.util;

import java.util.Collection;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public final class PaginatorUtils {
    private PaginatorUtils() {
    }

    public static <T> boolean isOutputTokenAvailable(T outputToken) {
        if (outputToken == null) {
            return false;
        }
        if (outputToken instanceof String) {
            return !((String)outputToken).isEmpty();
        }
        if (outputToken instanceof Map) {
            return !((Map)outputToken).isEmpty();
        }
        if (outputToken instanceof Collection) {
            return !((Collection)outputToken).isEmpty();
        }
        return true;
    }
}

