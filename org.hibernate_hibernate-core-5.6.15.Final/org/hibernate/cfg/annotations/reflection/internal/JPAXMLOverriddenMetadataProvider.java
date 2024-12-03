/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityListeners
 *  javax.persistence.NamedNativeQuery
 *  javax.persistence.NamedQuery
 *  javax.persistence.NamedStoredProcedureQuery
 *  javax.persistence.SequenceGenerator
 *  javax.persistence.SqlResultSetMapping
 *  javax.persistence.TableGenerator
 *  org.hibernate.annotations.common.reflection.AnnotationReader
 *  org.hibernate.annotations.common.reflection.MetadataProvider
 *  org.hibernate.annotations.common.reflection.java.JavaMetadataProvider
 */
package org.hibernate.cfg.annotations.reflection.internal;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityListeners;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQuery;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.TableGenerator;
import org.hibernate.annotations.common.reflection.AnnotationReader;
import org.hibernate.annotations.common.reflection.MetadataProvider;
import org.hibernate.annotations.common.reflection.java.JavaMetadataProvider;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityMappings;
import org.hibernate.boot.jaxb.mapping.spi.JaxbSequenceGenerator;
import org.hibernate.boot.jaxb.mapping.spi.JaxbTableGenerator;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.cfg.annotations.reflection.internal.JPAXMLOverriddenAnnotationReader;
import org.hibernate.cfg.annotations.reflection.internal.XMLContext;

