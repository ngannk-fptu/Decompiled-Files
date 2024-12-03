/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
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

