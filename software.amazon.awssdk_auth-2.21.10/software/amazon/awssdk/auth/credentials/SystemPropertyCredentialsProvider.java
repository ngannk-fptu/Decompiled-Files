/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.SystemSetting
 *  software.amazon.awssdk.utils.ToString
 */
package software.amazon.awssdk.auth.credentials;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.internal.SystemSettingsCredentialsProvider;
import software.amazon.awssdk.utils.SystemSetting;
import software.amazon.awssdk.utils.ToString;

@SdkPublicApi
public final class SystemPropertyCredentialsProvider
extends SystemSettingsCredentialsProvider {
    private SystemPropertyCredentialsProvider() {
    }

    public static SystemPropertyCredentialsProvider create() {
        return new SystemPropertyCredentialsProvider();
    }

    @Override
    protected Optional<String> loadSetting(SystemSetting setting) {
        return Optional.ofNullable(System.getProperty(setting.property()));
    }

    public String toString() {
        return ToString.create((String)"SystemPropertyCredentialsProvider");
    }
}

