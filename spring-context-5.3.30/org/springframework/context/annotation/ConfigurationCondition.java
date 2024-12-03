/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import org.springframework.context.annotation.Condition;

public interface ConfigurationCondition
extends Condition {
    public ConfigurationPhase getConfigurationPhase();

    public static enum ConfigurationPhase {
        PARSE_CONFIGURATION,
        REGISTER_BEAN;

    }
}

