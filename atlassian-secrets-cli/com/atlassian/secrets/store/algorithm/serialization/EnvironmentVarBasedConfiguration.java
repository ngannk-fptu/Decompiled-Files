/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.store.algorithm.serialization;

import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentVarBasedConfiguration {
    private static final Logger log = LoggerFactory.getLogger(EnvironmentVarBasedConfiguration.class);
    private static final String ENV_VARIABLE_PREFIX = "com_atlassian_db_config_password_ciphers_algorithm_";
    private final Function<String, String> getSystemEnv;
    private final String objectClassName;

    public EnvironmentVarBasedConfiguration(String objectClassName, Function<String, String> getSystemEnv) {
        this.getSystemEnv = getSystemEnv;
        this.objectClassName = objectClassName;
    }

    public String getFromEnv() {
        String envVarName = (ENV_VARIABLE_PREFIX + this.objectClassName).replace(".", "_");
        log.debug("Will try to read file path from environment variable under: {}", (Object)envVarName);
        String path = this.getSystemEnv.apply(envVarName);
        if (path == null) {
            log.debug("Nothing found under environment variable.");
        }
        return path;
    }
}

