/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerDriverStringProperty;
import com.microsoft.sqlserver.jdbc.Util;
import java.util.HashMap;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

public class JaasConfiguration
extends Configuration {
    private final Configuration delegate;
    private AppConfigurationEntry[] defaultValue;

    private static AppConfigurationEntry[] generateDefaultConfiguration() {
        if (Util.isIBM()) {
            HashMap<String, String> confDetailsWithoutPassword = new HashMap<String, String>();
            confDetailsWithoutPassword.put("useDefaultCcache", "true");
            HashMap confDetailsWithPassword = new HashMap();
            String ibmLoginModule = "com.ibm.security.auth.module.Krb5LoginModule";
            return new AppConfigurationEntry[]{new AppConfigurationEntry("com.ibm.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT, confDetailsWithoutPassword), new AppConfigurationEntry("com.ibm.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT, confDetailsWithPassword)};
        }
        HashMap<String, String> confDetails = new HashMap<String, String>();
        confDetails.put("useTicketCache", "true");
        return new AppConfigurationEntry[]{new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, confDetails)};
    }

    JaasConfiguration(Configuration delegate) {
        this.delegate = delegate;
        this.defaultValue = JaasConfiguration.generateDefaultConfiguration();
    }

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        AppConfigurationEntry[] conf;
        AppConfigurationEntry[] appConfigurationEntryArray = conf = this.delegate == null ? null : this.delegate.getAppConfigurationEntry(name);
        if (conf == null && name.equals(SQLServerDriverStringProperty.JAAS_CONFIG_NAME.getDefaultValue())) {
            return this.defaultValue;
        }
        return conf;
    }

    @Override
    public void refresh() {
        if (null != this.delegate) {
            this.delegate.refresh();
        }
    }
}

