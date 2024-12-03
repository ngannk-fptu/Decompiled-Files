/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.JavaSystemSetting;
import software.amazon.awssdk.utils.StringUtils;

@SdkProtectedApi
public class Platform {
    private Platform() {
    }

    public static boolean isWindows() {
        return JavaSystemSetting.OS_NAME.getStringValue().map(s -> StringUtils.lowerCase(s).startsWith("windows")).orElse(false);
    }
}

