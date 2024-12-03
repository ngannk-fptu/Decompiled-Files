/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  org.springframework.beans.factory.FactoryBean
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.List;
import org.springframework.beans.factory.FactoryBean;

@Deprecated(forRemoval=true)
public class ConfigurableMappingResources
implements FactoryBean {
    private final HibernateDatabaseCapabilities databaseCapabilities;
    private Iterable<String> mappings;
    private final Supplier<Iterable<String>> additionalMappings = Suppliers.memoize((Supplier)new Supplier<Iterable<String>>(){

        public Iterable<String> get() {
            ImmutableList.Builder mappings = ImmutableList.builder();
            if (ConfigurableMappingResources.this.databaseCapabilities.supportsIdentityColumns()) {
                mappings.add((Object)"com/atlassian/confluence/journal/JournalEntry-identity.hbm.xml");
            } else if (ConfigurableMappingResources.this.databaseCapabilities.supportsSequences() && ConfigurableMappingResources.this.databaseCapabilities.isOracle()) {
                mappings.add((Object)"com/atlassian/confluence/journal/JournalEntry-oracle.hbm.xml");
            } else if (ConfigurableMappingResources.this.databaseCapabilities.supportsSequences()) {
                mappings.add((Object)"com/atlassian/confluence/journal/JournalEntry-sequence.hbm.xml");
            } else {
                throw new IllegalStateException("The configured database supports neither identity nor the sequence generator");
            }
            mappings.add((Object)"com/atlassian/confluence/journal/JournalEntry-common.hbm.xml");
            mappings.add((Object)this.getDenormalisedSpaceChangeLogMapping());
            mappings.add((Object)this.getDenormalisedContentChangeLogMapping());
            return mappings.build();
        }

        private String getDenormalisedSpaceChangeLogMapping() {
            return ConfigurableMappingResources.this.databaseCapabilities.isOracle() ? "com/atlassian/confluence/security/denormalisedpermissions/DenormalisedSpaceChangeLogSequence.hbm.xml" : "com/atlassian/confluence/security/denormalisedpermissions/DenormalisedSpaceChangeLogIdentity.hbm.xml";
        }

        private String getDenormalisedContentChangeLogMapping() {
            return ConfigurableMappingResources.this.databaseCapabilities.isOracle() ? "com/atlassian/confluence/security/denormalisedpermissions/DenormalisedContentChangeLogSequence.hbm.xml" : "com/atlassian/confluence/security/denormalisedpermissions/DenormalisedContentChangeLogIdentity.hbm.xml";
        }
    });

    public ConfigurableMappingResources(HibernateDatabaseCapabilities databaseCapabilities) {
        this.databaseCapabilities = (HibernateDatabaseCapabilities)Preconditions.checkNotNull((Object)databaseCapabilities);
    }

    public void setMappings(List mappings) {
        this.mappings = mappings;
    }

    public Object getObject() throws Exception {
        return Iterables.concat(this.mappings, () -> ((Iterable)this.additionalMappings.get()).iterator());
    }

    public Class getObjectType() {
        return Iterable.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

