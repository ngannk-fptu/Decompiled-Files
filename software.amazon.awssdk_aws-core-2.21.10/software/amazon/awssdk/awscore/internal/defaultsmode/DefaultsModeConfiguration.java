/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.client.config.SdkClientOption
 *  software.amazon.awssdk.core.retry.RetryMode
 *  software.amazon.awssdk.http.SdkHttpConfigurationOption
 *  software.amazon.awssdk.regions.ServiceMetadataAdvancedOption
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.AttributeMap$Key
 */
package software.amazon.awssdk.awscore.internal.defaultsmode;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.defaultsmode.DefaultsMode;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.regions.ServiceMetadataAdvancedOption;
import software.amazon.awssdk.utils.AttributeMap;

@SdkInternalApi
public final class DefaultsModeConfiguration {
    private static final AttributeMap STANDARD_DEFAULTS = AttributeMap.builder().put((AttributeMap.Key)SdkClientOption.DEFAULT_RETRY_MODE, (Object)RetryMode.STANDARD).put((AttributeMap.Key)ServiceMetadataAdvancedOption.DEFAULT_S3_US_EAST_1_REGIONAL_ENDPOINT, (Object)"regional").build();
    private static final AttributeMap STANDARD_HTTP_DEFAULTS = AttributeMap.builder().put((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_TIMEOUT, (Object)Duration.ofMillis(3100L)).put((AttributeMap.Key)SdkHttpConfigurationOption.TLS_NEGOTIATION_TIMEOUT, (Object)Duration.ofMillis(3100L)).build();
    private static final AttributeMap MOBILE_DEFAULTS = AttributeMap.builder().put((AttributeMap.Key)SdkClientOption.DEFAULT_RETRY_MODE, (Object)RetryMode.STANDARD).put((AttributeMap.Key)ServiceMetadataAdvancedOption.DEFAULT_S3_US_EAST_1_REGIONAL_ENDPOINT, (Object)"regional").build();
    private static final AttributeMap MOBILE_HTTP_DEFAULTS = AttributeMap.builder().put((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_TIMEOUT, (Object)Duration.ofMillis(30000L)).put((AttributeMap.Key)SdkHttpConfigurationOption.TLS_NEGOTIATION_TIMEOUT, (Object)Duration.ofMillis(30000L)).build();
    private static final AttributeMap CROSS_REGION_DEFAULTS = AttributeMap.builder().put((AttributeMap.Key)SdkClientOption.DEFAULT_RETRY_MODE, (Object)RetryMode.STANDARD).put((AttributeMap.Key)ServiceMetadataAdvancedOption.DEFAULT_S3_US_EAST_1_REGIONAL_ENDPOINT, (Object)"regional").build();
    private static final AttributeMap CROSS_REGION_HTTP_DEFAULTS = AttributeMap.builder().put((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_TIMEOUT, (Object)Duration.ofMillis(3100L)).put((AttributeMap.Key)SdkHttpConfigurationOption.TLS_NEGOTIATION_TIMEOUT, (Object)Duration.ofMillis(3100L)).build();
    private static final AttributeMap IN_REGION_DEFAULTS = AttributeMap.builder().put((AttributeMap.Key)SdkClientOption.DEFAULT_RETRY_MODE, (Object)RetryMode.STANDARD).put((AttributeMap.Key)ServiceMetadataAdvancedOption.DEFAULT_S3_US_EAST_1_REGIONAL_ENDPOINT, (Object)"regional").build();
    private static final AttributeMap IN_REGION_HTTP_DEFAULTS = AttributeMap.builder().put((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_TIMEOUT, (Object)Duration.ofMillis(1100L)).put((AttributeMap.Key)SdkHttpConfigurationOption.TLS_NEGOTIATION_TIMEOUT, (Object)Duration.ofMillis(1100L)).build();
    private static final AttributeMap LEGACY_DEFAULTS = AttributeMap.empty();
    private static final AttributeMap LEGACY_HTTP_DEFAULTS = AttributeMap.empty();
    private static final Map<DefaultsMode, AttributeMap> DEFAULT_CONFIG_BY_MODE = new EnumMap<DefaultsMode, AttributeMap>(DefaultsMode.class);
    private static final Map<DefaultsMode, AttributeMap> DEFAULT_HTTP_CONFIG_BY_MODE = new EnumMap<DefaultsMode, AttributeMap>(DefaultsMode.class);

    private DefaultsModeConfiguration() {
    }

    public static AttributeMap defaultConfig(DefaultsMode mode) {
        return DEFAULT_CONFIG_BY_MODE.getOrDefault((Object)mode, AttributeMap.empty());
    }

    public static AttributeMap defaultHttpConfig(DefaultsMode mode) {
        return DEFAULT_HTTP_CONFIG_BY_MODE.getOrDefault((Object)mode, AttributeMap.empty());
    }

    static {
        DEFAULT_CONFIG_BY_MODE.put(DefaultsMode.STANDARD, STANDARD_DEFAULTS);
        DEFAULT_CONFIG_BY_MODE.put(DefaultsMode.MOBILE, MOBILE_DEFAULTS);
        DEFAULT_CONFIG_BY_MODE.put(DefaultsMode.CROSS_REGION, CROSS_REGION_DEFAULTS);
        DEFAULT_CONFIG_BY_MODE.put(DefaultsMode.IN_REGION, IN_REGION_DEFAULTS);
        DEFAULT_CONFIG_BY_MODE.put(DefaultsMode.LEGACY, LEGACY_DEFAULTS);
        DEFAULT_HTTP_CONFIG_BY_MODE.put(DefaultsMode.STANDARD, STANDARD_HTTP_DEFAULTS);
        DEFAULT_HTTP_CONFIG_BY_MODE.put(DefaultsMode.MOBILE, MOBILE_HTTP_DEFAULTS);
        DEFAULT_HTTP_CONFIG_BY_MODE.put(DefaultsMode.CROSS_REGION, CROSS_REGION_HTTP_DEFAULTS);
        DEFAULT_HTTP_CONFIG_BY_MODE.put(DefaultsMode.IN_REGION, IN_REGION_HTTP_DEFAULTS);
        DEFAULT_HTTP_CONFIG_BY_MODE.put(DefaultsMode.LEGACY, LEGACY_HTTP_DEFAULTS);
    }
}

