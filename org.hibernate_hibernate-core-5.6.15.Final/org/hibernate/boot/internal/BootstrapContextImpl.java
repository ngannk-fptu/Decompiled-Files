/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.annotations.common.reflection.MetadataProvider
 *  org.hibernate.annotations.common.reflection.ReflectionManager
 *  org.hibernate.annotations.common.reflection.java.JavaReflectionManager
 *  org.jboss.jandex.IndexView
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.AssertionFailure;
import org.hibernate.annotations.common.reflection.MetadataProvider;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.boot.AttributeConverterInfo;
import org.hibernate.boot.CacheRegionDefinition;
import org.hibernate.boot.archive.scan.internal.StandardScanOptions;
import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
import org.hibernate.boot.archive.scan.spi.ScanOptions;
import org.hibernate.boot.archive.scan.spi.Scanner;
import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
import org.hibernate.boot.internal.ClassLoaderAccessImpl;
import org.hibernate.boot.internal.ClassmateContext;
import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.cfg.annotations.reflection.internal.JPAXMLOverriddenMetadataProvider;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.jpa.internal.MutableJpaComplianceImpl;
import org.hibernate.jpa.spi.MutableJpaCompliance;
import org.hibernate.type.spi.TypeConfiguration;
import org.jboss.jandex.IndexView;
import org.jboss.logging.Logger;

