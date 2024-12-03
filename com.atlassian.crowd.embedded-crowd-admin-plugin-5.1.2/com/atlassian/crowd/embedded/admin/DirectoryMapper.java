/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.embedded.admin;

import com.atlassian.crowd.embedded.admin.crowd.CrowdDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.delegatingldap.DelegatingLdapDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.internal.InternalDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.jirajdbc.JiraJdbcDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.ldap.LdapDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.ldap.SharedLdapDirectoryConfiguration;
import com.atlassian.crowd.embedded.api.Directory;

public interface DirectoryMapper {
    public Directory buildLdapDirectory(LdapDirectoryConfiguration var1);

    public LdapDirectoryConfiguration toLdapConfiguration(Directory var1);

    public Directory buildCrowdDirectory(CrowdDirectoryConfiguration var1);

    public CrowdDirectoryConfiguration toCrowdConfiguration(Directory var1);

    public Directory buildDelegatingLdapDirectory(DelegatingLdapDirectoryConfiguration var1);

    public DelegatingLdapDirectoryConfiguration toDelegatingLdapConfiguration(Directory var1);

    public Directory buildInternalDirectory(InternalDirectoryConfiguration var1);

    public InternalDirectoryConfiguration toInternalConfiguration(Directory var1);

    public Directory buildJiraJdbcDirectory(JiraJdbcDirectoryConfiguration var1);

    public JiraJdbcDirectoryConfiguration toJiraJdbcConfiguration(Directory var1);

    public <T extends SharedLdapDirectoryConfiguration> void setDefaultSpringLdapProperties(T var1);
}

