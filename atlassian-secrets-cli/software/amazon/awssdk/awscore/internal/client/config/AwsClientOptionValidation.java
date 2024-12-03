/*
 * Decompiled with CFR 0.152.
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
        AwsClientOptionValidation.require("credentialsProvider", c.option(AwsClientOption.CREDENTIALS_IDENTITY_PROVIDER));
        AwsClientOptionValidation.require("overrideConfiguration.advancedOption[AWS_REGION]", c.option(AwsClientOption.AWS_REGION));
        AwsClientOptionValidation.require("overrideConfiguration.advancedOption[SIGNING_REGION]", c.option(AwsClientOption.SIGNING_REGION));
        AwsClientOptionValidation.require("overrideConfiguration.advancedOption[SERVICE_SIGNING_NAME]", c.option(AwsClientOption.SERVICE_SIGNING_NAME));
        AwsClientOptionValidation.require("overrideConfiguration.advancedOption[ENABLE_DEFAULT_REGION_DETECTION]", c.option(AwsAdvancedClientOption.ENABLE_DEFAULT_REGION_DETECTION));
    }
}

