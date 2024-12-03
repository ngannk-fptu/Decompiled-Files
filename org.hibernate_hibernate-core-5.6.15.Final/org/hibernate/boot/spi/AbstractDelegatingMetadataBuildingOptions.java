/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.SharedCacheMode
 *  org.hibernate.annotations.common.reflection.ReflectionManager
 *  org.jboss.jandex.IndexView
 */
package org.hibernate.boot.spi;

import java.util.List;
import java.util.Map;
import javax.persistence.SharedCacheMode;
import org.hibernate.HibernateException;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.boot.AttributeConverterInfo;
import org.hibernate.boot.CacheRegionDefinition;
import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
import org.hibernate.boot.archive.scan.spi.ScanOptions;
import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
import org.hibernate.boot.model.IdGeneratorStrategyInterpreter;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.BasicTypeRegistration;
import org.hibernate.boot.spi.JpaOrmXmlPersistenceUnitDefaultAware;
import org.hibernate.boot.spi.MappingDefaults;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.MetadataSourceType;
import org.hibernate.dialect.function.SQLFunction;
import org.jboss.jandex.IndexView;

public abstract class AbstractDelegatingMetadataBuildingOptions
implements MetadataBuildingOptions,
JpaOrmXmlPersistenceUnitDefaultAware {
    private final MetadataBuildingOptions delegate;

    public AbstractDelegatingMetadataBuildingOptions(MetadataBuildingOptions delegate) {
        this.delegate = delegate;
    }

    protected MetadataBuildingOptions delegate() {
        return this.delegate;
    }

    @Override
    public StandardServiceRegistry getServiceRegistry() {
        return this.delegate.getServiceRegistry();
    }

    @Override
    public MappingDefaults getMappingDefaults() {
        return this.delegate.getMappingDefaults();
    }

    @Override
    public List<BasicTypeRegistration> getBasicTypeRegistrations() {
        return this.delegate.getBasicTypeRegistrations();
    }

    @Override
    public IndexView getJandexView() {
        return this.delegate.getJandexView();
    }

    @Override
    public ScanOptions getScanOptions() {
        return this.delegate.getScanOptions();
    }

    @Override
    public ScanEnvironment getScanEnvironment() {
        return this.delegate.getScanEnvironment();
    }

    @Override
    public Object getScanner() {
        return this.delegate.getScanner();
    }

    @Override
    public ArchiveDescriptorFactory getArchiveDescriptorFactory() {
        return this.delegate.getArchiveDescriptorFactory();
    }

    @Override
    public ClassLoader getTempClassLoader() {
        return this.delegate.getTempClassLoader();
    }

    @Override
    public ImplicitNamingStrategy getImplicitNamingStrategy() {
        return this.delegate.getImplicitNamingStrategy();
    }

    @Override
    public PhysicalNamingStrategy getPhysicalNamingStrategy() {
        return this.delegate.getPhysicalNamingStrategy();
    }

    @Override
    public ReflectionManager getReflectionManager() {
        return this.delegate.getReflectionManager();
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return this.delegate.getSharedCacheMode();
    }

    @Override
    public AccessType getImplicitCacheAccessType() {
        return this.delegate.getImplicitCacheAccessType();
    }

    @Override
    public MultiTenancyStrategy getMultiTenancyStrategy() {
        return this.delegate.getMultiTenancyStrategy();
    }

    @Override
    public IdGeneratorStrategyInterpreter getIdGenerationTypeInterpreter() {
        return this.delegate.getIdGenerationTypeInterpreter();
    }

    @Override
    public List<CacheRegionDefinition> getCacheRegionDefinitions() {
        return this.delegate.getCacheRegionDefinitions();
    }

    @Override
    public boolean ignoreExplicitDiscriminatorsForJoinedInheritance() {
        return this.delegate.ignoreExplicitDiscriminatorsForJoinedInheritance();
    }

    @Override
    public boolean createImplicitDiscriminatorsForJoinedInheritance() {
        return this.delegate.createImplicitDiscriminatorsForJoinedInheritance();
    }

    @Override
    public boolean shouldImplicitlyForceDiscriminatorInSelect() {
        return this.delegate.shouldImplicitlyForceDiscriminatorInSelect();
    }

    @Override
    public boolean useNationalizedCharacterData() {
        return this.delegate.useNationalizedCharacterData();
    }

    @Override
    public boolean isSpecjProprietarySyntaxEnabled() {
        return this.delegate.isSpecjProprietarySyntaxEnabled();
    }

    @Override
    public boolean isNoConstraintByDefault() {
        return this.delegate.isNoConstraintByDefault();
    }

    @Override
    public List<MetadataSourceType> getSourceProcessOrdering() {
        return this.delegate.getSourceProcessOrdering();
    }

    @Override
    public Map<String, SQLFunction> getSqlFunctions() {
        return this.delegate.getSqlFunctions();
    }

    @Override
    public List<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectList() {
        return this.delegate.getAuxiliaryDatabaseObjectList();
    }

    @Override
    public List<AttributeConverterInfo> getAttributeConverters() {
        return this.delegate.getAttributeConverters();
    }

    @Override
    public void apply(JpaOrmXmlPersistenceUnitDefaultAware.JpaOrmXmlPersistenceUnitDefaults jpaOrmXmlPersistenceUnitDefaults) {
        if (!(this.delegate instanceof JpaOrmXmlPersistenceUnitDefaultAware)) {
            throw new HibernateException("AbstractDelegatingMetadataBuildingOptions delegate did not implement JpaOrmXmlPersistenceUnitDefaultAware; cannot delegate JpaOrmXmlPersistenceUnitDefaultAware#apply");
        }
        ((JpaOrmXmlPersistenceUnitDefaultAware)((Object)this.delegate)).apply(jpaOrmXmlPersistenceUnitDefaults);
    }

    @Override
    public String getSchemaCharset() {
        return this.delegate.getSchemaCharset();
    }

    @Override
    public boolean isXmlMappingEnabled() {
        return this.delegate.isXmlMappingEnabled();
    }
}

