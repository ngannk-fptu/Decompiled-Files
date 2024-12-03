/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.core.client.config.SdkClientOptionValidation
 */
package software.amazon.awssdk.awscore.internal.client.config;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.client.config.AwsAdvancedClientOption;
import software.amazon.awssdk.awscore.client.config.AwsClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOptionValidation;

@SdkInternalApi
public final class AwsClientOptionValidation
extends SdkClientOptionValidation {
    private AwsClientOptionValidation() {
    }

    public static void validateAsyncClientOptions(SdkClientConfiguration c) {
        AwsClientOptionValidation.validateClientOptions(c);
    }

    public static void validateSyncClientOptions(SdkClientConfiguration c) {
        AwsClientOptionValidation.validateClientOptions(c);
    }

    private static void validateClientOptions(SdkClientConfiguration c) {
        AwsClientOptionValidation.require((String)"credentialsProvider", (Object)c.option(AwsClientOption.CREDENTIALS_IDENTITY_PROVIDER));
        AwsClientOptionValidation.require((String)"overrideConfiguration.advancedOption[AWS_REGION]", (Object)c.option(AwsClientOption.AWS_REGION));
        AwsClientOptionValidation.require((String)"overrideConfiguration.advancedOption[SIGNING_REGION]", (Object)c.option(AwsClientOption.SIGNING_REGION));
        AwsClientOptionValidation.require((String)"overrideConfiguration.advancedOption[SERVICE_SIGNING_NAME]", (Object)c.option(AwsClientOption.SERVICE_SIGNING_NAME));
        AwsClientOptionValidation.require((String)"overrideConfiguration.advancedOption[ENABLE_DEFAULT_REGION_DETECTION]", (Object)c.option(AwsAdvancedClientOption.ENABLE_DEFAULT_REGION_DETECTION));
    }
}

