/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.client.config;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.client.config.SdkAdvancedAsyncClientOption;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public class SdkClientOptionValidation {
    protected SdkClientOptionValidation() {
    }

    public static void validateAsyncClientOptions(SdkClientConfiguration c) {
        SdkClientOptionValidation.require("asyncConfiguration.advancedOption[FUTURE_COMPLETION_EXECUTOR]", c.option(SdkAdvancedAsyncClientOption.FUTURE_COMPLETION_EXECUTOR));
        SdkClientOptionValidation.require("asyncHttpClient", c.option(SdkClientOption.ASYNC_HTTP_CLIENT));
        SdkClientOptionValidation.validateClientOptions(c);
    }

    public static void validateSyncClientOptions(SdkClientConfiguration c) {
        SdkClientOptionValidation.require("syncHttpClient", c.option(SdkClientOption.SYNC_HTTP_CLIENT));
        SdkClientOptionValidation.validateClientOptions(c);
    }

    private static void validateClientOptions(SdkClientConfiguration c) {
        SdkClientOptionValidation.require("endpoint", c.option(SdkClientOption.ENDPOINT));
        SdkClientOptionValidation.require("overrideConfiguration.additionalHttpHeaders", c.option(SdkClientOption.ADDITIONAL_HTTP_HEADERS));
        SdkClientOptionValidation.require("overrideConfiguration.executionInterceptors", c.option(SdkClientOption.EXECUTION_INTERCEPTORS));
        SdkClientOptionValidation.require("overrideConfiguration.retryPolicy", c.option(SdkClientOption.RETRY_POLICY));
        SdkClientOptionValidation.require("overrideConfiguration.advancedOption[USER_AGENT_PREFIX]", c.option(SdkAdvancedClientOption.USER_AGENT_PREFIX));
        SdkClientOptionValidation.require("overrideConfiguration.advancedOption[USER_AGENT_SUFFIX]", c.option(SdkAdvancedClientOption.USER_AGENT_SUFFIX));
        SdkClientOptionValidation.require("overrideConfiguration.advancedOption[CRC32_FROM_COMPRESSED_DATA_ENABLED]", c.option(SdkClientOption.CRC32_FROM_COMPRESSED_DATA_ENABLED));
    }

    protected static <U> U require(String field, U required) {
        return Validate.notNull(required, "The '%s' must be configured in the client builder.", field);
    }
}

