/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AccessType
 */
package org.hibernate.cfg.annotations.reflection.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.AccessType;
import org.hibernate.AnnotationException;
import org.hibernate.boot.internal.ClassmateContext;
import org.hibernate.boot.jaxb.mapping.spi.JaxbConverter;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntity;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityListener;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityListeners;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityMappings;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMappedSuperclass;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPersistenceUnitDefaults;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPersistenceUnitMetadata;
import org.hibernate.boot.jaxb.mapping.spi.ManagedType;
import org.hibernate.boot.model.convert.internal.ClassBasedConverterDescriptor;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.cfg.annotations.reflection.AttributeConverterDefinitionCollector;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;

public class XMLContext
implements Serializable {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(XMLContext.class);
    private final ClassLoaderAccess classLoaderAccess;
    private final ClassmateContext classmateContext;
    private Default globalDefaults;
    private final Map<String, ManagedType> managedTypeOverride = new HashMap<String, ManagedType>();
    private final Map<String, JaxbEntityListener> entityListenerOverride = new HashMap<String, JaxbEntityListener>();
    private final Map<String, Default> defaultsOverride = new HashMap<String, Default>();
    private final List<JaxbEntityMappings> defaultElements = new ArrayList<JaxbEntityMappings>();
    private final List<String> defaultEntityListeners = new ArrayList<String>();
    private boolean hasContext = false;
    private List<ConverterDescriptor> converterDescriptors = new ArrayList<ConverterDescriptor>();

    public XMLContext(BootstrapContext bootstrapContext) {
        this.classLoaderAccess = bootstrapContext.getClassLoaderAccess();
        this.classmateContext = bootstrapContext.getClassmateContext();
    }

    public List<String> addDocument(JaxbEntityMappings entityMappings) {
        this.hasContext = true;
        ArrayList<String> addedClasses = new ArrayList<String>();
        JaxbPersistenceUnitMetadata metadata = entityMappings.getPersistenceUnitMetadata();
        if (metadata != null) {
            if (this.globalDefaults == null) {
                this.globalDefaults = new Default();
                this.globalDefaults.setMetadataComplete(metadata.getXmlMappingMetadataComplete() != null ? Boolean.TRUE : null);
                JaxbPersistenceUnitDefaults defaultElement = metadata.getPersistenceUnitDefaults();
                if (defaultElement != null) {
                    this.globalDefaults.setSchema(defaultElement.getSchema());
                    this.globalDefaults.setCatalog(defaultElement.getCatalog());
                    this.globalDefaults.setAccess(defaultElement.getAccess());
                    this.globalDefaults.setCascadePersist(defaultElement.getCascadePersist() != null ? Boolean.TRUE : null);
                    this.globalDefaults.setDelimitedIdentifiers(defaultElement.getDelimitedIdentifiers() != null ? Boolean.TRUE : null);
                    this.defaultEntityListeners.addAll(this.addEntityListenerClasses(defaultElement.getEntityListeners(), null, addedClasses));
                }
            } else {
                LOG.duplicateMetadata();
            }
        }
        Default entityMappingDefault = new Default();
        String packageName = entityMappings.getPackage();
        entityMappingDefault.setPackageName(packageName);
        entityMappingDefault.setSchema(entityMappings.getSchema());
        entityMappingDefault.setCatalog(entityMappings.getCatalog());
        entityMappingDefault.setAccess(entityMappings.getAccess());
        this.defaultElements.add(entityMappings);
        this.setLocalAttributeConverterDefinitions(entityMappings.getConverter(), packageName);
        this.addClass(entityMappings.getEntity(), packageName, entityMappingDefault, addedClasses);
        this.addClass(entityMappings.getMappedSuperclass(), packageName, entityMappingDefault, addedClasses);
        this.addClass(entityMappings.getEmbeddable(), packageName, entityMappingDefault, addedClasses);
        return addedClasses;
    }

    private void addClass(List<? extends ManagedType> managedTypes, String packageName, Default defaults, List<String> addedClasses) {
        for (ManagedType managedType : managedTypes) {
            String className = XMLContext.buildSafeClassName(managedType.getClazz(), packageName);
            if (this.managedTypeOverride.containsKey(className)) {
                throw new IllegalStateException("Duplicate XML entry for " + className);
            }
            addedClasses.add(className);
            this.managedTypeOverride.put(className, managedType);
            Default mergedDefaults = new Default();
            mergedDefaults.overrideWithCatalogAndSchema(defaults);
            Default fileDefaults = new Default();
            fileDefaults.setMetadataComplete(managedType.isMetadataComplete());
            fileDefaults.setAccess(managedType.getAccess());
            mergedDefaults.overrideWithCatalogAndSchema(fileDefaults);
            this.defaultsOverride.put(className, mergedDefaults);
            LOG.debugf("Adding XML overriding information for %s", className);
            if (managedType instanceof JaxbEntity) {
                this.addEntityListenerClasses(((JaxbEntity)managedType).getEntityListeners(), packageName, addedClasses);
                continue;
            }
            if (!(managedType instanceof JaxbMappedSuperclass)) continue;
            this.addEntityListenerClasses(((JaxbMappedSuperclass)managedType).getEntityListeners(), packageName, addedClasses);
        }
    }

    private List<String> addEntityListenerClasses(JaxbEntityListeners listeners, String packageName, List<String> addedClasses) {
        ArrayList<String> localAddedClasses = new ArrayList<String>();
        if (listeners != null) {
            List<JaxbEntityListener> elements = listeners.getEntityListener();
            for (JaxbEntityListener listener : elements) {
                String listenerClassName = XMLContext.buildSafeClassName(listener.getClazz(), packageName);
                if (this.entityListenerOverride.containsKey(listenerClassName)) {
                    LOG.duplicateListener(listenerClassName);
                    continue;
                }
                localAddedClasses.add(listenerClassName);
                this.entityListenerOverride.put(listenerClassName, listener);
            }
        }
        LOG.debugf("Adding XML overriding information for listeners: %s", localAddedClasses);
        addedClasses.addAll(localAddedClasses);
        return localAddedClasses;
    }

    private void setLocalAttributeConverterDefinitions(List<JaxbConverter> converterElements, String packageName) {
        for (JaxbConverter converterElement : converterElements) {
            String className = converterElement.getClazz();
            boolean autoApply = Boolean.TRUE.equals(converterElement.isAutoApply());
            try {
                Class attributeConverterClass = this.classLoaderAccess.classForName(XMLContext.buildSafeClassName(className, packageName));
                this.converterDescriptors.add(new ClassBasedConverterDescriptor(attributeConverterClass, autoApply, this.classmateContext));
            }
            catch (ClassLoadingException e) {
                throw new AnnotationException("Unable to locate specified AttributeConverter implementation class : " + className, (Throwable)((Object)e));
            }
            catch (Exception e) {
                throw new AnnotationException("Unable to instantiate specified AttributeConverter implementation class : " + className, e);
            }
        }
    }

    public static String buildSafeClassName(String className, String defaultPackageName) {
        if (className.indexOf(46) < 0 && StringHelper.isNotEmpty(defaultPackageName)) {
            className = StringHelper.qualify(defaultPackageName, className);
        }
        return className;
    }

    public static String buildSafeClassName(String className, Default defaults) {
        return XMLContext.buildSafeClassName(className, defaults.getPackageName());
    }

    public Default getDefaultWithoutGlobalCatalogAndSchema(String className) {
        Default xmlDefault = new Default();
        xmlDefault.overrideWithoutCatalogAndSchema(this.globalDefaults);
        if (className != null) {
            Default entityMappingOverriding = this.defaultsOverride.get(className);
            xmlDefault.overrideWithCatalogAndSchema(entityMappingOverriding);
        }
        return xmlDefault;
    }

    public Default getDefaultWithGlobalCatalogAndSchema() {
        Default xmlDefault = new Default();
        xmlDefault.overrideWithCatalogAndSchema(this.globalDefaults);
        return xmlDefault;
    }

    public ManagedType getManagedTypeOverride(String className) {
        return this.managedTypeOverride.get(className);
    }

    public JaxbEntityListener getEntityListenerOverride(String className) {
        return this.entityListenerOverride.get(className);
    }

    public List<JaxbEntityMappings> getAllDocuments() {
        return this.defaultElements;
    }

    public boolean hasContext() {
        return this.hasContext;
    }

    public void applyDiscoveredAttributeConverters(AttributeConverterDefinitionCollector collector) {
        for (ConverterDescriptor descriptor : this.converterDescriptors) {
            collector.addAttributeConverter(descriptor);
        }
        this.converterDescriptors.clear();
    }

    public List<String> getDefaultEntityListeners() {
        return this.defaultEntityListeners;
    }

    public static class Default
    implements Serializable {
        private AccessType access;
        private String packageName;
        private String schema;
        private String catalog;
        private Boolean metadataComplete;
        private Boolean cascadePersist;
        private Boolean delimitedIdentifier;

        public AccessType getAccess() {
            return this.access;
        }

        protected void setAccess(AccessType access) {
            this.access = access;
        }

        public String getCatalog() {
            return this.catalog;
        }

        protected void setCatalog(String catalog) {
            this.catalog = catalog;
        }

        public String getPackageName() {
            return this.packageName;
        }

        protected void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getSchema() {
            return this.schema;
        }

        protected void setSchema(String schema) {
            this.schema = schema;
        }

        public Boolean getMetadataComplete() {
            return this.metadataComplete;
        }

        public boolean canUseJavaAnnotations() {
            return this.metadataComplete == null || this.metadataComplete == false;
        }

        protected void setMetadataComplete(Boolean metadataComplete) {
            this.metadataComplete = metadataComplete;
        }

        public Boolean getCascadePersist() {
            return this.cascadePersist;
        }

        void setCascadePersist(Boolean cascadePersist) {
            this.cascadePersist = cascadePersist;
        }

        public void overrideWithCatalogAndSchema(Default override) {
            this.overrideWithoutCatalogAndSchema(override);
            if (override != null) {
                if (override.getSchema() != null) {
                    this.schema = override.getSchema();
                }
                if (override.getCatalog() != null) {
                    this.catalog = override.getCatalog();
                }
            }
        }

        public void overrideWithoutCatalogAndSchema(Default globalDefault) {
            if (globalDefault != null) {
                if (globalDefault.getAccess() != null) {
                    this.access = globalDefault.getAccess();
                }
                if (globalDefault.getPackageName() != null) {
                    this.packageName = globalDefault.getPackageName();
                }
                if (globalDefault.getDelimitedIdentifier() != null) {
                    this.delimitedIdentifier = globalDefault.getDelimitedIdentifier();
                }
                if (globalDefault.getMetadataComplete() != null) {
                    this.metadataComplete = globalDefault.getMetadataComplete();
                }
                if (globalDefault.getCascadePersist() != null) {
                    this.cascadePersist = globalDefault.getCascadePersist();
                }
            }
        }

        public void setDelimitedIdentifiers(Boolean delimitedIdentifier) {
            this.delimitedIdentifier = delimitedIdentifier;
        }

        public Boolean getDelimitedIdentifier() {
            return this.delimitedIdentifier;
        }
    }
}

