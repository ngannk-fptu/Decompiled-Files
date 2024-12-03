/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.client.config.ClientOption
 *  software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
 *  software.amazon.awssdk.core.client.config.SdkAdvancedClientOption
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration$Builder
 *  software.amazon.awssdk.core.client.config.SdkClientOption
 *  software.amazon.awssdk.core.internal.SdkInternalTestAdvancedClientOption
 *  software.amazon.awssdk.core.signer.Signer
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.profiles.ProfileFileSupplier
 */
package software.amazon.awssdk.services.secretsmanager.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.internal.SdkInternalTestAdvancedClientOption;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSupplier;

@SdkInternalApi
public final class SdkClientConfigurationUtil {
    private SdkClientConfigurationUtil() {
    }

    public static SdkClientConfiguration.Builder copyOverridesToConfiguration(ClientOverrideConfiguration overrides, SdkClientConfiguration.Builder builder) {
        SdkClientConfigurationUtil.setClientOption(builder, SdkClientOption.ADDITIONAL_HTTP_HEADERS, overrides.headers());
        SdkClientConfigurationUtil.setClientOption(builder, SdkClientOption.RETRY_POLICY, overrides.retryPolicy());
        SdkClientConfigurationUtil.setClientOption(builder, SdkClientOption.API_CALL_TIMEOUT, overrides.apiCallTimeout());
        SdkClientConfigurationUtil.setClientOption(builder, SdkClientOption.API_CALL_ATTEMPT_TIMEOUT, overrides.apiCallAttemptTimeout());
        SdkClientConfigurationUtil.setClientOption(builder, SdkClientOption.SCHEDULED_EXECUTOR_SERVICE, overrides.scheduledExecutorService());
        SdkClientConfigurationUtil.setClientListOption(builder, SdkClientOption.EXECUTION_INTERCEPTORS, overrides.executionInterceptors());
        SdkClientConfigurationUtil.setClientOption(builder, SdkClientOption.EXECUTION_ATTRIBUTES, overrides.executionAttributes());
        Signer signer = overrides.advancedOption(SdkAdvancedClientOption.SIGNER).orElse(null);
        if (signer != null) {
            builder.option((ClientOption)SdkAdvancedClientOption.SIGNER, (Object)signer);
            builder.option((ClientOption)SdkClientOption.SIGNER_OVERRIDDEN, (Object)true);
        }
        SdkClientConfigurationUtil.setClientOption(builder, SdkAdvancedClientOption.USER_AGENT_SUFFIX, overrides.advancedOption(SdkAdvancedClientOption.USER_AGENT_SUFFIX));
        SdkClientConfigurationUtil.setClientOption(builder, SdkAdvancedClientOption.USER_AGENT_PREFIX, overrides.advancedOption(SdkAdvancedClientOption.USER_AGENT_PREFIX));
        SdkClientConfigurationUtil.setClientOption(builder, SdkAdvancedClientOption.DISABLE_HOST_PREFIX_INJECTION, overrides.advancedOption(SdkAdvancedClientOption.DISABLE_HOST_PREFIX_INJECTION));
        overrides.advancedOption((SdkAdvancedClientOption)SdkInternalTestAdvancedClientOption.ENDPOINT_OVERRIDDEN_OVERRIDE).ifPresent(value -> builder.option((ClientOption)SdkClientOption.ENDPOINT_OVERRIDDEN, value));
        ProfileFile profileFile = overrides.defaultProfileFile().orElse(null);
        if (profileFile != null) {
            builder.option((ClientOption)SdkClientOption.PROFILE_FILE_SUPPLIER, (Object)ProfileFileSupplier.fixedProfileFile((ProfileFile)profileFile));
        }
        SdkClientConfigurationUtil.setClientOption(builder, SdkClientOption.PROFILE_NAME, overrides.defaultProfileName());
        SdkClientConfigurationUtil.setClientListOption(builder, SdkClientOption.METRIC_PUBLISHERS, overrides.metricPublishers());
        SdkClientConfigurationUtil.setClientOption(builder, SdkAdvancedClientOption.TOKEN_SIGNER, overrides.advancedOption(SdkAdvancedClientOption.TOKEN_SIGNER));
        SdkClientConfigurationUtil.setClientOption(builder, SdkClientOption.COMPRESSION_CONFIGURATION, overrides.compressionConfiguration());
        return builder;
    }

    static <T> void setClientOption(SdkClientConfiguration.Builder builder, ClientOption<T> option, T newValue) {
        Object oldValue;
        if (!(newValue == null || (oldValue = builder.option(option)) != null && oldValue.equals(newValue))) {
            builder.option(option, newValue);
        }
    }

    static <T> void setClientOption(SdkClientConfiguration.Builder builder, ClientOption<T> option, Optional<T> newValueOpt) {
        SdkClientConfigurationUtil.setClientOption(builder, option, newValueOpt.orElse(null));
    }

    static <T> void setClientListOption(SdkClientConfiguration.Builder builder, ClientOption<List<T>> option, List<T> newValue) {
        if (newValue == null || newValue.isEmpty()) {
            return;
        }
        List oldValue = (List)builder.option(option);
        if (oldValue == null || oldValue.isEmpty()) {
            builder.option(option, newValue);
            return;
        }
        ArrayList<T> result = new ArrayList<T>(oldValue);
        HashSet dedup = new HashSet();
        dedup.addAll(oldValue);
        for (T value : newValue) {
            if (dedup.contains(value)) continue;
            result.add(value);
        }
        builder.option(option, result);
    }
}

