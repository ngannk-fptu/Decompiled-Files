/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.rfc4519;

import com.atlassian.crowd.directory.RFC4519Directory;
import com.atlassian.crowd.directory.ldap.mapper.ContextMapperWithRequiredAttributes;
import com.atlassian.crowd.directory.rfc4519.RFC4519DirectoryMembershipsIterable;
import com.atlassian.crowd.directory.rfc4519.RFC4519DirectoryMembershipsIterableWithFullCache;
import com.atlassian.crowd.model.LDAPDirectoryEntity;
import com.atlassian.crowd.model.group.LDAPGroupWithAttributes;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.naming.ldap.LdapName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RFC4519DirectoryMembershipsIterableBuilder {
    public static final int PARTITION_SIZE = Integer.getInteger("com.atlassian.crowd.directory.RFC4519DirectoryMembershipsIterable.PARTITION_SIZE", 1000);
    static final String FORCE_LOOKUP_MISSING_NAMES_PROPERTY = "com.atlassian.crowd.directory.RFC4519DirectoryMembershipsIterable.FORCE_LOOKUP_MISSING_NAMES";
    private static final boolean FORCE_LOOKUP_MISSING_NAMES = Boolean.getBoolean("com.atlassian.crowd.directory.RFC4519DirectoryMembershipsIterable.FORCE_LOOKUP_MISSING_NAMES");
    private static final Logger log = LoggerFactory.getLogger(RFC4519DirectoryMembershipsIterableBuilder.class);
    private RFC4519Directory connector;
    private Map<LdapName, String> users;
    private Map<LdapName, String> groups;
    private boolean fullCache;
    private Map<LdapName, String> groupsToInclude;
    private Integer membershipBatchSize;
    private ContextMapperWithRequiredAttributes<LdapName> dnMapper = RFC4519Directory.DN_MAPPER;

    public RFC4519DirectoryMembershipsIterable build() {
        int batchSize;
        Map<LdapName, String> groupsToInclude = this.groupsToInclude == null ? this.groups : this.groupsToInclude;
        int n = batchSize = this.membershipBatchSize == null ? PARTITION_SIZE : this.membershipBatchSize;
        if (this.fullCache) {
            return new RFC4519DirectoryMembershipsIterableWithFullCache(this.connector, this.users, this.groups, groupsToInclude, batchSize, this.dnMapper);
        }
        return new RFC4519DirectoryMembershipsIterable(this.connector, this.users, this.groups, groupsToInclude, batchSize, this.dnMapper);
    }

    public RFC4519DirectoryMembershipsIterableBuilder withDnMapper(ContextMapperWithRequiredAttributes<LdapName> dnMapper) {
        this.dnMapper = dnMapper;
        return this;
    }

    public RFC4519DirectoryMembershipsIterableBuilder forConnector(RFC4519Directory connector) {
        this.connector = connector;
        return this;
    }

    public RFC4519DirectoryMembershipsIterableBuilder withFullCache(Map<LdapName, String> users, Map<LdapName, String> groups) {
        if (FORCE_LOOKUP_MISSING_NAMES) {
            RFC4519DirectoryMembershipsIterableBuilder.logForceLookupEnabled();
        } else {
            this.fullCache = true;
        }
        this.users = users;
        this.groups = groups;
        return this;
    }

    public RFC4519DirectoryMembershipsIterableBuilder withPartialCache(Map<LdapName, String> users, Map<LdapName, String> groups) {
        this.fullCache = false;
        this.users = users;
        this.groups = groups;
        return this;
    }

    public RFC4519DirectoryMembershipsIterableBuilder forGroups(Map<LdapName, String> groupsToInclude) {
        this.groupsToInclude = groupsToInclude;
        return this;
    }

    public RFC4519DirectoryMembershipsIterableBuilder forGroups(Collection<LDAPGroupWithAttributes> groupsToInclude) {
        this.groupsToInclude = groupsToInclude.stream().collect(Collectors.toMap(LDAPDirectoryEntity::getLdapName, LDAPGroupWithAttributes::getName));
        return this;
    }

    public RFC4519DirectoryMembershipsIterableBuilder withCustomBatchSize(int membershipBatchSize) {
        this.membershipBatchSize = membershipBatchSize;
        return this;
    }

    private static void logForceLookupEnabled() {
        log.debug("Returning a RFC4519 memberships iterable with forced lookups as the {} property is enabled", (Object)FORCE_LOOKUP_MISSING_NAMES_PROPERTY);
    }
}

