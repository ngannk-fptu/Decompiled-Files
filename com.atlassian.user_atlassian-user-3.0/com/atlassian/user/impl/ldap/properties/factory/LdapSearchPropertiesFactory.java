/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.impl.ldap.properties.factory;

import com.atlassian.user.impl.ldap.properties.DefaultLdapSearchProperties;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import java.util.Properties;
import org.apache.log4j.Logger;

public class LdapSearchPropertiesFactory {
    protected final Logger log = Logger.getLogger(this.getClass());

    public LdapSearchProperties createInstance(Properties properties) {
        DefaultLdapSearchProperties result = new DefaultLdapSearchProperties();
        result.setBaseGroupNamespace(properties.getProperty("baseGroupNamespace"));
        result.setBaseUserNamespace(properties.getProperty("baseUserNamespace"));
        result.setEmailAttribute(properties.getProperty("emailAttribute"));
        result.setFirstnameAttribute(properties.getProperty("firstnameAttribute"));
        result.setGroupFilter(properties.getProperty("groupSearchFilter"));
        result.setGroupnameAttribute(properties.getProperty("groupnameAttribute"));
        String groupSearchScopeAllDepths = properties.getProperty("groupSearchAllDepths");
        result.setGroupSearchScopeAllDepths(Boolean.valueOf(groupSearchScopeAllDepths));
        result.setSurnameAttribute(properties.getProperty("surnameAttribute"));
        String timeLimitMillis = properties.getProperty("timeToLive");
        try {
            result.setTimeLimitMillis(Integer.parseInt(timeLimitMillis));
        }
        catch (NumberFormatException e) {
            this.log.warn((Object)"Error parsing LDAP time limit (in millis) in configuration file, using default value", (Throwable)e);
        }
        result.setUserFilter(properties.getProperty("userSearchFilter"));
        result.setUsernameAttribute(properties.getProperty("usernameAttribute"));
        String userSearchScopeAllDepths = properties.getProperty("userSearchAllDepths");
        result.setUserSearchScopeAllDepths(Boolean.valueOf(userSearchScopeAllDepths));
        return result;
    }
}

