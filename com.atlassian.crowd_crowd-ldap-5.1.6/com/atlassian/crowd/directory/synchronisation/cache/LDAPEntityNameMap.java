/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.atlassian.crowd.directory.synchronisation.cache;

import com.atlassian.crowd.model.LDAPDirectoryEntity;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.Map;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

public class LDAPEntityNameMap<T extends LDAPDirectoryEntity> {
    @VisibleForTesting
    final Map<String, String> guidMap = Maps.newHashMap();
    @VisibleForTesting
    final Map<String, String> dnMap = Maps.newHashMap();
    @VisibleForTesting
    final Map<String, String> guidToDnMap = Maps.newHashMap();

    public void putAll(Collection<T> entities) {
        for (LDAPDirectoryEntity ldapEntity : entities) {
            this.put(ldapEntity);
        }
    }

    public void put(T ldapEntity) {
        String objectGUID = ldapEntity.getValue("objectGUID");
        String dn = this.guidToDnMap.get(objectGUID);
        if (dn != null) {
            this.dnMap.remove(dn);
        }
        this.guidToDnMap.put(objectGUID, ldapEntity.getDn());
        this.guidMap.put(objectGUID, ldapEntity.getName());
        this.dnMap.put(ldapEntity.getDn(), ldapEntity.getName());
    }

    public void removeByGuid(String guid) {
        this.guidMap.remove(guid);
        String dn = this.guidToDnMap.get(guid);
        this.dnMap.remove(dn);
        this.guidToDnMap.remove(guid);
    }

    public void removeAllByGuid(Collection<String> guids) {
        for (String guid : guids) {
            this.removeByGuid(guid);
        }
    }

    public String getByDn(String dn) {
        return this.dnMap.get(dn);
    }

    public String getByGuid(String guid) {
        return this.guidMap.get(guid);
    }

    public void clear() {
        this.guidMap.clear();
        this.dnMap.clear();
        this.guidToDnMap.clear();
    }

    @SuppressFBWarnings(value={"LDAP_INJECTION"}, justification="No user input")
    public Map<LdapName, String> toLdapNameKeyedMap() throws InvalidNameException {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Map.Entry<String, String> e : this.dnMap.entrySet()) {
            builder.put((Object)new LdapName(e.getKey()), (Object)e.getValue());
        }
        return builder.build();
    }
}

