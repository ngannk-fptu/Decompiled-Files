/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.cfg.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.cache.cfg.internal.CollectionDataCachingConfigImpl;
import org.hibernate.cache.cfg.internal.EntityDataCachingConfigImpl;
import org.hibernate.cache.cfg.internal.NaturalIdDataCachingConfigImpl;
import org.hibernate.cache.cfg.spi.CollectionDataCachingConfig;
import org.hibernate.cache.cfg.spi.DomainDataCachingConfig;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.cfg.spi.EntityDataCachingConfig;
import org.hibernate.cache.cfg.spi.NaturalIdDataCachingConfig;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.metamodel.model.domain.NavigableRole;
import org.hibernate.type.VersionType;

public class DomainDataRegionConfigImpl
implements DomainDataRegionConfig {
    private final String regionName;
    private final List<EntityDataCachingConfig> entityConfigs;
    private final List<NaturalIdDataCachingConfig> naturalIdConfigs;
    private final List<CollectionDataCachingConfig> collectionConfigs;

    private DomainDataRegionConfigImpl(String regionName, List<EntityDataCachingConfig> entityConfigs, List<NaturalIdDataCachingConfig> naturalIdConfigs, List<CollectionDataCachingConfig> collectionConfigs) {
        this.regionName = regionName;
        this.entityConfigs = entityConfigs;
        this.naturalIdConfigs = naturalIdConfigs;
        this.collectionConfigs = collectionConfigs;
    }

    @Override
    public String getRegionName() {
        return this.regionName;
    }

    @Override
    public List<EntityDataCachingConfig> getEntityCaching() {
        return this.entityConfigs;
    }

    @Override
    public List<NaturalIdDataCachingConfig> getNaturalIdCaching() {
        return this.naturalIdConfigs;
    }

    @Override
    public List<CollectionDataCachingConfig> getCollectionCaching() {
        return this.collectionConfigs;
    }

    public static class Builder {
        private final String regionName;
        private Map<NavigableRole, EntityDataCachingConfigImpl> entityConfigsByRootName;
        private List<NaturalIdDataCachingConfig> naturalIdConfigs;
        private List<CollectionDataCachingConfig> collectionConfigs;

        public Builder(String regionName) {
            this.regionName = regionName;
        }

        public Builder addEntityConfig(PersistentClass bootEntityDescriptor, AccessType accessType) {
            if (this.entityConfigsByRootName == null) {
                this.entityConfigsByRootName = new HashMap<NavigableRole, EntityDataCachingConfigImpl>();
            }
            NavigableRole rootEntityName = new NavigableRole(bootEntityDescriptor.getRootClass().getEntityName());
            EntityDataCachingConfigImpl entityDataCachingConfig = this.entityConfigsByRootName.computeIfAbsent(rootEntityName, x -> new EntityDataCachingConfigImpl(rootEntityName, bootEntityDescriptor.isVersioned() ? () -> ((VersionType)bootEntityDescriptor.getVersion().getType()).getComparator() : null, bootEntityDescriptor.isMutable(), accessType));
            if (bootEntityDescriptor == bootEntityDescriptor.getRootClass()) {
                entityDataCachingConfig.addCachedType(rootEntityName);
            } else {
                entityDataCachingConfig.addCachedType(new NavigableRole(bootEntityDescriptor.getEntityName()));
            }
            return this;
        }

        public Builder addNaturalIdConfig(RootClass rootEntityDescriptor, AccessType accessType) {
            if (this.naturalIdConfigs == null) {
                this.naturalIdConfigs = new ArrayList<NaturalIdDataCachingConfig>();
            }
            this.naturalIdConfigs.add(new NaturalIdDataCachingConfigImpl(rootEntityDescriptor, accessType));
            return this;
        }

        public Builder addCollectionConfig(Collection collectionDescriptor, AccessType accessType) {
            if (this.collectionConfigs == null) {
                this.collectionConfigs = new ArrayList<CollectionDataCachingConfig>();
            }
            this.collectionConfigs.add(new CollectionDataCachingConfigImpl(collectionDescriptor, accessType));
            return this;
        }

        public DomainDataRegionConfigImpl build() {
            return new DomainDataRegionConfigImpl(this.regionName, this.finalize(this.entityConfigsByRootName), this.finalize(this.naturalIdConfigs), this.finalize(this.collectionConfigs));
        }

        private <T extends DomainDataCachingConfig> List<T> finalize(Map configs) {
            return configs == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList(configs.values()));
        }

        private <T extends DomainDataCachingConfig> List<T> finalize(List<T> configs) {
            return configs == null ? Collections.emptyList() : Collections.unmodifiableList(configs);
        }
    }
}

