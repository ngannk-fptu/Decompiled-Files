/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.ldap.util;

import com.atlassian.crowd.directory.ldap.LdapTypeConfig;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface LDAPPropertiesHelper {
    public Map<String, String> getImplementations();

    public Map<String, Properties> getConfigurationDetails();

    public List<LdapTypeConfig> getLdapTypeConfigs();
}

