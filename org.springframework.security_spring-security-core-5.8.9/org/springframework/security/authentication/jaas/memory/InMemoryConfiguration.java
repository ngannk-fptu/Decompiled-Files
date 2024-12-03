/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.authentication.jaas.memory;

import java.util.Collections;
import java.util.Map;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import org.springframework.util.Assert;

public class InMemoryConfiguration
extends Configuration {
    private final AppConfigurationEntry[] defaultConfiguration;
    private final Map<String, AppConfigurationEntry[]> mappedConfigurations;

    public InMemoryConfiguration(AppConfigurationEntry[] defaultConfiguration) {
        this(Collections.emptyMap(), defaultConfiguration);
    }

    public InMemoryConfiguration(Map<String, AppConfigurationEntry[]> mappedConfigurations) {
        this(mappedConfigurations, null);
    }

    public InMemoryConfiguration(Map<String, AppConfigurationEntry[]> mappedConfigurations, AppConfigurationEntry[] defaultConfiguration) {
        Assert.notNull(mappedConfigurations, (String)"mappedConfigurations cannot be null.");
        this.mappedConfigurations = mappedConfigurations;
        this.defaultConfiguration = defaultConfiguration;
    }

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        AppConfigurationEntry[] mappedResult = this.mappedConfigurations.get(name);
        return mappedResult != null ? mappedResult : this.defaultConfiguration;
    }

    @Override
    public void refresh() {
    }
}

