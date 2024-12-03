/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeConverter
 *  javax.persistence.Converter
 *  javax.persistence.Embeddable
 *  javax.persistence.Entity
 *  javax.persistence.MappedSuperclass
 *  org.hibernate.annotations.common.reflection.MetadataProviderInjector
 *  org.hibernate.annotations.common.reflection.ReflectionManager
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.jboss.jandex.IndexView
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.model.source.internal.annotations;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.common.reflection.MetadataProviderInjector;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.boot.AttributeConverterInfo;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityMappings;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;
import org.hibernate.boot.model.process.spi.ManagedResources;
import org.hibernate.boot.model.source.spi.MetadataSourceProcessor;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.JpaOrmXmlPersistenceUnitDefaultAware;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.cfg.AnnotationBinder;
import org.hibernate.cfg.InheritanceState;
import org.hibernate.cfg.annotations.reflection.AttributeConverterDefinitionCollector;
import org.hibernate.cfg.annotations.reflection.internal.JPAXMLOverriddenMetadataProvider;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.jboss.jandex.IndexView;
import org.jboss.logging.Logger;

public class AnnotationMetadataSourceProcessorImpl
implements MetadataSourceProcessor {
    private static final Logger log = Logger.getLogger(AnnotationMetadataSourceProcessorImpl.class);
    private final MetadataBuildingContextRootImpl rootMetadataBuildingContext;
    private final IndexView jandexView;
    private final ReflectionManager reflectionManager;
    private final LinkedHashSet<String> annotatedPackages = new LinkedHashSet();
    private final List<XClass> xClasses = new ArrayList<XClass>();
    private final ClassLoaderService classLoaderService;

    public AnnotationMetadataSourceProcessorImpl(ManagedResources managedResources, MetadataBuildingContextRootImpl rootMetadataBuildingContext, IndexView jandexView) {
        this.rootMetadataBuildingContext = rootMetadataBuildingContext;
        this.jandexView = jandexView;
        this.reflectionManager = rootMetadataBuildingContext.getBootstrapContext().getReflectionManager();
        if (CollectionHelper.isNotEmpty(managedResources.getAnnotatedPackageNames())) {
            this.annotatedPackages.addAll(managedResources.getAnnotatedPackageNames());
        }
        AttributeConverterManager attributeConverterManager = new AttributeConverterManager(rootMetadataBuildingContext);
        this.classLoaderService = rootMetadataBuildingContext.getBuildingOptions().getServiceRegistry().getService(ClassLoaderService.class);
        MetadataBuildingOptions metadataBuildingOptions = rootMetadataBuildingContext.getBuildingOptions();
        if (metadataBuildingOptions.isXmlMappingEnabled()) {
            JPAXMLOverriddenMetadataProvider jpaMetadataProvider = (JPAXMLOverriddenMetadataProvider)((MetadataProviderInjector)this.reflectionManager).getMetadataProvider();
            for (Binding xmlBinding : managedResources.getXmlMappingBindings()) {
                Object root = xmlBinding.getRoot();
                if (!(root instanceof JaxbEntityMappings)) continue;
                JaxbEntityMappings entityMappings = (JaxbEntityMappings)xmlBinding.getRoot();
                List<String> classNames = jpaMetadataProvider.getXMLContext().addDocument(entityMappings);
                for (String className : classNames) {
                    this.xClasses.add(this.toXClass(className, this.reflectionManager, this.classLoaderService));
                }
            }
            jpaMetadataProvider.getXMLContext().applyDiscoveredAttributeConverters(attributeConverterManager);
        }
        for (String className : managedResources.getAnnotatedClassNames()) {
            Class annotatedClass = this.classLoaderService.classForName(className);
            this.categorizeAnnotatedClass(annotatedClass, attributeConverterManager, this.classLoaderService);
        }
        for (Class annotatedClass : managedResources.getAnnotatedClassReferences()) {
            this.categorizeAnnotatedClass(annotatedClass, attributeConverterManager, this.classLoaderService);
        }
    }

    private void categorizeAnnotatedClass(Class annotatedClass, AttributeConverterManager attributeConverterManager, ClassLoaderService cls) {
        XClass xClass = this.reflectionManager.toXClass(annotatedClass);
        if (xClass.isAnnotationPresent(Converter.class)) {
            attributeConverterManager.addAttributeConverter(annotatedClass);
        } else if (xClass.isAnnotationPresent(Entity.class) || xClass.isAnnotationPresent(MappedSuperclass.class)) {
            this.xClasses.add(xClass);
        } else if (xClass.isAnnotationPresent(Embeddable.class)) {
            this.xClasses.add(xClass);
        } else {
            log.debugf("Encountered a non-categorized annotated class [%s]; ignoring", (Object)annotatedClass.getName());
        }
    }

    private XClass toXClass(String className, ReflectionManager reflectionManager, ClassLoaderService cls) {
        return reflectionManager.toXClass(cls.classForName(className));
    }

    @Override
    public void prepare() {
        ((JpaOrmXmlPersistenceUnitDefaultAware)((Object)this.rootMetadataBuildingContext.getBuildingOptions())).apply(new JpaOrmXmlPersistenceUnitDefaultAware.JpaOrmXmlPersistenceUnitDefaults(){
            final Map persistenceUnitDefaults;
            {
                this.persistenceUnitDefaults = AnnotationMetadataSourceProcessorImpl.this.reflectionManager.getDefaults();
            }

            @Override
            public String getDefaultSchemaName() {
                return StringHelper.nullIfEmpty((String)this.persistenceUnitDefaults.get("schema"));
            }

            @Override
            public String getDefaultCatalogName() {
                return StringHelper.nullIfEmpty((String)this.persistenceUnitDefaults.get("catalog"));
            }

            @Override
            public boolean shouldImplicitlyQuoteIdentifiers() {
                Object isDelimited = this.persistenceUnitDefaults.get("delimited-identifier");
                return isDelimited != null && isDelimited == Boolean.TRUE;
            }
        });
        this.rootMetadataBuildingContext.getMetadataCollector().getDatabase().adjustDefaultNamespace(this.rootMetadataBuildingContext.getBuildingOptions().getMappingDefaults().getImplicitCatalogName(), this.rootMetadataBuildingContext.getBuildingOptions().getMappingDefaults().getImplicitSchemaName());
        AnnotationBinder.bindDefaults(this.rootMetadataBuildingContext);
        for (String annotatedPackage : this.annotatedPackages) {
            AnnotationBinder.bindPackage(this.classLoaderService, annotatedPackage, this.rootMetadataBuildingContext);
        }
    }

    @Override
    public void processTypeDefinitions() {
    }

    @Override
    public void processQueryRenames() {
    }

    @Override
    public void processNamedQueries() {
    }

    @Override
    public void processAuxiliaryDatabaseObjectDefinitions() {
    }

    @Override
    public void processIdentifierGenerators() {
    }

    @Override
    public void processFilterDefinitions() {
    }

    @Override
    public void processFetchProfiles() {
    }

    @Override
    public void prepareForEntityHierarchyProcessing() {
    }

    @Override
    public void processEntityHierarchies(Set<String> processedEntityNames) {
        List<XClass> orderedClasses = this.orderAndFillHierarchy(this.xClasses);
        Map<XClass, InheritanceState> inheritanceStatePerClass = AnnotationBinder.buildInheritanceStates(orderedClasses, this.rootMetadataBuildingContext);
        for (XClass clazz : orderedClasses) {
            if (processedEntityNames.contains(clazz.getName())) {
                log.debugf("Skipping annotated class processing of entity [%s], as it has already been processed", (Object)clazz);
                continue;
            }
            AnnotationBinder.bindClass(clazz, inheritanceStatePerClass, this.rootMetadataBuildingContext);
            AnnotationBinder.bindFetchProfilesForClass(clazz, this.rootMetadataBuildingContext);
            processedEntityNames.add(clazz.getName());
        }
    }

    private List<XClass> orderAndFillHierarchy(List<XClass> original) {
        ArrayList<XClass> copy = new ArrayList<XClass>(original.size());
        this.insertMappedSuperclasses(original, copy);
        ArrayList<XClass> workingCopy = new ArrayList<XClass>(copy);
        ArrayList<XClass> newList = new ArrayList<XClass>(copy.size());
        while (workingCopy.size() > 0) {
            XClass clazz = (XClass)workingCopy.get(0);
            this.orderHierarchy(workingCopy, newList, copy, clazz);
        }
        return newList;
    }

    private void insertMappedSuperclasses(List<XClass> original, List<XClass> copy) {
        boolean debug = log.isDebugEnabled();
        for (XClass clazz : original) {
            if (clazz.isAnnotationPresent(MappedSuperclass.class)) {
                if (!debug) continue;
                log.debugf("Skipping explicit MappedSuperclass %s, the class will be discovered analyzing the implementing class", (Object)clazz);
                continue;
            }
            copy.add(clazz);
            for (XClass superClass = clazz.getSuperclass(); superClass != null && !this.reflectionManager.equals(superClass, Object.class) && !copy.contains(superClass); superClass = superClass.getSuperclass()) {
                if (!superClass.isAnnotationPresent(Entity.class) && !superClass.isAnnotationPresent(MappedSuperclass.class)) continue;
                copy.add(superClass);
            }
        }
    }

    private void orderHierarchy(List<XClass> copy, List<XClass> newList, List<XClass> original, XClass clazz) {
        if (clazz == null || this.reflectionManager.equals(clazz, Object.class)) {
            return;
        }
        this.orderHierarchy(copy, newList, original, clazz.getSuperclass());
        if (original.contains(clazz)) {
            if (!newList.contains(clazz)) {
                newList.add(clazz);
            }
            copy.remove(clazz);
        }
    }

    @Override
    public void postProcessEntityHierarchies() {
        for (String annotatedPackage : this.annotatedPackages) {
            AnnotationBinder.bindFetchProfilesForPackage(this.classLoaderService, annotatedPackage, this.rootMetadataBuildingContext);
        }
    }

    @Override
    public void processResultSetMappings() {
    }

    @Override
    public void finishUp() {
    }

    private static class AttributeConverterManager
    implements AttributeConverterDefinitionCollector {
        private final MetadataBuildingContextRootImpl rootMetadataBuildingContext;

        public AttributeConverterManager(MetadataBuildingContextRootImpl rootMetadataBuildingContext) {
            this.rootMetadataBuildingContext = rootMetadataBuildingContext;
        }

        @Override
        public void addAttributeConverter(AttributeConverterInfo info) {
            this.rootMetadataBuildingContext.getMetadataCollector().addAttributeConverter(info.toConverterDescriptor(this.rootMetadataBuildingContext));
        }

        @Override
        public void addAttributeConverter(ConverterDescriptor descriptor) {
            this.rootMetadataBuildingContext.getMetadataCollector().addAttributeConverter(descriptor);
        }

        public void addAttributeConverter(Class<? extends AttributeConverter> converterClass) {
            this.rootMetadataBuildingContext.getMetadataCollector().addAttributeConverter(converterClass);
        }
    }
}