public class BootstrapContextImpl
implements BootstrapContext {
    private static final Logger log = Logger.getLogger(BootstrapContextImpl.class);
    private final StandardServiceRegistry serviceRegistry;
    private final MutableJpaCompliance jpaCompliance;
    private final TypeConfiguration typeConfiguration;
    private final ClassLoaderAccessImpl classLoaderAccess;
    private final JavaReflectionManager hcannReflectionManager;
    private final ClassmateContext classmateContext;
    private final MetadataBuildingOptions metadataBuildingOptions;
    private boolean isJpaBootstrap;
    private ScanOptions scanOptions;
    private ScanEnvironment scanEnvironment;
    private Object scannerSetting;
    private ArchiveDescriptorFactory archiveDescriptorFactory;
    private IndexView jandexView;
    private HashMap<String, SQLFunction> sqlFunctionMap;
    private ArrayList<AuxiliaryDatabaseObject> auxiliaryDatabaseObjectList;
    private HashMap<Class, AttributeConverterInfo> attributeConverterInfoMap;
    private ArrayList<CacheRegionDefinition> cacheRegionDefinitions;

    public BootstrapContextImpl(StandardServiceRegistry serviceRegistry, MetadataBuildingOptions metadataBuildingOptions) {
        this.serviceRegistry = serviceRegistry;
        this.classmateContext = new ClassmateContext();
        this.metadataBuildingOptions = metadataBuildingOptions;
        ClassLoaderService classLoaderService = serviceRegistry.getService(ClassLoaderService.class);
        this.classLoaderAccess = new ClassLoaderAccessImpl(classLoaderService);
        StrategySelector strategySelector = serviceRegistry.getService(StrategySelector.class);
        ConfigurationService configService = serviceRegistry.getService(ConfigurationService.class);
        this.hcannReflectionManager = this.generateHcannReflectionManager();
        this.jpaCompliance = new MutableJpaComplianceImpl(configService.getSettings(), false);
        this.scanOptions = new StandardScanOptions((String)configService.getSettings().get("hibernate.archive.autodetection"), false);
        this.scannerSetting = configService.getSettings().get("hibernate.archive.scanner");
        if (this.scannerSetting == null) {
            this.scannerSetting = configService.getSettings().get("hibernate.ejb.resource_scanner");
            if (this.scannerSetting != null) {
                DeprecationLogger.DEPRECATION_LOGGER.logDeprecatedScannerSetting();
            }
        }
        this.archiveDescriptorFactory = strategySelector.resolveStrategy(ArchiveDescriptorFactory.class, configService.getSettings().get("hibernate.archive.interpreter"));
        this.typeConfiguration = new TypeConfiguration();
    }

    @Override
    public StandardServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }

    @Override
    public MutableJpaCompliance getJpaCompliance() {
        return this.jpaCompliance;
    }

    @Override
    public TypeConfiguration getTypeConfiguration() {
        return this.typeConfiguration;
    }

    @Override
    public MetadataBuildingOptions getMetadataBuildingOptions() {
        return this.metadataBuildingOptions;
    }

    @Override
    public boolean isJpaBootstrap() {
        return this.isJpaBootstrap;
    }

    @Override
    public void markAsJpaBootstrap() {
        this.isJpaBootstrap = true;
    }

    @Override
    public ClassLoader getJpaTempClassLoader() {
        return this.classLoaderAccess.getJpaTempClassLoader();
    }

    @Override
    public ClassLoaderAccess getClassLoaderAccess() {
        return this.classLoaderAccess;
    }

    @Override
    public ClassmateContext getClassmateContext() {
        return this.classmateContext;
    }

    @Override
    public ArchiveDescriptorFactory getArchiveDescriptorFactory() {
        return this.archiveDescriptorFactory;
    }

    @Override
    public ScanOptions getScanOptions() {
        return this.scanOptions;
    }

    @Override
    public ScanEnvironment getScanEnvironment() {
        return this.scanEnvironment;
    }

    @Override
    public Object getScanner() {
        return this.scannerSetting;
    }

    @Override
    public ReflectionManager getReflectionManager() {
        return this.hcannReflectionManager;
    }

    @Override
    public IndexView getJandexView() {
        return this.jandexView;
    }

    @Override
    public Map<String, SQLFunction> getSqlFunctions() {
        return this.sqlFunctionMap == null ? Collections.emptyMap() : this.sqlFunctionMap;
    }

    @Override
    public Collection<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectList() {
        return this.auxiliaryDatabaseObjectList == null ? Collections.emptyList() : this.auxiliaryDatabaseObjectList;
    }

    @Override
    public Collection<AttributeConverterInfo> getAttributeConverters() {
        return this.attributeConverterInfoMap != null ? new ArrayList<AttributeConverterInfo>(this.attributeConverterInfoMap.values()) : Collections.emptyList();
    }

    @Override
    public Collection<CacheRegionDefinition> getCacheRegionDefinitions() {
        return this.cacheRegionDefinitions == null ? Collections.emptyList() : this.cacheRegionDefinitions;
    }

    @Override
    public void release() {
        this.classmateContext.release();
        this.classLoaderAccess.release();
        this.scanOptions = null;
        this.scanEnvironment = null;
        this.scannerSetting = null;
        this.archiveDescriptorFactory = null;
        this.jandexView = null;
        if (this.sqlFunctionMap != null) {
            this.sqlFunctionMap.clear();
        }
        if (this.auxiliaryDatabaseObjectList != null) {
            this.auxiliaryDatabaseObjectList.clear();
        }
        if (this.attributeConverterInfoMap != null) {
            this.attributeConverterInfoMap.clear();
        }
        if (this.cacheRegionDefinitions != null) {
            this.cacheRegionDefinitions.clear();
        }
    }

    public void addAttributeConverterInfo(AttributeConverterInfo info) {
        AttributeConverterInfo old;
        if (this.attributeConverterInfoMap == null) {
            this.attributeConverterInfoMap = new HashMap();
        }
        if ((old = this.attributeConverterInfoMap.put(info.getConverterClass(), info)) != null) {
            throw new AssertionFailure(String.format("AttributeConverter class [%s] registered multiple times", info.getConverterClass()));
        }
    }

    void injectJpaTempClassLoader(ClassLoader jpaTempClassLoader) {
        log.debugf("Injecting JPA temp ClassLoader [%s] into BootstrapContext; was [%s]", (Object)jpaTempClassLoader, (Object)this.getJpaTempClassLoader());
        this.classLoaderAccess.injectTempClassLoader(jpaTempClassLoader);
    }

    void injectScanOptions(ScanOptions scanOptions) {
        log.debugf("Injecting ScanOptions [%s] into BootstrapContext; was [%s]", (Object)scanOptions, (Object)this.scanOptions);
        this.scanOptions = scanOptions;
    }

    void injectScanEnvironment(ScanEnvironment scanEnvironment) {
        log.debugf("Injecting ScanEnvironment [%s] into BootstrapContext; was [%s]", (Object)scanEnvironment, (Object)this.scanEnvironment);
        this.scanEnvironment = scanEnvironment;
    }

    void injectScanner(Scanner scanner) {
        log.debugf("Injecting Scanner [%s] into BootstrapContext; was [%s]", (Object)scanner, this.scannerSetting);
        this.scannerSetting = scanner;
    }

    void injectArchiveDescriptorFactory(ArchiveDescriptorFactory factory) {
        log.debugf("Injecting ArchiveDescriptorFactory [%s] into BootstrapContext; was [%s]", (Object)factory, (Object)this.archiveDescriptorFactory);
        this.archiveDescriptorFactory = factory;
    }

    void injectJandexView(IndexView jandexView) {
        log.debugf("Injecting Jandex IndexView [%s] into BootstrapContext; was [%s]", (Object)jandexView, (Object)this.jandexView);
        this.jandexView = jandexView;
    }

    public void addSqlFunction(String functionName, SQLFunction function) {
        if (this.sqlFunctionMap == null) {
            this.sqlFunctionMap = new HashMap();
        }
        this.sqlFunctionMap.put(functionName, function);
    }

    public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
        if (this.auxiliaryDatabaseObjectList == null) {
            this.auxiliaryDatabaseObjectList = new ArrayList();
        }
        this.auxiliaryDatabaseObjectList.add(auxiliaryDatabaseObject);
    }

    public void addCacheRegionDefinition(CacheRegionDefinition cacheRegionDefinition) {
        if (this.cacheRegionDefinitions == null) {
            this.cacheRegionDefinitions = new ArrayList();
        }
        this.cacheRegionDefinitions.add(cacheRegionDefinition);
    }

    private JavaReflectionManager generateHcannReflectionManager() {
        JavaReflectionManager reflectionManager = new JavaReflectionManager();
        reflectionManager.setMetadataProvider((MetadataProvider)new JPAXMLOverriddenMetadataProvider(this));
        return reflectionManager;
    }
}

