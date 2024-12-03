/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils;

import java.util.Locale;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.SystemSetting;
import software.amazon.awssdk.utils.internal.SystemSettingUtils;

@SdkProtectedApi
public enum ProxyEnvironmentSetting implements SystemSetting
{
    HTTP_PROXY("http_proxy"),
    HTTPS_PROXY("https_proxy"),
    NO_PROXY("no_proxy");

    private final String environmentVariable;

    private ProxyEnvironmentSetting(String environmentVariable) {
        this.environmentVariable = environmentVariable;
    }

    @Override
    public Optional<String> getStringValue() {
        Optional<String> envVarLowercase = SystemSettingUtils.resolveEnvironmentVariable(this.environmentVariable);
        if (envVarLowercase.isPresent()) {
            return this.getValueIfValid(envVarLowercase.get());
        }
        Optional<String> envVarUppercase = SystemSettingUtils.resolveEnvironmentVariable(this.environmentVariable.toUpperCase(Locale.getDefault()));
        if (envVarUppercase.isPresent()) {
            return this.getValueIfValid(envVarUppercase.get());
        }
        return Optional.empty();
    }

    @Override
    public String property() {
        return null;
    }

    @Override
    public String environmentVariable() {
        return this.environmentVariable;
    }

    @Override
    public String defaultValue() {
        return null;
    }

    private Optional<String> getValueIfValid(String value) {
        String trimmedValue = value.trim();
        if (!trimmedValue.isEmpty()) {
            return Optional.of(trimmedValue);
        }
        return Optional.empty();
    }
}

