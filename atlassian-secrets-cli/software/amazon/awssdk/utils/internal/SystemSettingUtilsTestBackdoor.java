/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.internal;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;

@SdkTestInternalApi
@SdkInternalApi
public final class SystemSettingUtilsTestBackdoor {
    private static final Map<String, String> ENVIRONMENT_OVERRIDES = new HashMap<String, String>();

    private SystemSettingUtilsTestBackdoor() {
    }

    public static void addEnvironmentVariableOverride(String key, String value) {
        ENVIRONMENT_OVERRIDES.put(key, value);
    }

    public static void clearEnvironmentVariableOverrides() {
        ENVIRONMENT_OVERRIDES.clear();
    }

    static String getEnvironmentVariable(String key) {
        if (!ENVIRONMENT_OVERRIDES.isEmpty() && ENVIRONMENT_OVERRIDES.containsKey(key)) {
            return ENVIRONMENT_OVERRIDES.get(key);
        }
        return System.getenv(key);
    }
}

