/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.config;

import com.atlassian.seraph.config.ConfigurationException;
import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.config.SecurityConfigImpl;

public class SecurityConfigFactory {
    private static volatile SecurityConfig instance;

    public static SecurityConfig getInstance() {
        if (instance == null) {
            SecurityConfigFactory.loadInstance("seraph-config.xml");
        }
        return instance;
    }

    public static SecurityConfig getInstance(String configFileLocation) {
        if (instance == null) {
            SecurityConfigFactory.loadInstance(configFileLocation);
        }
        return instance;
    }

    public static void setSecurityConfig(SecurityConfig securityConfig) {
        instance = securityConfig;
    }

    private static synchronized void loadInstance(String configFileLocation) {
        if (instance == null) {
            try {
                instance = new SecurityConfigImpl(configFileLocation);
            }
            catch (ConfigurationException ex) {
                throw new RuntimeException("Could not load security config '" + configFileLocation + "': " + ex.getMessage(), ex);
            }
        }
    }

    private SecurityConfigFactory() {
    }
}

