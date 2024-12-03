/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.annotations.common.reflection.ReflectionManager
 *  org.jboss.jandex.IndexView
 */
package org.hibernate.boot.spi;

import java.util.Collection;
import java.util.Map;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.boot.AttributeConverterInfo;
import org.hibernate.boot.CacheRegionDefinition;
import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
import org.hibernate.boot.archive.scan.spi.ScanOptions;
import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
import org.hibernate.boot.internal.ClassmateContext;
import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.jpa.spi.MutableJpaCompliance;
import org.hibernate.type.spi.TypeConfiguration;
import org.jboss.jandex.IndexView;

public interface BootstrapContext {
    public StandardServiceRegistry getServiceRegistry();

    public MutableJpaCompliance getJpaCompliance();

    public TypeConfiguration getTypeConfiguration();

    public MetadataBuildingOptions getMetadataBuildingOptions();

    public boolean isJpaBootstrap();

    public void markAsJpaBootstrap();

    public ClassLoader getJpaTempClassLoader();

    public ClassLoaderAccess getClassLoaderAccess();

    public ClassmateContext getClassmateContext();

    public ArchiveDescriptorFactory getArchiveDescriptorFactory();

    public ScanOptions getScanOptions();

    public ScanEnvironment getScanEnvironment();

    public Object getScanner();

    @Deprecated
    public ReflectionManager getReflectionManager();

    public IndexView getJandexView();

    public Map<String, SQLFunction> getSqlFunctions();

    public Collection<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectList();

    public Collection<AttributeConverterInfo> getAttributeConverters();

    public Collection<CacheRegionDefinition> getCacheRegionDefinitions();

    public void release();
}