public class JPAXMLOverriddenMetadataProvider
implements MetadataProvider {
    private static final MetadataProvider STATELESS_BASE_DELEGATE = new JavaMetadataProvider();
    private final ClassLoaderAccess classLoaderAccess;
    private final XMLContext xmlContext;
    private final boolean xmlMappingEnabled;
    private Map<Object, Object> defaults;
    private Map<AnnotatedElement, AnnotationReader> cache;

    public JPAXMLOverriddenMetadataProvider(BootstrapContext bootstrapContext) {
        this.classLoaderAccess = bootstrapContext.getClassLoaderAccess();
        this.xmlContext = new XMLContext(bootstrapContext);
        this.xmlMappingEnabled = bootstrapContext.getMetadataBuildingOptions().isXmlMappingEnabled();
    }

    public AnnotationReader getAnnotationReader(AnnotatedElement annotatedElement) {
        AnnotationReader reader;
        if (this.cache == null) {
            this.cache = new HashMap<AnnotatedElement, AnnotationReader>(50);
        }
        if ((reader = this.cache.get(annotatedElement)) == null) {
            reader = this.xmlContext.hasContext() ? new JPAXMLOverriddenAnnotationReader(annotatedElement, this.xmlContext, this.classLoaderAccess) : STATELESS_BASE_DELEGATE.getAnnotationReader(annotatedElement);
            this.cache.put(annotatedElement, reader);
        }
        return reader;
    }

    public void reset() {
        this.cache = null;
    }

    public Map<Object, Object> getDefaults() {
        if (!this.xmlMappingEnabled) {
            return Collections.emptyMap();
        }
        if (this.defaults == null) {
            this.defaults = new HashMap<Object, Object>();
            XMLContext.Default xmlDefaults = this.xmlContext.getDefaultWithGlobalCatalogAndSchema();
            this.defaults.put("schema", xmlDefaults.getSchema());
            this.defaults.put("catalog", xmlDefaults.getCatalog());
            this.defaults.put("delimited-identifier", xmlDefaults.getDelimitedIdentifier());
            this.defaults.put("cascade-persist", xmlDefaults.getCascadePersist());
            ArrayList entityListeners = new ArrayList();
            for (String className : this.xmlContext.getDefaultEntityListeners()) {
                try {
                    entityListeners.add(this.classLoaderAccess.classForName(className));
                }
                catch (ClassLoadingException e) {
                    throw new IllegalStateException("Default entity listener class not found: " + className);
                }
            }
            this.defaults.put(EntityListeners.class, entityListeners);
            for (JaxbEntityMappings entityMappings : this.xmlContext.getAllDocuments()) {
                List<JaxbSequenceGenerator> jaxbSequenceGenerators = entityMappings.getSequenceGenerator();
                ArrayList<SequenceGenerator> sequenceGenerators = (ArrayList<SequenceGenerator>)this.defaults.get(SequenceGenerator.class);
                if (sequenceGenerators == null) {
                    sequenceGenerators = new ArrayList<SequenceGenerator>();
                    this.defaults.put(SequenceGenerator.class, sequenceGenerators);
                }
                for (JaxbSequenceGenerator element : jaxbSequenceGenerators) {
                    sequenceGenerators.add(JPAXMLOverriddenAnnotationReader.buildSequenceGeneratorAnnotation(element));
                }
                List<JaxbTableGenerator> jaxbTableGenerators = entityMappings.getTableGenerator();
                ArrayList<TableGenerator> tableGenerators = (ArrayList<TableGenerator>)this.defaults.get(TableGenerator.class);
                if (tableGenerators == null) {
                    tableGenerators = new ArrayList<TableGenerator>();
                    this.defaults.put(TableGenerator.class, tableGenerators);
                }
                for (JaxbTableGenerator element : jaxbTableGenerators) {
                    tableGenerators.add(JPAXMLOverriddenAnnotationReader.buildTableGeneratorAnnotation(element, xmlDefaults));
                }
                ArrayList<NamedQuery> namedQueries = (ArrayList<NamedQuery>)this.defaults.get(NamedQuery.class);
                if (namedQueries == null) {
                    namedQueries = new ArrayList<NamedQuery>();
                    this.defaults.put(NamedQuery.class, namedQueries);
                }
                List<NamedQuery> currentNamedQueries = JPAXMLOverriddenAnnotationReader.buildNamedQueries(entityMappings.getNamedQuery(), xmlDefaults, this.classLoaderAccess);
                namedQueries.addAll(currentNamedQueries);
                ArrayList<NamedNativeQuery> namedNativeQueries = (ArrayList<NamedNativeQuery>)this.defaults.get(NamedNativeQuery.class);
                if (namedNativeQueries == null) {
                    namedNativeQueries = new ArrayList<NamedNativeQuery>();
                    this.defaults.put(NamedNativeQuery.class, namedNativeQueries);
                }
                List<NamedNativeQuery> currentNamedNativeQueries = JPAXMLOverriddenAnnotationReader.buildNamedNativeQueries(entityMappings.getNamedNativeQuery(), xmlDefaults, this.classLoaderAccess);
                namedNativeQueries.addAll(currentNamedNativeQueries);
                ArrayList<SqlResultSetMapping> sqlResultSetMappings = (ArrayList<SqlResultSetMapping>)this.defaults.get(SqlResultSetMapping.class);
                if (sqlResultSetMappings == null) {
                    sqlResultSetMappings = new ArrayList<SqlResultSetMapping>();
                    this.defaults.put(SqlResultSetMapping.class, sqlResultSetMappings);
                }
                List<SqlResultSetMapping> currentSqlResultSetMappings = JPAXMLOverriddenAnnotationReader.buildSqlResultsetMappings(entityMappings.getSqlResultSetMapping(), xmlDefaults, this.classLoaderAccess);
                sqlResultSetMappings.addAll(currentSqlResultSetMappings);
                ArrayList<NamedStoredProcedureQuery> namedStoredProcedureQueries = (ArrayList<NamedStoredProcedureQuery>)this.defaults.get(NamedStoredProcedureQuery.class);
                if (namedStoredProcedureQueries == null) {
                    namedStoredProcedureQueries = new ArrayList<NamedStoredProcedureQuery>();
                    this.defaults.put(NamedStoredProcedureQuery.class, namedStoredProcedureQueries);
                }
                List<NamedStoredProcedureQuery> currentNamedStoredProcedureQueries = JPAXMLOverriddenAnnotationReader.buildNamedStoreProcedureQueries(entityMappings.getNamedStoredProcedureQuery(), xmlDefaults, this.classLoaderAccess);
                namedStoredProcedureQueries.addAll(currentNamedStoredProcedureQueries);
            }
        }
        return this.defaults;
    }

    public XMLContext getXMLContext() {
        return this.xmlContext;
    }
}

