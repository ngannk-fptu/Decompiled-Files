/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AliasedDiscoveryConfig;
import com.hazelcast.config.AwsConfig;
import com.hazelcast.config.AzureConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.EurekaConfig;
import com.hazelcast.config.GcpConfig;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.KubernetesConfig;
import com.hazelcast.config.WanPublisherConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AliasedDiscoveryConfigUtils {
    private static final Map<String, String> ALIAS_MAPPINGS = new HashMap<String, String>();

    private AliasedDiscoveryConfigUtils() {
    }

    public static boolean supports(String tag) {
        return ALIAS_MAPPINGS.containsKey(tag);
    }

    public static String tagFor(AliasedDiscoveryConfig config) {
        return config.getTag();
    }

    public static List<DiscoveryStrategyConfig> createDiscoveryStrategyConfigs(JoinConfig config) {
        return AliasedDiscoveryConfigUtils.map(AliasedDiscoveryConfigUtils.aliasedDiscoveryConfigsFrom(config));
    }

    public static List<DiscoveryStrategyConfig> createDiscoveryStrategyConfigs(WanPublisherConfig config) {
        return AliasedDiscoveryConfigUtils.map(AliasedDiscoveryConfigUtils.aliasedDiscoveryConfigsFrom(config));
    }

    public static List<DiscoveryStrategyConfig> map(List<AliasedDiscoveryConfig<?>> aliasedDiscoveryConfigs) {
        ArrayList<DiscoveryStrategyConfig> result = new ArrayList<DiscoveryStrategyConfig>();
        for (AliasedDiscoveryConfig<?> config : aliasedDiscoveryConfigs) {
            if (!config.isEnabled()) continue;
            result.add(AliasedDiscoveryConfigUtils.createDiscoveryStrategyConfig(config));
        }
        return result;
    }

    private static DiscoveryStrategyConfig createDiscoveryStrategyConfig(AliasedDiscoveryConfig<?> config) {
        AliasedDiscoveryConfigUtils.validateConfig(config);
        String className = AliasedDiscoveryConfigUtils.discoveryStrategyFrom(config);
        HashMap<String, Comparable> properties = new HashMap<String, Comparable>();
        for (String key : config.getProperties().keySet()) {
            AliasedDiscoveryConfigUtils.putIfKeyNotNull(properties, key, config.getProperties().get(key));
        }
        return new DiscoveryStrategyConfig(className, properties);
    }

    private static void validateConfig(AliasedDiscoveryConfig config) {
        if (!ALIAS_MAPPINGS.containsKey(config.getTag())) {
            throw new InvalidConfigurationException(String.format("Invalid configuration class: '%s'", config.getClass().getName()));
        }
    }

    private static String discoveryStrategyFrom(AliasedDiscoveryConfig config) {
        return ALIAS_MAPPINGS.get(config.getTag());
    }

    private static void putIfKeyNotNull(Map<String, Comparable> properties, String key, String value) {
        if (key != null) {
            properties.put(key, (Comparable)((Object)value));
        }
    }

    public static AliasedDiscoveryConfig getConfigByTag(JoinConfig config, String tag) {
        if ("aws".equals(tag)) {
            return config.getAwsConfig();
        }
        if ("gcp".equals(tag)) {
            return config.getGcpConfig();
        }
        if ("azure".equals(tag)) {
            return config.getAzureConfig();
        }
        if ("kubernetes".equals(tag)) {
            return config.getKubernetesConfig();
        }
        if ("eureka".equals(tag)) {
            return config.getEurekaConfig();
        }
        throw new IllegalArgumentException(String.format("Invalid tag: '%s'", tag));
    }

    public static AliasedDiscoveryConfig getConfigByTag(WanPublisherConfig config, String tag) {
        if ("aws".equals(tag)) {
            return config.getAwsConfig();
        }
        if ("gcp".equals(tag)) {
            return config.getGcpConfig();
        }
        if ("azure".equals(tag)) {
            return config.getAzureConfig();
        }
        if ("kubernetes".equals(tag)) {
            return config.getKubernetesConfig();
        }
        if ("eureka".equals(tag)) {
            return config.getEurekaConfig();
        }
        throw new IllegalArgumentException(String.format("Invalid tag: '%s'", tag));
    }

    public static List<AliasedDiscoveryConfig<?>> aliasedDiscoveryConfigsFrom(JoinConfig config) {
        return Arrays.asList(config.getAwsConfig(), config.getGcpConfig(), config.getAzureConfig(), config.getKubernetesConfig(), config.getEurekaConfig());
    }

    public static List<AliasedDiscoveryConfig<?>> aliasedDiscoveryConfigsFrom(WanPublisherConfig config) {
        return Arrays.asList(config.getAwsConfig(), config.getGcpConfig(), config.getAzureConfig(), config.getKubernetesConfig(), config.getEurekaConfig());
    }

    public static boolean allUsePublicAddress(List<AliasedDiscoveryConfig<?>> configs) {
        boolean atLeastOneEnabled = false;
        for (AliasedDiscoveryConfig<?> config : configs) {
            if (!config.isEnabled()) continue;
            atLeastOneEnabled = true;
            if (config.isUsePublicIp()) continue;
            return false;
        }
        return atLeastOneEnabled;
    }

    public static AliasedDiscoveryConfig newConfigFor(String tag) {
        if ("aws".equals(tag)) {
            return new AwsConfig();
        }
        if ("gcp".equals(tag)) {
            return new GcpConfig();
        }
        if ("azure".equals(tag)) {
            return new AzureConfig();
        }
        if ("kubernetes".equals(tag)) {
            return new KubernetesConfig();
        }
        if ("eureka".equals(tag)) {
            return new EurekaConfig();
        }
        throw new IllegalArgumentException(String.format("Invalid tag: '%s'", tag));
    }

    static {
        ALIAS_MAPPINGS.put("aws", "com.hazelcast.aws.AwsDiscoveryStrategy");
        ALIAS_MAPPINGS.put("gcp", "com.hazelcast.gcp.GcpDiscoveryStrategy");
        ALIAS_MAPPINGS.put("azure", "com.hazelcast.azure.AzureDiscoveryStrategy");
        ALIAS_MAPPINGS.put("kubernetes", "com.hazelcast.kubernetes.HazelcastKubernetesDiscoveryStrategy");
        ALIAS_MAPPINGS.put("eureka", "com.hazelcast.eureka.one.EurekaOneDiscoveryStrategy");
    }
}

