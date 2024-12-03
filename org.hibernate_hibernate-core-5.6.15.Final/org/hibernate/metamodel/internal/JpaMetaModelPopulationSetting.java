/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.metamodel.internal;

import java.util.Map;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.config.ConfigurationHelper;

public enum JpaMetaModelPopulationSetting {
    ENABLED,
    DISABLED,
    IGNORE_UNSUPPORTED;


    public static JpaMetaModelPopulationSetting parse(String setting) {
        if ("enabled".equalsIgnoreCase(setting)) {
            return ENABLED;
        }
        if ("disabled".equalsIgnoreCase(setting)) {
            return DISABLED;
        }
        return IGNORE_UNSUPPORTED;
    }

    public static JpaMetaModelPopulationSetting determineJpaMetaModelPopulationSetting(Map configurationValues) {
        String setting = ConfigurationHelper.getString("hibernate.ejb.metamodel.population", configurationValues, null);
        if (setting == null && (setting = ConfigurationHelper.getString("hibernate.ejb.metamodel.generation", configurationValues, null)) != null) {
            DeprecationLogger.DEPRECATION_LOGGER.deprecatedSetting("hibernate.ejb.metamodel.generation", "hibernate.ejb.metamodel.population");
        }
        return JpaMetaModelPopulationSetting.parse(setting);
    }
}

