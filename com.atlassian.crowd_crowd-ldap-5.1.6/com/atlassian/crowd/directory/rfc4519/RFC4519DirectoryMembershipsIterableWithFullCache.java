/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.rfc4519;

import com.atlassian.crowd.directory.RFC4519Directory;
import com.atlassian.crowd.directory.ldap.mapper.ContextMapperWithRequiredAttributes;
import com.atlassian.crowd.directory.rfc4519.RFC4519DirectoryMembershipsIterable;
import java.util.List;
import java.util.Map;
import javax.naming.ldap.LdapName;

public class RFC4519DirectoryMembershipsIterableWithFullCache
extends RFC4519DirectoryMembershipsIterable {
    RFC4519DirectoryMembershipsIterableWithFullCache(RFC4519Directory connector, Map<LdapName, String> users, Map<LdapName, String> groups, Map<LdapName, String> groupsToInclude, int membershipBatchSize, ContextMapperWithRequiredAttributes<LdapName> dnMapper) {
        super(connector, users, groups, groupsToInclude, membershipBatchSize, dnMapper);
    }

    @Override
    protected void lookupMissingNames(List<RFC4519DirectoryMembershipsIterable.MembershipHolder> memberships) {
    }
}

