/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.spi.DcLicenseChecker
 *  org.springframework.ldap.core.ContextSource
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.LdapContextSourceFactory;
import com.atlassian.crowd.directory.SpringLdapPooledContextSourceProvider;
import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.directory.ldap.LdapPoolType;
import com.atlassian.crowd.embedded.spi.DcLicenseChecker;
import java.util.Map;
import org.springframework.ldap.core.ContextSource;

public class LdapContextSourceProvider {
    private final LdapContextSourceFactory ldapContextSourceFactory;
    private final SpringLdapPooledContextSourceProvider springLdapPooledContextSourceProvider;
    private DcLicenseChecker dcLicenseChecker;

    public LdapContextSourceProvider(LdapContextSourceFactory ldapContextSourceFactory, SpringLdapPooledContextSourceProvider springLdapPooledContextSourceProvider, DcLicenseChecker dcLicenseChecker) {
        this.ldapContextSourceFactory = ldapContextSourceFactory;
        this.springLdapPooledContextSourceProvider = springLdapPooledContextSourceProvider;
        this.dcLicenseChecker = dcLicenseChecker;
    }

    ContextSource createMinimalContextSource(String username, String password, LDAPPropertiesMapper ldapPropertiesMapper, Map<String, Object> envProperties) {
        return this.ldapContextSourceFactory.createMinimalContextSource(username, password, ldapPropertiesMapper, envProperties);
    }

    ContextSource getPooledContextSource(long directoryId, LDAPPropertiesMapper ldapPropertiesMapper, Map<String, Object> envProperties) {
        if (this.dcLicenseChecker.isDcLicense() && ldapPropertiesMapper.getLdapPoolType() == LdapPoolType.COMMONS_POOL2) {
            return this.springLdapPooledContextSourceProvider.getContextSource(directoryId, ldapPropertiesMapper, envProperties);
        }
        return this.ldapContextSourceFactory.createContextSource(ldapPropertiesMapper, envProperties, true);
    }

    ContextSource createContextSource(LDAPPropertiesMapper ldapPropertiesMapper, Map<String, Object> envProperties) {
        return this.ldapContextSourceFactory.createContextSource(ldapPropertiesMapper, envProperties, false);
    }
}

