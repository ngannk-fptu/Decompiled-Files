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
import org.hibernate.boot.spi.MappingDefaults;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.MetadataSourceType;
import org.hibernate.dialect.function.SQLFunction;
import org.jboss.jandex.IndexView;

public interface MetadataBuildingOptions {
    public StandardServiceRegistry getServiceRegistry();

    public MappingDefaults getMappingDefaults();

    public List<BasicTypeRegistration> getBasicTypeRegistrations();

    @Deprecated
    public ReflectionManager getReflectionManager();

    @Deprecated
    public IndexView getJandexView();

    @Deprecated
    public ScanOptions getScanOptions();

    @Deprecated
    public ScanEnvironment getScanEnvironment();

    @Deprecated
    public Object getScanner();

    @Deprecated
    public ArchiveDescriptorFactory getArchiveDescriptorFactory();

    @Deprecated
    public ClassLoader getTempClassLoader();

    public ImplicitNamingStrategy getImplicitNamingStrategy();

    public PhysicalNamingStrategy getPhysicalNamingStrategy();

    public SharedCacheMode getSharedCacheMode();

    public AccessType getImplicitCacheAccessType();

    public MultiTenancyStrategy getMultiTenancyStrategy();

    public IdGeneratorStrategyInterpreter getIdGenerationTypeInterpreter();

    @Deprecated
    public List<CacheRegionDefinition> getCacheRegionDefinitions();

    public boolean ignoreExplicitDiscriminatorsForJoinedInheritance();

    public boolean createImplicitDiscriminatorsForJoinedInheritance();

    public boolean shouldImplicitlyForceDiscriminatorInSelect();

    public boolean useNationalizedCharacterData();

    public boolean isSpecjProprietarySyntaxEnabled();

    public boolean isNoConstraintByDefault();

    public List<MetadataSourceType> getSourceProcessOrdering();

    default public String getSchemaCharset() {
        return null;
    }

    default public boolean isXmlMappingEnabled() {
        return true;
    }

    @Deprecated
    public Map<String, SQLFunction> getSqlFunctions();

    @Deprecated
    public List<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectList();

    @Deprecated
    public List<AttributeConverterInfo> getAttributeConverters();
}

