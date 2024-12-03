/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 */
package com.atlassian.crowd.search.ldap.test;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.ldap.test.LDAPConfigurationTester;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.util.List;

public class LDAPConfigurationTesterImpl
implements LDAPConfigurationTester {
    @Override
    public boolean canFindLdapObjects(RemoteDirectory remoteDirectory, LDAPConfigurationTester.Strategy strategy) throws OperationFailedException {
        List<String> results = strategy.search(remoteDirectory.getAuthoritativeDirectory(), (EntityQuery<String>)QueryBuilder.queryFor(String.class, (EntityDescriptor)strategy.getEntityDescriptor()).returningAtMost(1));
        return !results.isEmpty();
    }
}

