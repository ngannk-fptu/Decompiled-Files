/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.ldap.core.DirContextAdapter
 */
package com.atlassian.crowd.directory.ldap.mapper;

import com.atlassian.crowd.directory.ldap.mapper.ContextMapperWithCustomAttributes;
import com.atlassian.crowd.directory.ldap.mapper.ContextMapperWithRequiredAttributes;
import com.atlassian.crowd.directory.ldap.mapper.attribute.ObjectGUIDMapper;
import com.atlassian.crowd.directory.ldap.mapper.attribute.USNChangedMapper;
import com.atlassian.crowd.model.Tombstone;
import java.util.NoSuchElementException;
import java.util.Set;
import org.springframework.ldap.core.DirContextAdapter;

public class TombstoneContextMapper
implements ContextMapperWithRequiredAttributes<Tombstone> {
    private final ObjectGUIDMapper objectGUIDMapper = new ObjectGUIDMapper();
    private final USNChangedMapper usnChangedMapper = new USNChangedMapper();

    @Override
    public Tombstone mapFromContext(Object ctx) {
        try {
            DirContextAdapter context = (DirContextAdapter)ctx;
            String guid = this.objectGUIDMapper.getValues(context).iterator().next();
            String usnChanged = this.usnChangedMapper.getValues(context).iterator().next();
            return new Tombstone(guid, usnChanged);
        }
        catch (NoSuchElementException e) {
            throw new RuntimeException("Could not retrieve objectGUID/uSNChanged due to missing attribute for object: " + ((DirContextAdapter)ctx).getDn());
        }
        catch (Exception e) {
            throw new RuntimeException("Could not retrieve objectGUID/uSNChanged from object: " + ((DirContextAdapter)ctx).getDn(), e);
        }
    }

    @Override
    public Set<String> getRequiredLdapAttributes() {
        return ContextMapperWithCustomAttributes.aggregate(this.objectGUIDMapper, this.usnChangedMapper);
    }
}

