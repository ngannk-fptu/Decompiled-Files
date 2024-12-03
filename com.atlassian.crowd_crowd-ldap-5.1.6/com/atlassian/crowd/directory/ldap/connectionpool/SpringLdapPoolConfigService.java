/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.ldap.connectionpool;

import com.atlassian.crowd.directory.ldap.connectionpool.data.LdapPoolConfig;

public interface SpringLdapPoolConfigService {
    public void enrichByDefaultValues(LdapPoolConfig.Builder var1);

    public String toJsonLdapPoolConfig(LdapPoolConfig var1);

    public LdapPoolConfig toLdapPoolConfigDto(String var1);
}

