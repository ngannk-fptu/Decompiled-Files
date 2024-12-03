/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.defaultsmode;

import java.util.Map;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.internal.EnumUtils;

@SdkPublicApi
public enum DefaultsMode {
    LEGACY("legacy"),
    STANDARD("standard"),
    MOBILE("mobile"),
    CROSS_REGION("cross-region"),
    IN_REGION("in-region"),
    AUTO("auto");

    private static final Map<String, DefaultsMode> VALUE_MAP;
    private final String value;

    private DefaultsMode(String value) {
        this.value = value;
    }

    public static DefaultsMode fromValue(String value) {
        Validate.paramNotNull(value, "value");
        if (!VALUE_MAP.containsKey(value)) {
            throw new IllegalArgumentException("The provided value is not a valid defaults mode " + value);
        }
        return VALUE_MAP.get(value);
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(DefaultsMode.class, DefaultsMode::toString);
    }
}

