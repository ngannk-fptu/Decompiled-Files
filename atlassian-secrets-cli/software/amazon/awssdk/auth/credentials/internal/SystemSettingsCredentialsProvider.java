/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.credentials.internal;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.SystemSetting;

@SdkInternalApi
public abstract class SystemSettingsCredentialsProvider
implements AwsCredentialsProvider {
    @Override
    public AwsCredentials resolveCredentials() {
        String accessKey = StringUtils.trim(this.loadSetting(SdkSystemSetting.AWS_ACCESS_KEY_ID).orElse(null));
        String secretKey = StringUtils.trim(this.loadSetting(SdkSystemSetting.AWS_SECRET_ACCESS_KEY).orElse(null));
        String sessionToken = StringUtils.trim(this.loadSetting(SdkSystemSetting.AWS_SESSION_TOKEN).orElse(null));
        if (StringUtils.isBlank(accessKey)) {
            throw SdkClientException.builder().message(String.format("Unable to load credentials from system settings. Access key must be specified either via environment variable (%s) or system property (%s).", SdkSystemSetting.AWS_ACCESS_KEY_ID.environmentVariable(), SdkSystemSetting.AWS_ACCESS_KEY_ID.property())).build();
        }
        if (StringUtils.isBlank(secretKey)) {
            throw SdkClientException.builder().message(String.format("Unable to load credentials from system settings. Secret key must be specified either via environment variable (%s) or system property (%s).", SdkSystemSetting.AWS_SECRET_ACCESS_KEY.environmentVariable(), SdkSystemSetting.AWS_SECRET_ACCESS_KEY.property())).build();
        }
        return StringUtils.isBlank(sessionToken) ? AwsBasicCredentials.create(accessKey, secretKey) : AwsSessionCredentials.create(accessKey, secretKey, sessionToken);
    }

    protected abstract Optional<String> loadSetting(SystemSetting var1);
}

