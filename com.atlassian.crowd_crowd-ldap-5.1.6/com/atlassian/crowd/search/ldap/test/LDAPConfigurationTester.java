/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 */
package com.atlassian.crowd.search.ldap.test;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.util.List;

public interface LDAPConfigurationTester {
    public boolean canFindLdapObjects(RemoteDirectory var1, Strategy var2) throws OperationFailedException;

    public static enum Strategy {
        USER{

            @Override
            EntityDescriptor getEntityDescriptor() {
                return EntityDescriptor.user();
            }

            @Override
            List<String> search(RemoteDirectory directory, EntityQuery<String> query) throws OperationFailedException {
                return directory.searchUsers(query);
            }
        }
        ,
        GROUP{

            @Override
            EntityDescriptor getEntityDescriptor() {
                return EntityDescriptor.group();
            }

            @Override
            List<String> search(RemoteDirectory directory, EntityQuery<String> query) throws OperationFailedException {
                return directory.searchGroups(query);
            }
        };


        abstract EntityDescriptor getEntityDescriptor();

        abstract List<String> search(RemoteDirectory var1, EntityQuery<String> var2) throws OperationFailedException;
    }
}

