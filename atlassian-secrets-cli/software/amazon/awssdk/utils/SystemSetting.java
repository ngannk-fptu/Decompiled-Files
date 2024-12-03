/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.internal.SystemSettingUtils;

@SdkProtectedApi
public interface SystemSetting {
    public String property();

    public String environmentVariable();

    public String defaultValue();

    default public Optional<String> getStringValue() {
        return SystemSettingUtils.resolveSetting(this);
    }

    public static Optional<String> getStringValueFromEnvironmentVariable(String key) {
        return SystemSettingUtils.resolveEnvironmentVariable(key);
    }

    default public Optional<String> getNonDefaultStringValue() {
        return SystemSettingUtils.resolveNonDefaultSetting(this);
    }

    default public String getStringValueOrThrow() {
        return this.getStringValue().orElseThrow(() -> new IllegalStateException("Either the environment variable " + this.environmentVariable() + " or the javaproperty " + this.property() + " must be set."));
    }

    default public Optional<Integer> getIntegerValue() {
        return this.getStringValue().map(Integer::parseInt);
    }

    default public Integer getIntegerValueOrThrow() {
        return Integer.parseInt(this.getStringValueOrThrow());
    }

    default public Optional<Boolean> getBooleanValue() {
        return this.getStringValue().map(value -> SystemSettingUtils.safeStringToBoolean(this, value));
    }

    default public Boolean getBooleanValueOrThrow() {
        return this.getBooleanValue().orElseThrow(() -> new IllegalStateException("Either the environment variable " + this.environmentVariable() + " or the javaproperty " + this.property() + " must be set."));
    }
}

