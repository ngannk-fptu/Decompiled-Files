/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.metamodel.internal;

import java.util.Map;
import org.hibernate.internal.util.config.ConfigurationHelper;

public enum JpaStaticMetaModelPopulationSetting {
    ENABLED,
    DISABLED,
    SKIP_UNSUPPORTED;


    public static JpaStaticMetaModelPopulationSetting parse(String setting) {
        if ("enabled".equalsIgnoreCase(setting)) {
            return ENABLED;
        }
        if ("disabled".equalsIgnoreCase(setting)) {
            return DISABLED;
        }
        return SKIP_UNSUPPORTED;
    }

    public static JpaStaticMetaModelPopulationSetting determineJpaMetaModelPopulationSetting(Map configurationValues) {
        String setting = ConfigurationHelper.getString("hibernate.jpa.static_metamodel.population", configurationValues, null);
        return JpaStaticMetaModelPopulationSetting.parse(setting);
    }
}

