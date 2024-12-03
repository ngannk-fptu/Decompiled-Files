/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigLoadingStrategy;
import com.typesafe.config.ConfigParseOptions;

public class DefaultConfigLoadingStrategy
implements ConfigLoadingStrategy {
    @Override
    public Config parseApplicationConfig(ConfigParseOptions parseOptions) {
        return ConfigFactory.parseApplicationReplacement(parseOptions).orElseGet(() -> ConfigFactory.parseResourcesAnySyntax("application", parseOptions));
    }
}

