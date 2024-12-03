/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.JavaSystemSetting;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.SystemSetting;

@SdkProtectedApi
public final class UserHomeDirectoryUtils {
    private UserHomeDirectoryUtils() {
    }

    public static String userHomeDirectory() {
        Optional<String> home = SystemSetting.getStringValueFromEnvironmentVariable("HOME");
        if (home.isPresent()) {
            return home.get();
        }
        boolean isWindows = JavaSystemSetting.OS_NAME.getStringValue().map(s -> StringUtils.lowerCase(s).startsWith("windows")).orElse(false);
        if (isWindows) {
            Optional<String> userProfile = SystemSetting.getStringValueFromEnvironmentVariable("USERPROFILE");
            if (userProfile.isPresent()) {
                return userProfile.get();
            }
            Optional<String> homeDrive = SystemSetting.getStringValueFromEnvironmentVariable("HOMEDRIVE");
            Optional<String> homePath = SystemSetting.getStringValueFromEnvironmentVariable("HOMEPATH");
            if (homeDrive.isPresent() && homePath.isPresent()) {
                return homeDrive.get() + homePath.get();
            }
        }
        return JavaSystemSetting.USER_HOME.getStringValueOrThrow();
    }
}

