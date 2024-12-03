/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.utils.internal;

import java.util.Optional;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.OptionalUtils;
import software.amazon.awssdk.utils.SystemSetting;
import software.amazon.awssdk.utils.internal.SystemSettingUtilsTestBackdoor;

@SdkInternalApi
public final class SystemSettingUtils {
    private static final Logger LOG = LoggerFactory.getLogger(SystemSettingUtils.class);

    private SystemSettingUtils() {
    }

    public static Optional<String> resolveSetting(SystemSetting setting) {
        return OptionalUtils.firstPresent(SystemSettingUtils.resolveProperty(setting), () -> SystemSettingUtils.resolveEnvironmentVariable(setting), () -> SystemSettingUtils.resolveDefault(setting)).map(String::trim);
    }

    public static Optional<String> resolveNonDefaultSetting(SystemSetting setting) {
        return OptionalUtils.firstPresent(SystemSettingUtils.resolveProperty(setting), new Supplier[]{() -> SystemSettingUtils.resolveEnvironmentVariable(setting)}).map(String::trim);
    }

    private static Optional<String> resolveProperty(SystemSetting setting) {
        return Optional.ofNullable(setting.property()).map(System::getProperty);
    }

    public static Optional<String> resolveEnvironmentVariable(SystemSetting setting) {
        return SystemSettingUtils.resolveEnvironmentVariable(setting.environmentVariable());
    }

    public static Optional<String> resolveEnvironmentVariable(String key) {
        try {
            return Optional.ofNullable(key).map(SystemSettingUtilsTestBackdoor::getEnvironmentVariable);
        }
        catch (SecurityException e) {
            LOG.debug("Unable to load the environment variable '{}' because the security manager did not allow the SDK to read this system property. This setting will be assumed to be null", (Object)key, (Object)e);
            return Optional.empty();
        }
    }

    private static Optional<String> resolveDefault(SystemSetting setting) {
        return Optional.ofNullable(setting.defaultValue());
    }

    public static Boolean safeStringToBoolean(SystemSetting setting, String value) {
        if (value.equalsIgnoreCase("true")) {
            return true;
        }
        if (value.equalsIgnoreCase("false")) {
            return false;
        }
        throw new IllegalStateException("Environment variable '" + setting.environmentVariable() + "' or system property '" + setting.property() + "' was defined as '" + value + "', but should be 'false' or 'true'");
    }
}

