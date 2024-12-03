/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Converter
 *  javax.persistence.Embeddable
 *  javax.persistence.Entity
 *  javax.persistence.MappedSuperclass
 *  javax.persistence.PersistenceException
 *  javax.persistence.SharedCacheMode
 *  javax.persistence.ValidationMode
 *  javax.persistence.spi.PersistenceUnitInfo
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.ResourceLoaderAware
 *  org.springframework.context.index.CandidateComponentsIndex
 *  org.springframework.context.index.CandidateComponentsIndexLoader
 *  org.springframework.context.weaving.LoadTimeWeaverAware
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 *  org.springframework.core.io.support.ResourcePatternResolver
 *  org.springframework.core.io.support.ResourcePatternUtils
 *  org.springframework.core.type.classreading.CachingMetadataReaderFactory
 *  org.springframework.core.type.classreading.MetadataReader
 *  org.springframework.core.type.classreading.MetadataReaderFactory
 *  org.springframework.core.type.filter.AnnotationTypeFilter
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver
 *  org.springframework.instrument.classloading.LoadTimeWeaver
 *  org.springframework.jdbc.datasource.lookup.DataSourceLookup
 *  org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup
 *  org.springframework.jdbc.datasource.lookup.MapDataSourceLookup
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ResourceUtils
 */
package org.springframework.orm.jpa.persistenceunit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Converter;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceException;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.index.CandidateComponentsIndex;
import org.springframework.context.index.CandidateComponentsIndexLoader;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitReader;
import org.springframework.orm.jpa.persistenceunit.SpringPersistenceUnitInfo;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;

