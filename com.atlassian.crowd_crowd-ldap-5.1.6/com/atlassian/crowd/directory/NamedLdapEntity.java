/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.springframework.ldap.core.DirContextAdapter
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.ldap.mapper.ContextMapperWithRequiredAttributes;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import org.springframework.ldap.core.DirContextAdapter;

public class NamedLdapEntity {
    private final LdapName dn;
    private final String name;

    public NamedLdapEntity(LdapName dn, String name) {
        this.dn = (LdapName)Preconditions.checkNotNull((Object)dn, (Object)"DN may not be null");
        this.name = name;
    }

    public LdapName getDn() {
        return this.dn;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.dn.toString() + " = " + this.name;
    }

    static Iterable<String> namesOf(Iterable<? extends NamedLdapEntity> namedEntities) {
        return Iterables.transform(namedEntities, NamedLdapEntity::getName);
    }

    static List<String> namesOf(List<? extends NamedLdapEntity> namedEntities) {
        return namedEntities.stream().map(NamedLdapEntity::getName).collect(Collectors.toList());
    }

    static Iterable<LdapName> dnsOf(Iterable<? extends NamedLdapEntity> namedEntities) {
        return Iterables.transform(namedEntities, NamedLdapEntity::getDn);
    }

    static ContextMapperWithRequiredAttributes<NamedLdapEntity> mapperFromAttribute(String attrName) {
        return new NamedEntityMapper(attrName);
    }

    public static class NamedEntityMapper
    implements ContextMapperWithRequiredAttributes<NamedLdapEntity> {
        private final String nameAttribute;

        public NamedEntityMapper(String nameAttribute) {
            this.nameAttribute = (String)Preconditions.checkNotNull((Object)nameAttribute, (Object)"Attribute name may not be null");
        }

        @Override
        @SuppressFBWarnings(value={"LDAP_INJECTION"}, justification="No user input")
        public NamedLdapEntity mapFromContext(Object ctx) {
            LdapName dn;
            DirContextAdapter context = (DirContextAdapter)ctx;
            try {
                dn = new LdapName(context.getDn().toString());
            }
            catch (InvalidNameException e) {
                throw new RuntimeException(e);
            }
            return new NamedLdapEntity(dn, context.getStringAttribute(this.nameAttribute));
        }

        @Override
        public Set<String> getRequiredLdapAttributes() {
            return ImmutableSet.of((Object)this.nameAttribute);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            NamedEntityMapper that = (NamedEntityMapper)o;
            return this.nameAttribute.equals(that.nameAttribute);
        }

        public int hashCode() {
            return this.nameAttribute.hashCode();
        }
    }
}

