/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.config.util.BootstrapUtils
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.config.util.BootstrapUtils;

@Deprecated
public class UpgradeUtils {
    public static boolean isSqlServer() {
        return HibernateConfig.isSqlServerDialect((String)UpgradeUtils.lookUpDialect());
    }

    public static boolean isOracle() {
        return HibernateConfig.isOracleDialect((String)UpgradeUtils.lookUpDialect());
    }

    private static String lookUpDialect() {
        return (String)BootstrapUtils.getBootstrapManager().getProperty("hibernate.dialect");
    }
}