public class DefaultPersistenceUnitManager
implements PersistenceUnitManager,
ResourceLoaderAware,
LoadTimeWeaverAware,
InitializingBean {
    private static final String CLASS_RESOURCE_PATTERN = "/**/*.class";
    private static final String PACKAGE_INFO_SUFFIX = ".package-info";
    private static final String DEFAULT_ORM_XML_RESOURCE = "META-INF/orm.xml";
    private static final String PERSISTENCE_XML_FILENAME = "persistence.xml";
    public static final String DEFAULT_PERSISTENCE_XML_LOCATION = "classpath*:META-INF/persistence.xml";
    public static final String ORIGINAL_DEFAULT_PERSISTENCE_UNIT_ROOT_LOCATION = "classpath:";
    public static final String ORIGINAL_DEFAULT_PERSISTENCE_UNIT_NAME = "default";
    private static final Set<AnnotationTypeFilter> entityTypeFilters = new LinkedHashSet<AnnotationTypeFilter>(8);
    protected final Log logger = LogFactory.getLog(this.getClass());
    private String[] persistenceXmlLocations = new String[]{"classpath*:META-INF/persistence.xml"};
    @Nullable
    private String defaultPersistenceUnitRootLocation = "classpath:";
    @Nullable
    private String defaultPersistenceUnitName = "default";
    @Nullable
    private String[] packagesToScan;
    @Nullable
    private String[] mappingResources;
    @Nullable
    private SharedCacheMode sharedCacheMode;
    @Nullable
    private ValidationMode validationMode;
    private DataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
    @Nullable
    private DataSource defaultDataSource;
    @Nullable
    private DataSource defaultJtaDataSource;
    @Nullable
    private PersistenceUnitPostProcessor[] persistenceUnitPostProcessors;
    @Nullable
    private LoadTimeWeaver loadTimeWeaver;
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    @Nullable
    private CandidateComponentsIndex componentsIndex;
    private final Set<String> persistenceUnitInfoNames = new HashSet<String>();
    private final Map<String, PersistenceUnitInfo> persistenceUnitInfos = new HashMap<String, PersistenceUnitInfo>();

    public void setPersistenceXmlLocation(String persistenceXmlLocation) {
        this.persistenceXmlLocations = new String[]{persistenceXmlLocation};
    }

    public void setPersistenceXmlLocations(String ... persistenceXmlLocations) {
        this.persistenceXmlLocations = persistenceXmlLocations;
    }

    public void setDefaultPersistenceUnitRootLocation(String defaultPersistenceUnitRootLocation) {
        this.defaultPersistenceUnitRootLocation = defaultPersistenceUnitRootLocation;
    }

    public void setDefaultPersistenceUnitName(String defaultPersistenceUnitName) {
        this.defaultPersistenceUnitName = defaultPersistenceUnitName;
    }

    public void setPackagesToScan(String ... packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public void setMappingResources(String ... mappingResources) {
        this.mappingResources = mappingResources;
    }

    public void setSharedCacheMode(SharedCacheMode sharedCacheMode) {
        this.sharedCacheMode = sharedCacheMode;
    }

    public void setValidationMode(ValidationMode validationMode) {
        this.validationMode = validationMode;
    }

    public void setDataSources(Map<String, DataSource> dataSources) {
        this.dataSourceLookup = new MapDataSourceLookup(dataSources);
    }

    public void setDataSourceLookup(@Nullable DataSourceLookup dataSourceLookup) {
        this.dataSourceLookup = dataSourceLookup != null ? dataSourceLookup : new JndiDataSourceLookup();
    }

    @Nullable
    public DataSourceLookup getDataSourceLookup() {
        return this.dataSourceLookup;
    }

    public void setDefaultDataSource(@Nullable DataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    @Nullable
    public DataSource getDefaultDataSource() {
        return this.defaultDataSource;
    }

    public void setDefaultJtaDataSource(@Nullable DataSource defaultJtaDataSource) {
        this.defaultJtaDataSource = defaultJtaDataSource;
    }

    @Nullable
    public DataSource getDefaultJtaDataSource() {
        return this.defaultJtaDataSource;
    }

    public void setPersistenceUnitPostProcessors(PersistenceUnitPostProcessor ... postProcessors) {
        this.persistenceUnitPostProcessors = postProcessors;
    }

    @Nullable
    public PersistenceUnitPostProcessor[] getPersistenceUnitPostProcessors() {
        return this.persistenceUnitPostProcessors;
    }

    public void setLoadTimeWeaver(@Nullable LoadTimeWeaver loadTimeWeaver) {
        this.loadTimeWeaver = loadTimeWeaver;
    }

    @Nullable
    public LoadTimeWeaver getLoadTimeWeaver() {
        return this.loadTimeWeaver;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver((ResourceLoader)resourceLoader);
        this.componentsIndex = CandidateComponentsIndexLoader.loadIndex((ClassLoader)resourceLoader.getClassLoader());
    }

    public void afterPropertiesSet() {
        if (this.loadTimeWeaver == null && InstrumentationLoadTimeWeaver.isInstrumentationAvailable()) {
            this.loadTimeWeaver = new InstrumentationLoadTimeWeaver(this.resourcePatternResolver.getClassLoader());
        }
        this.preparePersistenceUnitInfos();
    }

    public void preparePersistenceUnitInfos() {
        this.persistenceUnitInfoNames.clear();
        this.persistenceUnitInfos.clear();
        List<SpringPersistenceUnitInfo> puis = this.readPersistenceUnitInfos();
        for (SpringPersistenceUnitInfo pui : puis) {
            if (pui.getPersistenceUnitRootUrl() == null) {
                pui.setPersistenceUnitRootUrl(this.determineDefaultPersistenceUnitRootUrl());
            }
            if (pui.getJtaDataSource() == null && this.defaultJtaDataSource != null) {
                pui.setJtaDataSource(this.defaultJtaDataSource);
            }
            if (pui.getNonJtaDataSource() == null && this.defaultDataSource != null) {
                pui.setNonJtaDataSource(this.defaultDataSource);
            }
            if (this.sharedCacheMode != null) {
                pui.setSharedCacheMode(this.sharedCacheMode);
            }
            if (this.validationMode != null) {
                pui.setValidationMode(this.validationMode);
            }
            if (this.loadTimeWeaver != null) {
                pui.init(this.loadTimeWeaver);
            } else {
                pui.init(this.resourcePatternResolver.getClassLoader());
            }
            this.postProcessPersistenceUnitInfo(pui);
            String name = pui.getPersistenceUnitName();
            if (!this.persistenceUnitInfoNames.add(name) && !this.isPersistenceUnitOverrideAllowed()) {
                StringBuilder msg = new StringBuilder();
                msg.append("Conflicting persistence unit definitions for name '").append(name).append("': ");
                msg.append(pui.getPersistenceUnitRootUrl()).append(", ");
                msg.append(this.persistenceUnitInfos.get(name).getPersistenceUnitRootUrl());
                throw new IllegalStateException(msg.toString());
            }
            this.persistenceUnitInfos.put(name, pui);
        }
    }

    private List<SpringPersistenceUnitInfo> readPersistenceUnitInfos() {
        SpringPersistenceUnitInfo[] readInfos;
        ArrayList<SpringPersistenceUnitInfo> infos = new ArrayList<SpringPersistenceUnitInfo>(1);
        String defaultName = this.defaultPersistenceUnitName;
        boolean buildDefaultUnit = this.packagesToScan != null || this.mappingResources != null;
        boolean foundDefaultUnit = false;
        PersistenceUnitReader reader = new PersistenceUnitReader(this.resourcePatternResolver, this.dataSourceLookup);
        for (SpringPersistenceUnitInfo readInfo : readInfos = reader.readPersistenceUnitInfos(this.persistenceXmlLocations)) {
            infos.add(readInfo);
            if (defaultName == null || !defaultName.equals(readInfo.getPersistenceUnitName())) continue;
            foundDefaultUnit = true;
        }
        if (buildDefaultUnit) {
            if (foundDefaultUnit) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn((Object)("Found explicit default persistence unit with name '" + defaultName + "' in persistence.xml - overriding local default persistence unit settings ('packagesToScan'/'mappingResources')"));
                }
            } else {
                infos.add(this.buildDefaultPersistenceUnitInfo());
            }
        }
        return infos;
    }

    private SpringPersistenceUnitInfo buildDefaultPersistenceUnitInfo() {
        SpringPersistenceUnitInfo scannedUnit;
        block8: {
            block7: {
                scannedUnit = new SpringPersistenceUnitInfo();
                if (this.defaultPersistenceUnitName != null) {
                    scannedUnit.setPersistenceUnitName(this.defaultPersistenceUnitName);
                }
                scannedUnit.setExcludeUnlistedClasses(true);
                if (this.packagesToScan != null) {
                    for (String pkg : this.packagesToScan) {
                        this.scanPackage(scannedUnit, pkg);
                    }
                }
                if (this.mappingResources == null) break block7;
                for (String mappingFileName : this.mappingResources) {
                    scannedUnit.addMappingFileName(mappingFileName);
                }
                break block8;
            }
            Resource ormXml = this.getOrmXmlForDefaultPersistenceUnit();
            if (ormXml == null) break block8;
            scannedUnit.addMappingFileName(DEFAULT_ORM_XML_RESOURCE);
            if (scannedUnit.getPersistenceUnitRootUrl() == null) {
                try {
                    scannedUnit.setPersistenceUnitRootUrl(PersistenceUnitReader.determinePersistenceUnitRootUrl(ormXml));
                }
                catch (IOException ex) {
                    this.logger.debug((Object)"Failed to determine persistence unit root URL from orm.xml location", (Throwable)ex);
                }
            }
        }
        return scannedUnit;
    }

    private void scanPackage(SpringPersistenceUnitInfo scannedUnit, String pkg) {
        if (this.componentsIndex != null) {
            HashSet candidates = new HashSet();
            for (AnnotationTypeFilter filter : entityTypeFilters) {
                candidates.addAll(this.componentsIndex.getCandidateTypes(pkg, filter.getAnnotationType().getName()));
            }
            candidates.forEach(scannedUnit::addManagedClassName);
            Set managedPackages = this.componentsIndex.getCandidateTypes(pkg, "package-info");
            managedPackages.forEach(scannedUnit::addManagedPackage);
            return;
        }
        try {
            String pattern = "classpath*:" + ClassUtils.convertClassNameToResourcePath((String)pkg) + CLASS_RESOURCE_PATTERN;
            Resource[] resources = this.resourcePatternResolver.getResources(pattern);
            CachingMetadataReaderFactory readerFactory = new CachingMetadataReaderFactory((ResourceLoader)this.resourcePatternResolver);
            for (Resource resource : resources) {
                try {
                    MetadataReader reader = readerFactory.getMetadataReader(resource);
                    String className = reader.getClassMetadata().getClassName();
                    if (this.matchesFilter(reader, (MetadataReaderFactory)readerFactory)) {
                        URL url;
                        scannedUnit.addManagedClassName(className);
                        if (scannedUnit.getPersistenceUnitRootUrl() != null || !ResourceUtils.isJarURL((URL)(url = resource.getURL()))) continue;
                        scannedUnit.setPersistenceUnitRootUrl(ResourceUtils.extractJarFileURL((URL)url));
                        continue;
                    }
                    if (!className.endsWith(PACKAGE_INFO_SUFFIX)) continue;
                    scannedUnit.addManagedPackage(className.substring(0, className.length() - PACKAGE_INFO_SUFFIX.length()));
                }
                catch (FileNotFoundException fileNotFoundException) {
                    // empty catch block
                }
            }
        }
        catch (IOException ex) {
            throw new PersistenceException("Failed to scan classpath for unlisted entity classes", (Throwable)ex);
        }
    }

    private boolean matchesFilter(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
        for (TypeFilter typeFilter : entityTypeFilters) {
            if (!typeFilter.match(reader, readerFactory)) continue;
            return true;
        }
        return false;
    }

    @Nullable
    private URL determineDefaultPersistenceUnitRootUrl() {
        if (this.defaultPersistenceUnitRootLocation == null) {
            return null;
        }
        try {
            URL url = this.resourcePatternResolver.getResource(this.defaultPersistenceUnitRootLocation).getURL();
            return ResourceUtils.isJarURL((URL)url) ? ResourceUtils.extractJarFileURL((URL)url) : url;
        }
        catch (IOException ex) {
            throw new PersistenceException("Unable to resolve persistence unit root URL", (Throwable)ex);
        }
    }

    @Nullable
    private Resource getOrmXmlForDefaultPersistenceUnit() {
        Resource ormXml = this.resourcePatternResolver.getResource(this.defaultPersistenceUnitRootLocation + DEFAULT_ORM_XML_RESOURCE);
        if (ormXml.exists()) {
            try {
                Resource persistenceXml = ormXml.createRelative(PERSISTENCE_XML_FILENAME);
                if (!persistenceXml.exists()) {
                    return ormXml;
                }
            }
            catch (IOException ex) {
                return ormXml;
            }
        }
        return null;
    }

    @Nullable
    protected final MutablePersistenceUnitInfo getPersistenceUnitInfo(String persistenceUnitName) {
        PersistenceUnitInfo pui = this.persistenceUnitInfos.get(persistenceUnitName);
        return (MutablePersistenceUnitInfo)pui;
    }

    protected void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        PersistenceUnitPostProcessor[] postProcessors = this.getPersistenceUnitPostProcessors();
        if (postProcessors != null) {
            for (PersistenceUnitPostProcessor postProcessor : postProcessors) {
                postProcessor.postProcessPersistenceUnitInfo(pui);
            }
        }
    }

    protected boolean isPersistenceUnitOverrideAllowed() {
        return false;
    }

    @Override
    public PersistenceUnitInfo obtainDefaultPersistenceUnitInfo() {
        if (this.persistenceUnitInfoNames.isEmpty()) {
            throw new IllegalStateException("No persistence units parsed from " + ObjectUtils.nullSafeToString((Object[])this.persistenceXmlLocations));
        }
        if (this.persistenceUnitInfos.isEmpty()) {
            throw new IllegalStateException("All persistence units from " + ObjectUtils.nullSafeToString((Object[])this.persistenceXmlLocations) + " already obtained");
        }
        if (this.persistenceUnitInfos.size() > 1 && this.defaultPersistenceUnitName != null) {
            return this.obtainPersistenceUnitInfo(this.defaultPersistenceUnitName);
        }
        PersistenceUnitInfo pui = this.persistenceUnitInfos.values().iterator().next();
        this.persistenceUnitInfos.clear();
        return pui;
    }

    @Override
    public PersistenceUnitInfo obtainPersistenceUnitInfo(String persistenceUnitName) {
        PersistenceUnitInfo pui = this.persistenceUnitInfos.remove(persistenceUnitName);
        if (pui == null) {
            if (!this.persistenceUnitInfoNames.contains(persistenceUnitName)) {
                throw new IllegalArgumentException("No persistence unit with name '" + persistenceUnitName + "' found");
            }
            throw new IllegalStateException("Persistence unit with name '" + persistenceUnitName + "' already obtained");
        }
        return pui;
    }

    static {
        entityTypeFilters.add(new AnnotationTypeFilter(Entity.class, false));
        entityTypeFilters.add(new AnnotationTypeFilter(Embeddable.class, false));
        entityTypeFilters.add(new AnnotationTypeFilter(MappedSuperclass.class, false));
        entityTypeFilters.add(new AnnotationTypeFilter(Converter.class, false));
    }
}

