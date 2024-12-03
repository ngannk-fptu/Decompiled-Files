/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Access
 *  javax.persistence.AccessType
 *  javax.persistence.AssociationOverride
 *  javax.persistence.AssociationOverrides
 *  javax.persistence.AttributeOverride
 *  javax.persistence.AttributeOverrides
 *  javax.persistence.Basic
 *  javax.persistence.Cacheable
 *  javax.persistence.CascadeType
 *  javax.persistence.CollectionTable
 *  javax.persistence.Column
 *  javax.persistence.ColumnResult
 *  javax.persistence.ConstructorResult
 *  javax.persistence.Convert
 *  javax.persistence.Converts
 *  javax.persistence.DiscriminatorColumn
 *  javax.persistence.DiscriminatorType
 *  javax.persistence.DiscriminatorValue
 *  javax.persistence.ElementCollection
 *  javax.persistence.Embeddable
 *  javax.persistence.Embedded
 *  javax.persistence.EmbeddedId
 *  javax.persistence.Entity
 *  javax.persistence.EntityListeners
 *  javax.persistence.EntityResult
 *  javax.persistence.EnumType
 *  javax.persistence.Enumerated
 *  javax.persistence.ExcludeDefaultListeners
 *  javax.persistence.ExcludeSuperclassListeners
 *  javax.persistence.FetchType
 *  javax.persistence.FieldResult
 *  javax.persistence.ForeignKey
 *  javax.persistence.GeneratedValue
 *  javax.persistence.GenerationType
 *  javax.persistence.Id
 *  javax.persistence.IdClass
 *  javax.persistence.Index
 *  javax.persistence.Inheritance
 *  javax.persistence.InheritanceType
 *  javax.persistence.JoinColumn
 *  javax.persistence.JoinColumns
 *  javax.persistence.JoinTable
 *  javax.persistence.Lob
 *  javax.persistence.ManyToMany
 *  javax.persistence.ManyToOne
 *  javax.persistence.MapKey
 *  javax.persistence.MapKeyClass
 *  javax.persistence.MapKeyColumn
 *  javax.persistence.MapKeyEnumerated
 *  javax.persistence.MapKeyJoinColumn
 *  javax.persistence.MapKeyJoinColumns
 *  javax.persistence.MapKeyTemporal
 *  javax.persistence.MappedSuperclass
 *  javax.persistence.MapsId
 *  javax.persistence.NamedAttributeNode
 *  javax.persistence.NamedEntityGraph
 *  javax.persistence.NamedEntityGraphs
 *  javax.persistence.NamedNativeQueries
 *  javax.persistence.NamedNativeQuery
 *  javax.persistence.NamedQueries
 *  javax.persistence.NamedQuery
 *  javax.persistence.NamedStoredProcedureQueries
 *  javax.persistence.NamedStoredProcedureQuery
 *  javax.persistence.NamedSubgraph
 *  javax.persistence.OneToMany
 *  javax.persistence.OneToOne
 *  javax.persistence.OrderBy
 *  javax.persistence.OrderColumn
 *  javax.persistence.ParameterMode
 *  javax.persistence.PostLoad
 *  javax.persistence.PostPersist
 *  javax.persistence.PostRemove
 *  javax.persistence.PostUpdate
 *  javax.persistence.PrePersist
 *  javax.persistence.PreRemove
 *  javax.persistence.PreUpdate
 *  javax.persistence.PrimaryKeyJoinColumn
 *  javax.persistence.PrimaryKeyJoinColumns
 *  javax.persistence.QueryHint
 *  javax.persistence.SecondaryTable
 *  javax.persistence.SecondaryTables
 *  javax.persistence.SequenceGenerator
 *  javax.persistence.SqlResultSetMapping
 *  javax.persistence.SqlResultSetMappings
 *  javax.persistence.StoredProcedureParameter
 *  javax.persistence.Table
 *  javax.persistence.TableGenerator
 *  javax.persistence.Temporal
 *  javax.persistence.TemporalType
 *  javax.persistence.Transient
 *  javax.persistence.UniqueConstraint
 *  javax.persistence.Version
 *  org.hibernate.annotations.common.annotationfactory.AnnotationDescriptor
 *  org.hibernate.annotations.common.annotationfactory.AnnotationFactory
 *  org.hibernate.annotations.common.reflection.AnnotationReader
 *  org.hibernate.annotations.common.reflection.Filter
 *  org.hibernate.annotations.common.reflection.ReflectionUtil
 */
package org.hibernate.cfg.annotations.reflection.internal;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Convert;
import javax.persistence.Converts;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EntityResult;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ExcludeDefaultListeners;
import javax.persistence.ExcludeSuperclassListeners;
import javax.persistence.FetchType;
import javax.persistence.FieldResult;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.MapKeyJoinColumns;
import javax.persistence.MapKeyTemporal;
import javax.persistence.MappedSuperclass;
import javax.persistence.MapsId;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.ParameterMode;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.QueryHint;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import org.hibernate.AnnotationException;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.common.annotationfactory.AnnotationDescriptor;
import org.hibernate.annotations.common.annotationfactory.AnnotationFactory;
import org.hibernate.annotations.common.reflection.AnnotationReader;
import org.hibernate.annotations.common.reflection.Filter;
import org.hibernate.annotations.common.reflection.ReflectionUtil;
import org.hibernate.boot.jaxb.mapping.spi.AssociationAttribute;
import org.hibernate.boot.jaxb.mapping.spi.AttributesContainer;
import org.hibernate.boot.jaxb.mapping.spi.EntityOrMappedSuperclass;
import org.hibernate.boot.jaxb.mapping.spi.JaxbAssociationOverride;
import org.hibernate.boot.jaxb.mapping.spi.JaxbAttributeOverride;
import org.hibernate.boot.jaxb.mapping.spi.JaxbAttributes;
import org.hibernate.boot.jaxb.mapping.spi.JaxbBasic;
import org.hibernate.boot.jaxb.mapping.spi.JaxbCascadeType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbCollectionTable;
import org.hibernate.boot.jaxb.mapping.spi.JaxbColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbColumnResult;
import org.hibernate.boot.jaxb.mapping.spi.JaxbConstructorResult;
import org.hibernate.boot.jaxb.mapping.spi.JaxbConvert;
import org.hibernate.boot.jaxb.mapping.spi.JaxbDiscriminatorColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbElementCollection;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmbeddable;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmbedded;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmbeddedId;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmptyType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntity;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityListener;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityListeners;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityResult;
import org.hibernate.boot.jaxb.mapping.spi.JaxbFieldResult;
import org.hibernate.boot.jaxb.mapping.spi.JaxbGeneratedValue;
import org.hibernate.boot.jaxb.mapping.spi.JaxbId;
import org.hibernate.boot.jaxb.mapping.spi.JaxbIdClass;
import org.hibernate.boot.jaxb.mapping.spi.JaxbIndex;
import org.hibernate.boot.jaxb.mapping.spi.JaxbInheritance;
import org.hibernate.boot.jaxb.mapping.spi.JaxbJoinColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbJoinTable;
import org.hibernate.boot.jaxb.mapping.spi.JaxbLob;
import org.hibernate.boot.jaxb.mapping.spi.JaxbManyToMany;
import org.hibernate.boot.jaxb.mapping.spi.JaxbManyToOne;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMapKey;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMapKeyClass;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMapKeyColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMapKeyJoinColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMappedSuperclass;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedAttributeNode;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedEntityGraph;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedNativeQuery;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedQuery;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedStoredProcedureQuery;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedSubgraph;
import org.hibernate.boot.jaxb.mapping.spi.JaxbOneToMany;
import org.hibernate.boot.jaxb.mapping.spi.JaxbOneToOne;
import org.hibernate.boot.jaxb.mapping.spi.JaxbOrderColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPrimaryKeyJoinColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbQueryHint;
import org.hibernate.boot.jaxb.mapping.spi.JaxbSecondaryTable;
import org.hibernate.boot.jaxb.mapping.spi.JaxbSequenceGenerator;
import org.hibernate.boot.jaxb.mapping.spi.JaxbSqlResultSetMapping;
import org.hibernate.boot.jaxb.mapping.spi.JaxbStoredProcedureParameter;
import org.hibernate.boot.jaxb.mapping.spi.JaxbTable;
import org.hibernate.boot.jaxb.mapping.spi.JaxbTableGenerator;
import org.hibernate.boot.jaxb.mapping.spi.JaxbUniqueConstraint;
import org.hibernate.boot.jaxb.mapping.spi.JaxbVersion;
import org.hibernate.boot.jaxb.mapping.spi.LifecycleCallbackContainer;
import org.hibernate.boot.jaxb.mapping.spi.ManagedType;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.cfg.annotations.reflection.PersistentAttributeFilter;
import org.hibernate.cfg.annotations.reflection.internal.PropertyMappingElementCollector;
import org.hibernate.cfg.annotations.reflection.internal.XMLContext;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;

public class JPAXMLOverriddenAnnotationReader
implements AnnotationReader {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(JPAXMLOverriddenAnnotationReader.class);
    private static final String SCHEMA_VALIDATION = "Activate schema validation for more information";
    private static final String WORD_SEPARATOR = "-";
    private static final Map<Class, String> annotationToXml = new HashMap<Class, String>();
    private final XMLContext xmlContext;
    private final ClassLoaderAccess classLoaderAccess;
    private final AnnotatedElement element;
    private final String className;
    private final String propertyName;
    private final PropertyType propertyType;
    private transient Annotation[] annotations;
    private transient Map<Class, Annotation> annotationsMap;
    private transient PropertyMappingElementCollector elementsForProperty;
    private AccessibleObject mirroredAttribute;

    JPAXMLOverriddenAnnotationReader(AnnotatedElement el, XMLContext xmlContext, ClassLoaderAccess classLoaderAccess) {
        this.element = el;
        this.xmlContext = xmlContext;
        this.classLoaderAccess = classLoaderAccess;
        if (el instanceof Class) {
            Class clazz = (Class)el;
            this.className = clazz.getName();
            this.propertyName = null;
            this.propertyType = null;
        } else if (el instanceof Field) {
            Field field = (Field)el;
            this.className = field.getDeclaringClass().getName();
            this.propertyName = field.getName();
            this.propertyType = PropertyType.FIELD;
            String expectedGetter = "get" + Character.toUpperCase(this.propertyName.charAt(0)) + this.propertyName.substring(1);
            try {
                this.mirroredAttribute = field.getDeclaringClass().getDeclaredMethod(expectedGetter, new Class[0]);
            }
            catch (NoSuchMethodException noSuchMethodException) {}
        } else if (el instanceof Method) {
            Method method = (Method)el;
            this.className = method.getDeclaringClass().getName();
            String methodName = method.getName();
            if (ReflectionUtil.isProperty((Method)method, null, (Filter)PersistentAttributeFilter.INSTANCE)) {
                if (methodName.startsWith("get")) {
                    this.propertyName = Introspector.decapitalize(methodName.substring("get".length()));
                } else if (methodName.startsWith("is")) {
                    this.propertyName = Introspector.decapitalize(methodName.substring("is".length()));
                } else {
                    throw new RuntimeException("Method " + methodName + " is not a property getter");
                }
                this.propertyType = PropertyType.PROPERTY;
                try {
                    this.mirroredAttribute = method.getDeclaringClass().getDeclaredField(this.propertyName);
                }
                catch (NoSuchFieldException noSuchFieldException) {}
            } else {
                this.propertyName = methodName;
                this.propertyType = PropertyType.METHOD;
            }
        } else {
            this.className = null;
            this.propertyName = null;
            this.propertyType = null;
        }
    }

    public JPAXMLOverriddenAnnotationReader(AnnotatedElement el, XMLContext xmlContext, BootstrapContext bootstrapContext) {
        this(el, xmlContext, bootstrapContext.getClassLoaderAccess());
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        this.initAnnotations();
        return (T)this.annotationsMap.get(annotationType);
    }

    public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationType) {
        this.initAnnotations();
        return this.annotationsMap.containsKey(annotationType);
    }

    public Annotation[] getAnnotations() {
        this.initAnnotations();
        return this.annotations;
    }

    private void initAnnotations() {
        block12: {
            if (this.annotations != null) break block12;
            XMLContext.Default defaults = this.xmlContext.getDefaultWithoutGlobalCatalogAndSchema(this.className);
            if (this.className != null && this.propertyName == null) {
                ManagedType managedTypeOverride = this.xmlContext.getManagedTypeOverride(this.className);
                Annotation[] annotations = this.getPhysicalAnnotations();
                ArrayList<Annotation> annotationList = new ArrayList<Annotation>(annotations.length + 5);
                this.annotationsMap = new HashMap<Class, Annotation>(annotations.length + 5);
                for (Annotation annotation : annotations) {
                    if (annotationToXml.containsKey(annotation.annotationType())) continue;
                    annotationList.add(annotation);
                }
                this.addIfNotNull(annotationList, (Annotation)this.getEntity(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getMappedSuperclass(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getEmbeddable(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getTable(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getSecondaryTables(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getPrimaryKeyJoinColumns(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getIdClass(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getCacheable(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getInheritance(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getDiscriminatorValue(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getDiscriminatorColumn(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getSequenceGenerator(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getTableGenerator(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getNamedQueries(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getNamedNativeQueries(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getNamedStoredProcedureQueries(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getNamedEntityGraphs(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getSqlResultSetMappings(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getExcludeDefaultListeners(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getExcludeSuperclassListeners(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getAccessType(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getAttributeOverrides(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getAssociationOverrides(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getEntityListeners(managedTypeOverride, defaults));
                this.addIfNotNull(annotationList, (Annotation)this.getConverts(managedTypeOverride, defaults));
                this.annotations = annotationList.toArray(new Annotation[annotationList.size()]);
                for (Annotation ann : this.annotations) {
                    this.annotationsMap.put(ann.annotationType(), ann);
                }
                this.checkForOrphanProperties(managedTypeOverride);
            } else if (this.className != null) {
                ManagedType managedTypeOverride = this.xmlContext.getManagedTypeOverride(this.className);
                JaxbEntityListener entityListenerOverride = this.xmlContext.getEntityListenerOverride(this.className);
                Annotation[] annotations = this.getPhysicalAnnotations();
                ArrayList<Annotation> annotationList = new ArrayList<Annotation>(annotations.length + 5);
                this.annotationsMap = new HashMap<Class, Annotation>(annotations.length + 5);
                for (Annotation annotation : annotations) {
                    if (annotationToXml.containsKey(annotation.annotationType())) continue;
                    annotationList.add(annotation);
                }
                this.preCalculateElementsForProperty(managedTypeOverride, entityListenerOverride);
                Transient transientAnn = this.getTransient(defaults);
                if (transientAnn != null) {
                    annotationList.add((Annotation)transientAnn);
                } else {
                    if (defaults.canUseJavaAnnotations()) {
                        Access annotation = this.getPhysicalAnnotation(Access.class);
                        this.addIfNotNull(annotationList, (Annotation)annotation);
                    }
                    this.getId(annotationList, defaults);
                    this.getEmbeddedId(annotationList, defaults);
                    this.getEmbedded(annotationList, defaults);
                    this.getBasic(annotationList, defaults);
                    this.getVersion(annotationList, defaults);
                    this.getManyToOne(annotationList, defaults);
                    this.getOneToOne(annotationList, defaults);
                    this.getOneToMany(annotationList, defaults);
                    this.getManyToMany(annotationList, defaults);
                    this.getAny(annotationList, defaults);
                    this.getManyToAny(annotationList, defaults);
                    this.getElementCollection(annotationList, defaults);
                    this.addIfNotNull(annotationList, this.getSequenceGenerator(this.elementsForProperty, defaults));
                    this.addIfNotNull(annotationList, this.getTableGenerator(this.elementsForProperty, defaults));
                    this.addIfNotNull(annotationList, this.getConvertsForAttribute(this.elementsForProperty, defaults));
                }
                this.processEventAnnotations(annotationList, defaults);
                for (Annotation ann : this.annotations = annotationList.toArray(new Annotation[annotationList.size()])) {
                    this.annotationsMap.put(ann.annotationType(), ann);
                }
            } else {
                this.annotations = this.getPhysicalAnnotations();
                this.annotationsMap = new HashMap<Class, Annotation>(this.annotations.length + 5);
                for (Annotation ann : this.annotations) {
                    this.annotationsMap.put(ann.annotationType(), ann);
                }
            }
        }
    }

    private Annotation getConvertsForAttribute(PropertyMappingElementCollector elementsForProperty, XMLContext.Default defaults) {
        HashMap<String, Convert> convertAnnotationsMap = new HashMap<String, Convert>();
        for (JaxbBasic jaxbBasic : elementsForProperty.getBasic()) {
            JaxbConvert convert = jaxbBasic.getConvert();
            if (convert == null) continue;
            this.applyXmlDefinedConverts(Collections.singletonList(convert), defaults, null, convertAnnotationsMap);
        }
        for (JaxbEmbedded jaxbEmbedded : elementsForProperty.getEmbedded()) {
            this.applyXmlDefinedConverts(jaxbEmbedded.getConvert(), defaults, this.propertyName, convertAnnotationsMap);
        }
        for (JaxbElementCollection jaxbElementCollection : elementsForProperty.getElementCollection()) {
            this.applyXmlDefinedConverts(jaxbElementCollection.getConvert(), defaults, this.propertyName, convertAnnotationsMap);
        }
        if (defaults.canUseJavaAnnotations()) {
            this.applyPhysicalConvertAnnotations(this.propertyName, convertAnnotationsMap);
        }
        if (!convertAnnotationsMap.isEmpty()) {
            AnnotationDescriptor groupingDescriptor = new AnnotationDescriptor(Converts.class);
            groupingDescriptor.setValue("value", (Object)convertAnnotationsMap.values().toArray(new Convert[convertAnnotationsMap.size()]));
            return AnnotationFactory.create((AnnotationDescriptor)groupingDescriptor);
        }
        return null;
    }

    private Converts getConverts(ManagedType root, XMLContext.Default defaults) {
        HashMap<String, Convert> convertAnnotationsMap = new HashMap<String, Convert>();
        if (root instanceof JaxbEntity) {
            this.applyXmlDefinedConverts(((JaxbEntity)root).getConvert(), defaults, null, convertAnnotationsMap);
        }
        if (defaults.canUseJavaAnnotations()) {
            this.applyPhysicalConvertAnnotations(null, convertAnnotationsMap);
        }
        if (!convertAnnotationsMap.isEmpty()) {
            AnnotationDescriptor groupingDescriptor = new AnnotationDescriptor(Converts.class);
            groupingDescriptor.setValue("value", (Object)convertAnnotationsMap.values().toArray(new Convert[convertAnnotationsMap.size()]));
            return (Converts)AnnotationFactory.create((AnnotationDescriptor)groupingDescriptor);
        }
        return null;
    }

    private void applyXmlDefinedConverts(List<JaxbConvert> elements, XMLContext.Default defaults, String attributeNamePrefix, Map<String, Convert> convertAnnotationsMap) {
        for (JaxbConvert convertElement : elements) {
            AnnotationDescriptor convertAnnotationDescriptor = new AnnotationDescriptor(Convert.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(convertAnnotationDescriptor, "attribute-name", convertElement.getAttributeName(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(convertAnnotationDescriptor, "disable-conversion", convertElement.isDisableConversion(), false);
            String converter = convertElement.getConverter();
            if (converter != null) {
                String converterClassName = XMLContext.buildSafeClassName(converter, defaults);
                try {
                    Class converterClass = this.classLoaderAccess.classForName(converterClassName);
                    convertAnnotationDescriptor.setValue("converter", converterClass);
                }
                catch (ClassLoadingException e) {
                    throw new AnnotationException("Unable to find specified converter class id-class: " + converterClassName, (Throwable)((Object)e));
                }
            }
            Convert convertAnnotation = (Convert)AnnotationFactory.create((AnnotationDescriptor)convertAnnotationDescriptor);
            String qualifiedAttributeName = this.qualifyConverterAttributeName(attributeNamePrefix, convertAnnotation.attributeName());
            convertAnnotationsMap.put(qualifiedAttributeName, convertAnnotation);
        }
    }

    private String qualifyConverterAttributeName(String attributeNamePrefix, String specifiedAttributeName) {
        String qualifiedAttributeName = StringHelper.isNotEmpty(specifiedAttributeName) ? (StringHelper.isNotEmpty(attributeNamePrefix) ? attributeNamePrefix + '.' + specifiedAttributeName : specifiedAttributeName) : "";
        return qualifiedAttributeName;
    }

    private void applyPhysicalConvertAnnotations(String attributeNamePrefix, Map<String, Convert> convertAnnotationsMap) {
        Converts physicalGroupingAnnotation;
        String qualifiedAttributeName;
        Convert physicalAnnotation = this.getPhysicalAnnotation(Convert.class);
        if (physicalAnnotation != null && !convertAnnotationsMap.containsKey(qualifiedAttributeName = this.qualifyConverterAttributeName(attributeNamePrefix, physicalAnnotation.attributeName()))) {
            convertAnnotationsMap.put(qualifiedAttributeName, physicalAnnotation);
        }
        if ((physicalGroupingAnnotation = this.getPhysicalAnnotation(Converts.class)) != null) {
            for (Convert convertAnnotation : physicalGroupingAnnotation.value()) {
                String qualifiedAttributeName2 = this.qualifyConverterAttributeName(attributeNamePrefix, convertAnnotation.attributeName());
                if (convertAnnotationsMap.containsKey(qualifiedAttributeName2)) continue;
                convertAnnotationsMap.put(qualifiedAttributeName2, convertAnnotation);
            }
        }
    }

    private void checkForOrphanProperties(ManagedType root) {
        AttributesContainer container;
        Class clazz;
        try {
            clazz = this.classLoaderAccess.classForName(this.className);
        }
        catch (ClassLoadingException e) {
            return;
        }
        AttributesContainer attributesContainer = container = root != null ? root.getAttributes() : null;
        if (container != null) {
            HashSet<String> properties = new HashSet<String>();
            for (Field field : clazz.getFields()) {
                properties.add(field.getName());
            }
            for (AccessibleObject accessibleObject : clazz.getMethods()) {
                String name = ((Method)accessibleObject).getName();
                if (name.startsWith("get")) {
                    properties.add(Introspector.decapitalize(name.substring("get".length())));
                    continue;
                }
                if (!name.startsWith("is")) continue;
                properties.add(Introspector.decapitalize(name.substring("is".length())));
            }
            if (container instanceof JaxbAttributes) {
                JaxbAttributes jaxbAttributes = (JaxbAttributes)container;
                this.checkForOrphanProperties((Object)jaxbAttributes.getId(), (Set<String>)properties, (Function)PropertyMappingElementCollector.PERSISTENT_ATTRIBUTE_NAME);
                this.checkForOrphanProperties(jaxbAttributes.getEmbeddedId(), properties, PropertyMappingElementCollector.PERSISTENT_ATTRIBUTE_NAME);
                this.checkForOrphanProperties((Object)jaxbAttributes.getVersion(), (Set<String>)properties, (Function)PropertyMappingElementCollector.PERSISTENT_ATTRIBUTE_NAME);
            }
            this.checkForOrphanProperties((Object)container.getBasic(), (Set<String>)properties, (Function)PropertyMappingElementCollector.PERSISTENT_ATTRIBUTE_NAME);
            this.checkForOrphanProperties((Object)container.getManyToOne(), (Set<String>)properties, (Function)PropertyMappingElementCollector.PERSISTENT_ATTRIBUTE_NAME);
            this.checkForOrphanProperties((Object)container.getOneToMany(), (Set<String>)properties, (Function)PropertyMappingElementCollector.PERSISTENT_ATTRIBUTE_NAME);
            this.checkForOrphanProperties((Object)container.getOneToOne(), (Set<String>)properties, (Function)PropertyMappingElementCollector.PERSISTENT_ATTRIBUTE_NAME);
            this.checkForOrphanProperties((Object)container.getManyToMany(), (Set<String>)properties, (Function)PropertyMappingElementCollector.PERSISTENT_ATTRIBUTE_NAME);
            this.checkForOrphanProperties((Object)container.getElementCollection(), (Set<String>)properties, (Function)PropertyMappingElementCollector.PERSISTENT_ATTRIBUTE_NAME);
            this.checkForOrphanProperties((Object)container.getEmbedded(), (Set<String>)properties, (Function)PropertyMappingElementCollector.PERSISTENT_ATTRIBUTE_NAME);
            this.checkForOrphanProperties((Object)container.getTransient(), (Set<String>)properties, (Function)PropertyMappingElementCollector.JAXB_TRANSIENT_NAME);
        }
    }

    private <T> void checkForOrphanProperties(List<T> elements, Set<String> properties, Function<? super T, String> nameGetter) {
        for (T element : elements) {
            this.checkForOrphanProperties(element, properties, nameGetter);
        }
    }

    private <T> void checkForOrphanProperties(T element, Set<String> properties, Function<? super T, String> nameGetter) {
        if (element == null) {
            return;
        }
        String propertyName = nameGetter.apply(element);
        if (!properties.contains(propertyName)) {
            LOG.propertyNotFound(StringHelper.qualify(this.className, propertyName));
        }
    }

    private Annotation addIfNotNull(List<Annotation> annotationList, Annotation annotation) {
        if (annotation != null) {
            annotationList.add(annotation);
        }
        return annotation;
    }

    private Annotation getTableGenerator(PropertyMappingElementCollector elementsForProperty, XMLContext.Default defaults) {
        for (JaxbId element : elementsForProperty.getId()) {
            JaxbTableGenerator subelement = element.getTableGenerator();
            if (subelement == null) continue;
            return JPAXMLOverriddenAnnotationReader.buildTableGeneratorAnnotation(subelement, defaults);
        }
        if (elementsForProperty.isEmpty() && defaults.canUseJavaAnnotations()) {
            return this.getPhysicalAnnotation(TableGenerator.class);
        }
        return null;
    }

    private Annotation getSequenceGenerator(PropertyMappingElementCollector elementsForProperty, XMLContext.Default defaults) {
        for (JaxbId element : elementsForProperty.getId()) {
            JaxbSequenceGenerator subelement = element.getSequenceGenerator();
            if (subelement == null) continue;
            return JPAXMLOverriddenAnnotationReader.buildSequenceGeneratorAnnotation(subelement);
        }
        if (elementsForProperty.isEmpty() && defaults.canUseJavaAnnotations()) {
            return this.getPhysicalAnnotation(SequenceGenerator.class);
        }
        return null;
    }

    private void processEventAnnotations(List<Annotation> annotationList, XMLContext.Default defaults) {
        AnnotationDescriptor ad;
        boolean eventElement = false;
        if (!this.elementsForProperty.getPrePersist().isEmpty()) {
            ad = new AnnotationDescriptor(PrePersist.class);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
            eventElement = true;
        } else if (!this.elementsForProperty.getPreRemove().isEmpty()) {
            ad = new AnnotationDescriptor(PreRemove.class);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
            eventElement = true;
        } else if (!this.elementsForProperty.getPreUpdate().isEmpty()) {
            ad = new AnnotationDescriptor(PreUpdate.class);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
            eventElement = true;
        } else if (!this.elementsForProperty.getPostPersist().isEmpty()) {
            ad = new AnnotationDescriptor(PostPersist.class);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
            eventElement = true;
        } else if (!this.elementsForProperty.getPostRemove().isEmpty()) {
            ad = new AnnotationDescriptor(PostRemove.class);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
            eventElement = true;
        } else if (!this.elementsForProperty.getPostUpdate().isEmpty()) {
            ad = new AnnotationDescriptor(PostUpdate.class);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
            eventElement = true;
        } else if (!this.elementsForProperty.getPostLoad().isEmpty()) {
            ad = new AnnotationDescriptor(PostLoad.class);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
            eventElement = true;
        }
        if (!eventElement && defaults.canUseJavaAnnotations()) {
            PrePersist ann = this.getPhysicalAnnotation(PrePersist.class);
            this.addIfNotNull(annotationList, (Annotation)ann);
            ann = this.getPhysicalAnnotation(PreRemove.class);
            this.addIfNotNull(annotationList, (Annotation)ann);
            ann = this.getPhysicalAnnotation(PreUpdate.class);
            this.addIfNotNull(annotationList, (Annotation)ann);
            ann = this.getPhysicalAnnotation(PostPersist.class);
            this.addIfNotNull(annotationList, (Annotation)ann);
            ann = this.getPhysicalAnnotation(PostRemove.class);
            this.addIfNotNull(annotationList, (Annotation)ann);
            ann = this.getPhysicalAnnotation(PostUpdate.class);
            this.addIfNotNull(annotationList, (Annotation)ann);
            ann = this.getPhysicalAnnotation(PostLoad.class);
            this.addIfNotNull(annotationList, (Annotation)ann);
        }
    }

    private EntityListeners getEntityListeners(ManagedType root, XMLContext.Default defaults) {
        JaxbEntityListeners element;
        JaxbEntityListeners jaxbEntityListeners = element = root instanceof EntityOrMappedSuperclass ? ((EntityOrMappedSuperclass)root).getEntityListeners() : null;
        if (element != null) {
            ArrayList entityListenerClasses = new ArrayList();
            for (JaxbEntityListener subelement : element.getEntityListener()) {
                String className = subelement.getClazz();
                try {
                    entityListenerClasses.add(this.classLoaderAccess.classForName(XMLContext.buildSafeClassName(className, defaults)));
                }
                catch (ClassLoadingException e) {
                    throw new AnnotationException("Unable to find class: " + className, (Throwable)((Object)e));
                }
            }
            AnnotationDescriptor ad = new AnnotationDescriptor(EntityListeners.class);
            ad.setValue("value", (Object)entityListenerClasses.toArray(new Class[entityListenerClasses.size()]));
            return (EntityListeners)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        if (defaults.canUseJavaAnnotations()) {
            return this.getPhysicalAnnotation(EntityListeners.class);
        }
        return null;
    }

    private JoinTable overridesDefaultsInJoinTable(Annotation annotation, XMLContext.Default defaults) {
        boolean defaultToJoinTable = !this.isPhysicalAnnotationPresent(JoinColumn.class) && !this.isPhysicalAnnotationPresent(JoinColumns.class);
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        defaultToJoinTable = defaultToJoinTable && (annotationClass == ManyToMany.class && StringHelper.isEmpty(((ManyToMany)annotation).mappedBy()) || annotationClass == OneToMany.class && StringHelper.isEmpty(((OneToMany)annotation).mappedBy()) || annotationClass == ElementCollection.class);
        Class<JoinTable> annotationType = JoinTable.class;
        if (defaultToJoinTable && (StringHelper.isNotEmpty(defaults.getCatalog()) || StringHelper.isNotEmpty(defaults.getSchema()))) {
            JoinTable table;
            AnnotationDescriptor ad = new AnnotationDescriptor(annotationType);
            if (defaults.canUseJavaAnnotations() && (table = this.getPhysicalAnnotation(annotationType)) != null) {
                ad.setValue("name", (Object)table.name());
                ad.setValue("schema", (Object)table.schema());
                ad.setValue("catalog", (Object)table.catalog());
                ad.setValue("uniqueConstraints", (Object)table.uniqueConstraints());
                ad.setValue("joinColumns", (Object)table.joinColumns());
                ad.setValue("inverseJoinColumns", (Object)table.inverseJoinColumns());
            }
            if (StringHelper.isEmpty((String)ad.valueOf("schema")) && StringHelper.isNotEmpty(defaults.getSchema())) {
                ad.setValue("schema", (Object)defaults.getSchema());
            }
            if (StringHelper.isEmpty((String)ad.valueOf("catalog")) && StringHelper.isNotEmpty(defaults.getCatalog())) {
                ad.setValue("catalog", (Object)defaults.getCatalog());
            }
            return (JoinTable)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        if (defaults.canUseJavaAnnotations()) {
            return this.getPhysicalAnnotation(annotationType);
        }
        return null;
    }

    private Annotation overridesDefaultCascadePersist(Annotation annotation, XMLContext.Default defaults) {
        if (Boolean.TRUE.equals(defaults.getCascadePersist())) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType == ManyToOne.class) {
                ManyToOne manyToOne = (ManyToOne)annotation;
                ArrayList<CascadeType> cascades = new ArrayList<CascadeType>(Arrays.asList(manyToOne.cascade()));
                if (cascades.contains(CascadeType.ALL) || cascades.contains(CascadeType.PERSIST)) {
                    return annotation;
                }
                cascades.add(CascadeType.PERSIST);
                AnnotationDescriptor ad = new AnnotationDescriptor(annotationType);
                ad.setValue("cascade", (Object)cascades.toArray(new CascadeType[0]));
                ad.setValue("targetEntity", (Object)manyToOne.targetEntity());
                ad.setValue("fetch", (Object)manyToOne.fetch());
                ad.setValue("optional", (Object)manyToOne.optional());
                return AnnotationFactory.create((AnnotationDescriptor)ad);
            }
            if (annotationType == OneToOne.class) {
                OneToOne oneToOne = (OneToOne)annotation;
                ArrayList<CascadeType> cascades = new ArrayList<CascadeType>(Arrays.asList(oneToOne.cascade()));
                if (cascades.contains(CascadeType.ALL) || cascades.contains(CascadeType.PERSIST)) {
                    return annotation;
                }
                cascades.add(CascadeType.PERSIST);
                AnnotationDescriptor ad = new AnnotationDescriptor(annotationType);
                ad.setValue("cascade", (Object)cascades.toArray(new CascadeType[0]));
                ad.setValue("targetEntity", (Object)oneToOne.targetEntity());
                ad.setValue("fetch", (Object)oneToOne.fetch());
                ad.setValue("optional", (Object)oneToOne.optional());
                ad.setValue("mappedBy", (Object)oneToOne.mappedBy());
                ad.setValue("orphanRemoval", (Object)oneToOne.orphanRemoval());
                return AnnotationFactory.create((AnnotationDescriptor)ad);
            }
        }
        return annotation;
    }

    private void getJoinTable(List<Annotation> annotationList, AssociationAttribute associationAttribute, XMLContext.Default defaults) {
        this.addIfNotNull(annotationList, (Annotation)this.buildJoinTable(associationAttribute.getJoinTable(), defaults));
    }

    private JoinTable buildJoinTable(JaxbJoinTable subelement, XMLContext.Default defaults) {
        Class<JoinTable> annotationType = JoinTable.class;
        if (subelement == null) {
            return null;
        }
        AnnotationDescriptor annotation = new AnnotationDescriptor(annotationType);
        JPAXMLOverriddenAnnotationReader.copyAttribute(annotation, "name", subelement.getName(), false);
        JPAXMLOverriddenAnnotationReader.copyAttribute(annotation, "catalog", subelement.getCatalog(), false);
        if (StringHelper.isNotEmpty(defaults.getCatalog()) && StringHelper.isEmpty((String)annotation.valueOf("catalog"))) {
            annotation.setValue("catalog", (Object)defaults.getCatalog());
        }
        JPAXMLOverriddenAnnotationReader.copyAttribute(annotation, "schema", subelement.getSchema(), false);
        if (StringHelper.isNotEmpty(defaults.getSchema()) && StringHelper.isEmpty((String)annotation.valueOf("schema"))) {
            annotation.setValue("schema", (Object)defaults.getSchema());
        }
        JPAXMLOverriddenAnnotationReader.buildUniqueConstraints(annotation, subelement.getUniqueConstraint());
        JPAXMLOverriddenAnnotationReader.buildIndex(annotation, subelement.getIndex());
        annotation.setValue("joinColumns", (Object)this.getJoinColumns(subelement.getJoinColumn(), false));
        annotation.setValue("inverseJoinColumns", (Object)this.getJoinColumns(subelement.getInverseJoinColumn(), true));
        return (JoinTable)AnnotationFactory.create((AnnotationDescriptor)annotation);
    }

    private void getOneToMany(List<Annotation> annotationList, XMLContext.Default defaults) {
        Class<OneToMany> annotationType = OneToMany.class;
        List<JaxbOneToMany> elements = this.elementsForProperty.getOneToMany();
        for (JaxbOneToMany element : elements) {
            AnnotationDescriptor ad = new AnnotationDescriptor(annotationType);
            this.addTargetClass(element.getTargetEntity(), ad, "target-entity", defaults);
            this.getFetchType(ad, element.getFetch());
            this.getCascades(ad, element.getCascade(), defaults);
            this.getJoinTable(annotationList, element, defaults);
            this.buildJoinColumns(annotationList, element.getJoinColumn());
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "orphan-removal", element.isOrphanRemoval(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "mapped-by", element.getMappedBy(), false);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
            this.getOrderBy(annotationList, element.getOrderBy());
            this.getMapKey(annotationList, element.getMapKey());
            this.getMapKeyClass(annotationList, element.getMapKeyClass(), defaults);
            this.getMapKeyColumn(annotationList, element.getMapKeyColumn());
            this.getOrderColumn(annotationList, element.getOrderColumn());
            this.getMapKeyTemporal(annotationList, element.getMapKeyTemporal());
            this.getMapKeyEnumerated(annotationList, element.getMapKeyEnumerated());
            AttributeOverrides annotation = this.getMapKeyAttributeOverrides(element.getMapKeyAttributeOverride(), defaults);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            this.getMapKeyJoinColumns(annotationList, element.getMapKeyJoinColumn());
            this.getAccessType(annotationList, element.getAccess());
        }
        this.afterGetAssociation(annotationType, annotationList, defaults);
    }

    private void getOneToOne(List<Annotation> annotationList, XMLContext.Default defaults) {
        Class<OneToOne> annotationType = OneToOne.class;
        List<JaxbOneToOne> elements = this.elementsForProperty.getOneToOne();
        for (JaxbOneToOne element : elements) {
            AnnotationDescriptor ad = new AnnotationDescriptor(annotationType);
            this.addTargetClass(element.getTargetEntity(), ad, "target-entity", defaults);
            this.getFetchType(ad, element.getFetch());
            this.getCascades(ad, element.getCascade(), defaults);
            this.getJoinTable(annotationList, element, defaults);
            this.buildJoinColumns(annotationList, element.getJoinColumn());
            PrimaryKeyJoinColumns annotation = this.getPrimaryKeyJoinColumns(element.getPrimaryKeyJoinColumn(), defaults, false);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "optional", element.isOptional(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "orphan-removal", element.isOrphanRemoval(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "mapped-by", element.getMappedBy(), false);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
            this.getAssociationId(annotationList, element.isId());
            this.getMapsId(annotationList, element.getMapsId());
            this.getAccessType(annotationList, element.getAccess());
        }
        this.afterGetAssociation(annotationType, annotationList, defaults);
    }

    private void getManyToOne(List<Annotation> annotationList, XMLContext.Default defaults) {
        Class<ManyToOne> annotationType = ManyToOne.class;
        List<JaxbManyToOne> elements = this.elementsForProperty.getManyToOne();
        for (JaxbManyToOne element : elements) {
            AnnotationDescriptor ad = new AnnotationDescriptor(annotationType);
            this.addTargetClass(element.getTargetEntity(), ad, "target-entity", defaults);
            this.getFetchType(ad, element.getFetch());
            this.getCascades(ad, element.getCascade(), defaults);
            this.getJoinTable(annotationList, element, defaults);
            this.buildJoinColumns(annotationList, element.getJoinColumn());
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "optional", element.isOptional(), false);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
            this.getAssociationId(annotationList, element.isId());
            this.getMapsId(annotationList, element.getMapsId());
            this.getAccessType(annotationList, element.getAccess());
        }
        this.afterGetAssociation(annotationType, annotationList, defaults);
    }

    private void getManyToMany(List<Annotation> annotationList, XMLContext.Default defaults) {
        Class<ManyToMany> annotationType = ManyToMany.class;
        List<JaxbManyToMany> elements = this.elementsForProperty.getManyToMany();
        for (JaxbManyToMany element : elements) {
            AnnotationDescriptor ad = new AnnotationDescriptor(annotationType);
            this.addTargetClass(element.getTargetEntity(), ad, "target-entity", defaults);
            this.getFetchType(ad, element.getFetch());
            this.getCascades(ad, element.getCascade(), defaults);
            this.getJoinTable(annotationList, element, defaults);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "mapped-by", element.getMappedBy(), false);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
            this.getOrderBy(annotationList, element.getOrderBy());
            this.getMapKey(annotationList, element.getMapKey());
            this.getMapKeyClass(annotationList, element.getMapKeyClass(), defaults);
            this.getMapKeyColumn(annotationList, element.getMapKeyColumn());
            this.getOrderColumn(annotationList, element.getOrderColumn());
            this.getMapKeyTemporal(annotationList, element.getMapKeyTemporal());
            this.getMapKeyEnumerated(annotationList, element.getMapKeyEnumerated());
            AttributeOverrides annotation = this.getMapKeyAttributeOverrides(element.getMapKeyAttributeOverride(), defaults);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            this.getMapKeyJoinColumns(annotationList, element.getMapKeyJoinColumn());
            this.getAccessType(annotationList, element.getAccess());
        }
        this.afterGetAssociation(annotationType, annotationList, defaults);
    }

    private void getAny(List<Annotation> annotationList, XMLContext.Default defaults) {
        this.afterGetAssociation(Any.class, annotationList, defaults);
    }

    private void getManyToAny(List<Annotation> annotationList, XMLContext.Default defaults) {
        this.afterGetAssociation(ManyToAny.class, annotationList, defaults);
    }

    private void afterGetAssociation(Class<? extends Annotation> annotationType, List<Annotation> annotationList, XMLContext.Default defaults) {
        if (this.elementsForProperty.isEmpty() && defaults.canUseJavaAnnotations()) {
            Annotation annotation = this.getPhysicalAnnotation(annotationType);
            if (annotation != null) {
                annotation = this.overridesDefaultCascadePersist(annotation, defaults);
                annotationList.add(annotation);
                annotation = this.overridesDefaultsInJoinTable(annotation, defaults);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(JoinColumn.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(JoinColumns.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(PrimaryKeyJoinColumn.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(PrimaryKeyJoinColumns.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(MapKey.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(OrderBy.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(AttributeOverride.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(AttributeOverrides.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(AssociationOverride.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(AssociationOverrides.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(Lob.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(Enumerated.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(Temporal.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(Column.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(Columns.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(MapKeyClass.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(MapKeyTemporal.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(MapKeyEnumerated.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(MapKeyColumn.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(MapKeyJoinColumn.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(MapKeyJoinColumns.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(OrderColumn.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(Cascade.class);
                this.addIfNotNull(annotationList, annotation);
            } else if (this.isPhysicalAnnotationPresent(ElementCollection.class)) {
                annotation = this.overridesDefaultsInJoinTable((Annotation)this.getPhysicalAnnotation(ElementCollection.class), defaults);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(MapKey.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(OrderBy.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(AttributeOverride.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(AttributeOverrides.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(AssociationOverride.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(AssociationOverrides.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(Lob.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(Enumerated.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(Temporal.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(Column.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(OrderColumn.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(MapKeyClass.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(MapKeyTemporal.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(MapKeyEnumerated.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(MapKeyColumn.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(MapKeyJoinColumn.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(MapKeyJoinColumns.class);
                this.addIfNotNull(annotationList, annotation);
                annotation = this.getPhysicalAnnotation(CollectionTable.class);
                this.addIfNotNull(annotationList, annotation);
            }
        }
    }

    private void getMapKeyJoinColumns(List<Annotation> annotationList, List<JaxbMapKeyJoinColumn> elements) {
        MapKeyJoinColumn[] joinColumns = this.buildMapKeyJoinColumns(elements);
        if (joinColumns.length > 0) {
            AnnotationDescriptor ad = new AnnotationDescriptor(MapKeyJoinColumns.class);
            ad.setValue("value", (Object)joinColumns);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
        }
    }

    private MapKeyJoinColumn[] buildMapKeyJoinColumns(List<JaxbMapKeyJoinColumn> elements) {
        ArrayList<MapKeyJoinColumn> joinColumns = new ArrayList<MapKeyJoinColumn>();
        if (elements != null) {
            for (JaxbMapKeyJoinColumn element : elements) {
                AnnotationDescriptor column = new AnnotationDescriptor(MapKeyJoinColumn.class);
                JPAXMLOverriddenAnnotationReader.copyAttribute(column, "name", element.getName(), false);
                JPAXMLOverriddenAnnotationReader.copyAttribute(column, "referenced-column-name", element.getReferencedColumnName(), false);
                JPAXMLOverriddenAnnotationReader.copyAttribute(column, "unique", element.isUnique(), false);
                JPAXMLOverriddenAnnotationReader.copyAttribute(column, "nullable", element.isNullable(), false);
                JPAXMLOverriddenAnnotationReader.copyAttribute(column, "insertable", element.isInsertable(), false);
                JPAXMLOverriddenAnnotationReader.copyAttribute(column, "updatable", element.isUpdatable(), false);
                JPAXMLOverriddenAnnotationReader.copyAttribute(column, "column-definition", element.getColumnDefinition(), false);
                JPAXMLOverriddenAnnotationReader.copyAttribute(column, "table", element.getTable(), false);
                joinColumns.add((MapKeyJoinColumn)AnnotationFactory.create((AnnotationDescriptor)column));
            }
        }
        return joinColumns.toArray(new MapKeyJoinColumn[joinColumns.size()]);
    }

    private AttributeOverrides getMapKeyAttributeOverrides(List<JaxbAttributeOverride> elements, XMLContext.Default defaults) {
        List<AttributeOverride> attributes = this.buildAttributeOverrides(elements, "map-key-attribute-override");
        return this.mergeAttributeOverrides(defaults, attributes, false);
    }

    private Cacheable getCacheable(ManagedType root, XMLContext.Default defaults) {
        Boolean attValue;
        if (root instanceof JaxbEntity && (attValue = ((JaxbEntity)root).isCacheable()) != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(Cacheable.class);
            ad.setValue("value", (Object)attValue);
            return (Cacheable)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        if (defaults.canUseJavaAnnotations()) {
            return this.getPhysicalAnnotation(Cacheable.class);
        }
        return null;
    }

    private void getMapKeyEnumerated(List<Annotation> annotationList, EnumType enumType) {
        if (enumType != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(MapKeyEnumerated.class);
            ad.setValue("value", (Object)enumType);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
        }
    }

    private void getMapKeyTemporal(List<Annotation> annotationList, TemporalType temporalType) {
        if (temporalType != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(MapKeyTemporal.class);
            ad.setValue("value", (Object)temporalType);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
        }
    }

    private void getOrderColumn(List<Annotation> annotationList, JaxbOrderColumn element) {
        if (element != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(OrderColumn.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "name", element.getName(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "nullable", element.isNullable(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "insertable", element.isInsertable(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "updatable", element.isUpdatable(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "column-definition", element.getColumnDefinition(), false);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
        }
    }

    private void getMapsId(List<Annotation> annotationList, String mapsId) {
        if (mapsId != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(MapsId.class);
            ad.setValue("value", (Object)mapsId);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
        }
    }

    private void getAssociationId(List<Annotation> annotationList, Boolean isId) {
        if (Boolean.TRUE.equals(isId)) {
            AnnotationDescriptor ad = new AnnotationDescriptor(Id.class);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
        }
    }

    private void addTargetClass(String className, AnnotationDescriptor ad, String nodeName, XMLContext.Default defaults) {
        if (className != null) {
            Class clazz;
            try {
                clazz = this.classLoaderAccess.classForName(XMLContext.buildSafeClassName(className, defaults));
            }
            catch (ClassLoadingException e) {
                throw new AnnotationException("Unable to find " + nodeName + ": " + className, (Throwable)((Object)e));
            }
            ad.setValue(JPAXMLOverriddenAnnotationReader.getJavaAttributeNameFromXMLOne(nodeName), clazz);
        }
    }

    private void getElementCollection(List<Annotation> annotationList, XMLContext.Default defaults) {
        for (JaxbElementCollection element : this.elementsForProperty.getElementCollection()) {
            AnnotationDescriptor ad = new AnnotationDescriptor(ElementCollection.class);
            this.addTargetClass(element.getTargetClass(), ad, "target-class", defaults);
            this.getFetchType(ad, element.getFetch());
            this.getOrderBy(annotationList, element.getOrderBy());
            this.getOrderColumn(annotationList, element.getOrderColumn());
            this.getMapKey(annotationList, element.getMapKey());
            this.getMapKeyClass(annotationList, element.getMapKeyClass(), defaults);
            this.getMapKeyTemporal(annotationList, element.getMapKeyTemporal());
            this.getMapKeyEnumerated(annotationList, element.getMapKeyEnumerated());
            this.getMapKeyColumn(annotationList, element.getMapKeyColumn());
            this.getMapKeyJoinColumns(annotationList, element.getMapKeyJoinColumn());
            Column annotation = this.getColumn(element.getColumn(), false, "element-collection");
            this.addIfNotNull(annotationList, (Annotation)annotation);
            this.getTemporal(annotationList, element.getTemporal());
            this.getEnumerated(annotationList, element.getEnumerated());
            this.getLob(annotationList, element.getLob());
            ArrayList<AttributeOverride> attributes = new ArrayList<AttributeOverride>();
            attributes.addAll(this.buildAttributeOverrides(element.getMapKeyAttributeOverride(), "map-key-attribute-override"));
            attributes.addAll(this.buildAttributeOverrides(element.getAttributeOverride(), "attribute-override"));
            annotation = this.mergeAttributeOverrides(defaults, attributes, false);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getAssociationOverrides(element.getAssociationOverride(), defaults, false);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            this.getCollectionTable(annotationList, element.getCollectionTable(), defaults);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
            this.getAccessType(annotationList, element.getAccess());
        }
    }

    private void getOrderBy(List<Annotation> annotationList, String orderBy) {
        if (orderBy != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(OrderBy.class);
            ad.setValue("value", (Object)orderBy);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
        }
    }

    private void getMapKey(List<Annotation> annotationList, JaxbMapKey element) {
        if (element != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(MapKey.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "name", element.getName(), false);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
        }
    }

    private void getMapKeyColumn(List<Annotation> annotationList, JaxbMapKeyColumn element) {
        if (element != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(MapKeyColumn.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "name", element.getName(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "unique", element.isUnique(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "nullable", element.isNullable(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "insertable", element.isInsertable(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "updatable", element.isUpdatable(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "column-definition", element.getColumnDefinition(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "table", element.getTable(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "length", element.getLength(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "precision", element.getPrecision(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "scale", element.getScale(), false);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
        }
    }

    private void getMapKeyClass(List<Annotation> annotationList, JaxbMapKeyClass element, XMLContext.Default defaults) {
        String nodeName = "map-key-class";
        if (element != null) {
            String mapKeyClassName = element.getClazz();
            AnnotationDescriptor ad = new AnnotationDescriptor(MapKeyClass.class);
            if (StringHelper.isNotEmpty(mapKeyClassName)) {
                Class clazz;
                try {
                    clazz = this.classLoaderAccess.classForName(XMLContext.buildSafeClassName(mapKeyClassName, defaults));
                }
                catch (ClassLoadingException e) {
                    throw new AnnotationException("Unable to find " + nodeName + ": " + mapKeyClassName, (Throwable)((Object)e));
                }
                ad.setValue("value", clazz);
            }
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
        }
    }

    private void getCollectionTable(List<Annotation> annotationList, JaxbCollectionTable element, XMLContext.Default defaults) {
        if (element != null) {
            JoinColumn[] joinColumns;
            AnnotationDescriptor annotation = new AnnotationDescriptor(CollectionTable.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(annotation, "name", element.getName(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(annotation, "catalog", element.getCatalog(), false);
            if (StringHelper.isNotEmpty(defaults.getCatalog()) && StringHelper.isEmpty((String)annotation.valueOf("catalog"))) {
                annotation.setValue("catalog", (Object)defaults.getCatalog());
            }
            JPAXMLOverriddenAnnotationReader.copyAttribute(annotation, "schema", element.getSchema(), false);
            if (StringHelper.isNotEmpty(defaults.getSchema()) && StringHelper.isEmpty((String)annotation.valueOf("schema"))) {
                annotation.setValue("schema", (Object)defaults.getSchema());
            }
            if ((joinColumns = this.getJoinColumns(element.getJoinColumn(), false)).length > 0) {
                annotation.setValue("joinColumns", (Object)joinColumns);
            }
            JPAXMLOverriddenAnnotationReader.buildUniqueConstraints(annotation, element.getUniqueConstraint());
            JPAXMLOverriddenAnnotationReader.buildIndex(annotation, element.getIndex());
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)annotation));
        }
    }

    private void buildJoinColumns(List<Annotation> annotationList, List<JaxbJoinColumn> elements) {
        JoinColumn[] joinColumns = this.getJoinColumns(elements, false);
        if (joinColumns.length > 0) {
            AnnotationDescriptor ad = new AnnotationDescriptor(JoinColumns.class);
            ad.setValue("value", (Object)joinColumns);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
        }
    }

    private void getCascades(AnnotationDescriptor ad, JaxbCascadeType element, XMLContext.Default defaults) {
        ArrayList<CascadeType> cascades = new ArrayList<CascadeType>();
        if (element != null) {
            if (element.getCascadeAll() != null) {
                cascades.add(CascadeType.ALL);
            }
            if (element.getCascadePersist() != null) {
                cascades.add(CascadeType.PERSIST);
            }
            if (element.getCascadeMerge() != null) {
                cascades.add(CascadeType.MERGE);
            }
            if (element.getCascadeRemove() != null) {
                cascades.add(CascadeType.REMOVE);
            }
            if (element.getCascadeRefresh() != null) {
                cascades.add(CascadeType.REFRESH);
            }
            if (element.getCascadeDetach() != null) {
                cascades.add(CascadeType.DETACH);
            }
        }
        if (Boolean.TRUE.equals(defaults.getCascadePersist()) && !cascades.contains(CascadeType.ALL) && !cascades.contains(CascadeType.PERSIST)) {
            cascades.add(CascadeType.PERSIST);
        }
        if (cascades.size() > 0) {
            ad.setValue("cascade", (Object)cascades.toArray(new CascadeType[cascades.size()]));
        }
    }

    private void getEmbedded(List<Annotation> annotationList, XMLContext.Default defaults) {
        Embedded annotation;
        for (JaxbEmbedded element : this.elementsForProperty.getEmbedded()) {
            AnnotationDescriptor ad = new AnnotationDescriptor(Embedded.class);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
            AttributeOverrides annotation2 = this.getAttributeOverrides(element.getAttributeOverride(), defaults, false);
            this.addIfNotNull(annotationList, (Annotation)annotation2);
            annotation2 = this.getAssociationOverrides(element.getAssociationOverride(), defaults, false);
            this.addIfNotNull(annotationList, (Annotation)annotation2);
            this.getAccessType(annotationList, element.getAccess());
        }
        if (this.elementsForProperty.isEmpty() && defaults.canUseJavaAnnotations() && (annotation = this.getPhysicalAnnotation(Embedded.class)) != null) {
            annotationList.add((Annotation)annotation);
            annotation = this.getPhysicalAnnotation(AttributeOverride.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(AttributeOverrides.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(AssociationOverride.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(AssociationOverrides.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
        }
    }

    private Transient getTransient(XMLContext.Default defaults) {
        if (!this.elementsForProperty.getTransient().isEmpty()) {
            AnnotationDescriptor ad = new AnnotationDescriptor(Transient.class);
            return (Transient)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        if (this.elementsForProperty.isEmpty() && defaults.canUseJavaAnnotations()) {
            return this.getPhysicalAnnotation(Transient.class);
        }
        return null;
    }

    private void getVersion(List<Annotation> annotationList, XMLContext.Default defaults) {
        Object annotation;
        for (JaxbVersion element : this.elementsForProperty.getVersion()) {
            Columns annotation2 = this.buildColumns(element.getColumn(), "version");
            this.addIfNotNull(annotationList, annotation2);
            this.getTemporal(annotationList, element.getTemporal());
            AnnotationDescriptor basic = new AnnotationDescriptor(Version.class);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)basic));
            this.getAccessType(annotationList, element.getAccess());
        }
        if (this.elementsForProperty.isEmpty() && defaults.canUseJavaAnnotations() && (annotation = this.getPhysicalAnnotation(Version.class)) != null) {
            annotationList.add((Annotation)annotation);
            annotation = this.getPhysicalAnnotation(Column.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(Columns.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(Temporal.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
        }
    }

    private void getBasic(List<Annotation> annotationList, XMLContext.Default defaults) {
        for (JaxbBasic element : this.elementsForProperty.getBasic()) {
            Columns annotation = this.buildColumns(element.getColumn(), "basic");
            this.addIfNotNull(annotationList, annotation);
            this.getAccessType(annotationList, element.getAccess());
            this.getTemporal(annotationList, element.getTemporal());
            this.getLob(annotationList, element.getLob());
            this.getEnumerated(annotationList, element.getEnumerated());
            AnnotationDescriptor basic = new AnnotationDescriptor(Basic.class);
            this.getFetchType(basic, element.getFetch());
            JPAXMLOverriddenAnnotationReader.copyAttribute(basic, "optional", element.isOptional(), false);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)basic));
        }
        if (this.elementsForProperty.isEmpty() && defaults.canUseJavaAnnotations()) {
            Object annotation = this.getPhysicalAnnotation(Basic.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(Lob.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(Enumerated.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(Temporal.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(Column.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(Columns.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(AttributeOverride.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(AttributeOverrides.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(AssociationOverride.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(AssociationOverrides.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
        }
    }

    private void getEnumerated(List<Annotation> annotationList, EnumType enumType) {
        if (enumType != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(Enumerated.class);
            ad.setValue("value", (Object)enumType);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
        }
    }

    private void getLob(List<Annotation> annotationList, JaxbLob element) {
        if (element != null) {
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)new AnnotationDescriptor(Lob.class)));
        }
    }

    private void getFetchType(AnnotationDescriptor descriptor, FetchType type) {
        if (type != null) {
            descriptor.setValue("fetch", (Object)type);
        }
    }

    private void getEmbeddedId(List<Annotation> annotationList, XMLContext.Default defaults) {
        Object annotation;
        for (JaxbEmbeddedId element : this.elementsForProperty.getEmbeddedId()) {
            if (!this.isProcessingId(defaults)) continue;
            AttributeOverrides annotation2 = this.getAttributeOverrides(element.getAttributeOverride(), defaults, false);
            this.addIfNotNull(annotationList, (Annotation)annotation2);
            AnnotationDescriptor ad = new AnnotationDescriptor(EmbeddedId.class);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
            this.getAccessType(annotationList, element.getAccess());
        }
        if (this.elementsForProperty.isEmpty() && defaults.canUseJavaAnnotations() && (annotation = this.getPhysicalAnnotation(EmbeddedId.class)) != null) {
            annotationList.add((Annotation)annotation);
            annotation = this.getPhysicalAnnotation(Column.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(Columns.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(GeneratedValue.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(Temporal.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(TableGenerator.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(SequenceGenerator.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(AttributeOverride.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(AttributeOverrides.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(AssociationOverride.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(AssociationOverrides.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
        }
    }

    private void preCalculateElementsForProperty(ManagedType managedType, JaxbEntityListener entityListener) {
        AttributesContainer attributes;
        this.elementsForProperty = new PropertyMappingElementCollector(this.propertyName);
        AttributesContainer attributesContainer = attributes = managedType == null ? null : managedType.getAttributes();
        if (attributes != null) {
            this.elementsForProperty.collectPersistentAttributesIfMatching(attributes);
        }
        if (managedType instanceof LifecycleCallbackContainer) {
            this.elementsForProperty.collectLifecycleCallbacksIfMatching((LifecycleCallbackContainer)((Object)managedType));
        }
        if (entityListener != null) {
            this.elementsForProperty.collectLifecycleCallbacksIfMatching(entityListener);
        }
    }

    private void getId(List<Annotation> annotationList, XMLContext.Default defaults) {
        Object annotation;
        for (JaxbId element : this.elementsForProperty.getId()) {
            boolean processId = this.isProcessingId(defaults);
            if (!processId) continue;
            Columns annotation2 = this.buildColumns(element.getColumn(), "id");
            this.addIfNotNull(annotationList, annotation2);
            annotation2 = this.buildGeneratedValue(element.getGeneratedValue());
            this.addIfNotNull(annotationList, annotation2);
            this.getTemporal(annotationList, element.getTemporal());
            annotation2 = this.getTableGenerator(element.getTableGenerator(), defaults);
            this.addIfNotNull(annotationList, annotation2);
            annotation2 = this.getSequenceGenerator(element.getSequenceGenerator(), defaults);
            this.addIfNotNull(annotationList, annotation2);
            AnnotationDescriptor id = new AnnotationDescriptor(Id.class);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)id));
            this.getAccessType(annotationList, element.getAccess());
        }
        if (this.elementsForProperty.isEmpty() && defaults.canUseJavaAnnotations() && (annotation = this.getPhysicalAnnotation(Id.class)) != null) {
            annotationList.add((Annotation)annotation);
            annotation = this.getPhysicalAnnotation(Column.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(Columns.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(GeneratedValue.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(Temporal.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(TableGenerator.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(SequenceGenerator.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(AttributeOverride.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(AttributeOverrides.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(AssociationOverride.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
            annotation = this.getPhysicalAnnotation(AssociationOverrides.class);
            this.addIfNotNull(annotationList, (Annotation)annotation);
        }
    }

    private boolean isProcessingId(XMLContext.Default defaults) {
        boolean isExplicit = defaults.getAccess() != null;
        boolean correctAccess = PropertyType.PROPERTY.equals((Object)this.propertyType) && AccessType.PROPERTY.equals((Object)defaults.getAccess()) || PropertyType.FIELD.equals((Object)this.propertyType) && AccessType.FIELD.equals((Object)defaults.getAccess());
        boolean hasId = defaults.canUseJavaAnnotations() && (this.isPhysicalAnnotationPresent(Id.class) || this.isPhysicalAnnotationPresent(EmbeddedId.class));
        boolean mirrorAttributeIsId = defaults.canUseJavaAnnotations() && this.mirroredAttribute != null && (this.mirroredAttribute.isAnnotationPresent(Id.class) || this.mirroredAttribute.isAnnotationPresent(EmbeddedId.class));
        boolean propertyIsDefault = PropertyType.PROPERTY.equals((Object)this.propertyType) && !mirrorAttributeIsId;
        return correctAccess || !isExplicit && hasId || !isExplicit && propertyIsDefault;
    }

    private Columns buildColumns(JaxbColumn element, String nodeName) {
        if (element == null) {
            return null;
        }
        ArrayList<Column> columns = new ArrayList<Column>(1);
        columns.add(this.getColumn(element, false, nodeName));
        if (columns.size() > 0) {
            AnnotationDescriptor columnsDescr = new AnnotationDescriptor(Columns.class);
            columnsDescr.setValue("columns", (Object)columns.toArray(new Column[columns.size()]));
            return (Columns)AnnotationFactory.create((AnnotationDescriptor)columnsDescr);
        }
        return null;
    }

    private Columns buildColumns(List<JaxbColumn> elements, String nodeName) {
        ArrayList<Column> columns = new ArrayList<Column>(elements.size());
        for (JaxbColumn element : elements) {
            columns.add(this.getColumn(element, false, nodeName));
        }
        if (columns.size() > 0) {
            AnnotationDescriptor columnsDescr = new AnnotationDescriptor(Columns.class);
            columnsDescr.setValue("columns", (Object)columns.toArray(new Column[columns.size()]));
            return (Columns)AnnotationFactory.create((AnnotationDescriptor)columnsDescr);
        }
        return null;
    }

    private GeneratedValue buildGeneratedValue(JaxbGeneratedValue element) {
        if (element != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(GeneratedValue.class);
            GenerationType strategy = element.getStrategy();
            if (strategy != null) {
                ad.setValue("strategy", (Object)strategy);
            }
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "generator", element.getGenerator(), false);
            return (GeneratedValue)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        return null;
    }

    private void getTemporal(List<Annotation> annotationList, TemporalType type) {
        if (type != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(Temporal.class);
            ad.setValue("value", (Object)type);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
        }
    }

    private void getAccessType(List<Annotation> annotationList, AccessType type) {
        if (this.element == null) {
            return;
        }
        if (type != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(Access.class);
            if (AccessType.PROPERTY.equals((Object)type) && this.element instanceof Method || AccessType.FIELD.equals((Object)type) && this.element instanceof Field) {
                return;
            }
            ad.setValue("value", (Object)type);
            annotationList.add(AnnotationFactory.create((AnnotationDescriptor)ad));
        }
    }

    private AssociationOverrides getAssociationOverrides(ManagedType root, XMLContext.Default defaults) {
        return this.getAssociationOverrides(root instanceof JaxbEntity ? ((JaxbEntity)root).getAssociationOverride() : Collections.emptyList(), defaults, true);
    }

    private AssociationOverrides getAssociationOverrides(List<JaxbAssociationOverride> elements, XMLContext.Default defaults, boolean mergeWithAnnotations) {
        List<AssociationOverride> attributes = this.buildAssociationOverrides(elements, defaults);
        if (mergeWithAnnotations && defaults.canUseJavaAnnotations()) {
            AssociationOverride annotation = this.getPhysicalAnnotation(AssociationOverride.class);
            this.addAssociationOverrideIfNeeded(annotation, attributes);
            AssociationOverrides annotations = this.getPhysicalAnnotation(AssociationOverrides.class);
            if (annotations != null) {
                for (AssociationOverride current : annotations.value()) {
                    this.addAssociationOverrideIfNeeded(current, attributes);
                }
            }
        }
        if (attributes.size() > 0) {
            AnnotationDescriptor ad = new AnnotationDescriptor(AssociationOverrides.class);
            ad.setValue("value", (Object)attributes.toArray(new AssociationOverride[attributes.size()]));
            return (AssociationOverrides)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        return null;
    }

    private List<AssociationOverride> buildAssociationOverrides(List<JaxbAssociationOverride> elements, XMLContext.Default defaults) {
        ArrayList<AssociationOverride> overrides = new ArrayList<AssociationOverride>();
        if (elements != null && elements.size() > 0) {
            for (JaxbAssociationOverride current : elements) {
                AnnotationDescriptor override = new AnnotationDescriptor(AssociationOverride.class);
                JPAXMLOverriddenAnnotationReader.copyAttribute(override, "name", current.getName(), true);
                override.setValue("joinColumns", (Object)this.getJoinColumns(current.getJoinColumn(), false));
                JoinTable joinTable = this.buildJoinTable(current.getJoinTable(), defaults);
                if (joinTable != null) {
                    override.setValue("joinTable", (Object)joinTable);
                }
                overrides.add((AssociationOverride)AnnotationFactory.create((AnnotationDescriptor)override));
            }
        }
        return overrides;
    }

    private JoinColumn[] getJoinColumns(List<JaxbJoinColumn> subelements, boolean isInverse) {
        ArrayList<JoinColumn> joinColumns = new ArrayList<JoinColumn>();
        if (subelements != null) {
            for (JaxbJoinColumn subelement : subelements) {
                AnnotationDescriptor column = new AnnotationDescriptor(JoinColumn.class);
                JPAXMLOverriddenAnnotationReader.copyAttribute(column, "name", subelement.getName(), false);
                JPAXMLOverriddenAnnotationReader.copyAttribute(column, "referenced-column-name", subelement.getReferencedColumnName(), false);
                JPAXMLOverriddenAnnotationReader.copyAttribute(column, "unique", subelement.isUnique(), false);
                JPAXMLOverriddenAnnotationReader.copyAttribute(column, "nullable", subelement.isNullable(), false);
                JPAXMLOverriddenAnnotationReader.copyAttribute(column, "insertable", subelement.isInsertable(), false);
                JPAXMLOverriddenAnnotationReader.copyAttribute(column, "updatable", subelement.isUpdatable(), false);
                JPAXMLOverriddenAnnotationReader.copyAttribute(column, "column-definition", subelement.getColumnDefinition(), false);
                JPAXMLOverriddenAnnotationReader.copyAttribute(column, "table", subelement.getTable(), false);
                joinColumns.add((JoinColumn)AnnotationFactory.create((AnnotationDescriptor)column));
            }
        }
        return joinColumns.toArray(new JoinColumn[joinColumns.size()]);
    }

    private void addAssociationOverrideIfNeeded(AssociationOverride annotation, List<AssociationOverride> overrides) {
        if (annotation != null) {
            String overrideName = annotation.name();
            boolean present = false;
            for (AssociationOverride current : overrides) {
                if (!current.name().equals(overrideName)) continue;
                present = true;
                break;
            }
            if (!present) {
                overrides.add(annotation);
            }
        }
    }

    private AttributeOverrides getAttributeOverrides(ManagedType root, XMLContext.Default defaults) {
        return this.getAttributeOverrides(root instanceof JaxbEntity ? ((JaxbEntity)root).getAttributeOverride() : Collections.emptyList(), defaults, true);
    }

    private AttributeOverrides getAttributeOverrides(List<JaxbAttributeOverride> elements, XMLContext.Default defaults, boolean mergeWithAnnotations) {
        List<AttributeOverride> attributes = this.buildAttributeOverrides(elements, "attribute-override");
        return this.mergeAttributeOverrides(defaults, attributes, mergeWithAnnotations);
    }

    private AttributeOverrides mergeAttributeOverrides(XMLContext.Default defaults, List<AttributeOverride> attributes, boolean mergeWithAnnotations) {
        if (mergeWithAnnotations && defaults.canUseJavaAnnotations()) {
            AttributeOverride annotation = this.getPhysicalAnnotation(AttributeOverride.class);
            this.addAttributeOverrideIfNeeded(annotation, attributes);
            AttributeOverrides annotations = this.getPhysicalAnnotation(AttributeOverrides.class);
            if (annotations != null) {
                for (AttributeOverride current : annotations.value()) {
                    this.addAttributeOverrideIfNeeded(current, attributes);
                }
            }
        }
        if (attributes.size() > 0) {
            AnnotationDescriptor ad = new AnnotationDescriptor(AttributeOverrides.class);
            ad.setValue("value", (Object)attributes.toArray(new AttributeOverride[attributes.size()]));
            return (AttributeOverrides)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        return null;
    }

    private List<AttributeOverride> buildAttributeOverrides(List<JaxbAttributeOverride> subelements, String nodeName) {
        ArrayList<AttributeOverride> overrides = new ArrayList<AttributeOverride>();
        if (subelements != null && subelements.size() > 0) {
            for (JaxbAttributeOverride current : subelements) {
                AnnotationDescriptor override = new AnnotationDescriptor(AttributeOverride.class);
                JPAXMLOverriddenAnnotationReader.copyAttribute(override, "name", current.getName(), true);
                JaxbColumn column = current.getColumn();
                override.setValue("column", (Object)this.getColumn(column, true, nodeName));
                overrides.add((AttributeOverride)AnnotationFactory.create((AnnotationDescriptor)override));
            }
        }
        return overrides;
    }

    private Column getColumn(JaxbColumn element, boolean isMandatory, String nodeName) {
        if (element != null) {
            AnnotationDescriptor column = new AnnotationDescriptor(Column.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(column, "name", element.getName(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(column, "unique", element.isUnique(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(column, "nullable", element.isNullable(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(column, "insertable", element.isInsertable(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(column, "updatable", element.isUpdatable(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(column, "column-definition", element.getColumnDefinition(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(column, "table", element.getTable(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(column, "length", element.getLength(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(column, "precision", element.getPrecision(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(column, "scale", element.getScale(), false);
            return (Column)AnnotationFactory.create((AnnotationDescriptor)column);
        }
        if (isMandatory) {
            throw new AnnotationException(nodeName + ".column is mandatory. " + SCHEMA_VALIDATION);
        }
        return null;
    }

    private void addAttributeOverrideIfNeeded(AttributeOverride annotation, List<AttributeOverride> overrides) {
        if (annotation != null) {
            String overrideName = annotation.name();
            boolean present = false;
            for (AttributeOverride current : overrides) {
                if (!current.name().equals(overrideName)) continue;
                present = true;
                break;
            }
            if (!present) {
                overrides.add(annotation);
            }
        }
    }

    private Access getAccessType(ManagedType root, XMLContext.Default defaults) {
        AccessType access;
        AccessType accessType = access = root == null ? null : root.getAccess();
        if (access != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(Access.class);
            ad.setValue("value", (Object)access);
            return (Access)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        if (defaults.canUseJavaAnnotations() && this.isPhysicalAnnotationPresent(Access.class)) {
            return this.getPhysicalAnnotation(Access.class);
        }
        if (defaults.getAccess() != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(Access.class);
            ad.setValue("value", (Object)defaults.getAccess());
            return (Access)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        return null;
    }

    private ExcludeSuperclassListeners getExcludeSuperclassListeners(ManagedType root, XMLContext.Default defaults) {
        return (ExcludeSuperclassListeners)this.getMarkerAnnotation(ExcludeSuperclassListeners.class, root instanceof EntityOrMappedSuperclass ? ((EntityOrMappedSuperclass)root).getExcludeSuperclassListeners() : null, defaults);
    }

    private ExcludeDefaultListeners getExcludeDefaultListeners(ManagedType root, XMLContext.Default defaults) {
        return (ExcludeDefaultListeners)this.getMarkerAnnotation(ExcludeDefaultListeners.class, root instanceof EntityOrMappedSuperclass ? ((EntityOrMappedSuperclass)root).getExcludeDefaultListeners() : null, defaults);
    }

    private Annotation getMarkerAnnotation(Class<? extends Annotation> clazz, JaxbEmptyType element, XMLContext.Default defaults) {
        if (element != null) {
            return AnnotationFactory.create((AnnotationDescriptor)new AnnotationDescriptor(clazz));
        }
        if (defaults.canUseJavaAnnotations()) {
            return this.getPhysicalAnnotation(clazz);
        }
        return null;
    }

    private SqlResultSetMappings getSqlResultSetMappings(ManagedType root, XMLContext.Default defaults) {
        ArrayList<SqlResultSetMapping> results;
        List<SqlResultSetMapping> list = results = root instanceof JaxbEntity ? JPAXMLOverriddenAnnotationReader.buildSqlResultsetMappings(((JaxbEntity)root).getSqlResultSetMapping(), defaults, this.classLoaderAccess) : new ArrayList<SqlResultSetMapping>();
        if (defaults.canUseJavaAnnotations()) {
            SqlResultSetMapping annotation = this.getPhysicalAnnotation(SqlResultSetMapping.class);
            this.addSqlResultsetMappingIfNeeded(annotation, results);
            SqlResultSetMappings annotations = this.getPhysicalAnnotation(SqlResultSetMappings.class);
            if (annotations != null) {
                for (SqlResultSetMapping current : annotations.value()) {
                    this.addSqlResultsetMappingIfNeeded(current, results);
                }
            }
        }
        if (results.size() > 0) {
            AnnotationDescriptor ad = new AnnotationDescriptor(SqlResultSetMappings.class);
            ad.setValue("value", (Object)results.toArray(new SqlResultSetMapping[results.size()]));
            return (SqlResultSetMappings)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        return null;
    }

    public static List<NamedEntityGraph> buildNamedEntityGraph(List<JaxbNamedEntityGraph> elements, XMLContext.Default defaults, ClassLoaderAccess classLoaderAccess) {
        ArrayList<NamedEntityGraph> namedEntityGraphList = new ArrayList<NamedEntityGraph>();
        for (JaxbNamedEntityGraph element : elements) {
            AnnotationDescriptor ann = new AnnotationDescriptor(NamedEntityGraph.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ann, "name", element.getName(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ann, "include-all-attributes", element.isIncludeAllAttributes(), false);
            JPAXMLOverriddenAnnotationReader.bindNamedAttributeNodes(element.getNamedAttributeNode(), ann);
            JPAXMLOverriddenAnnotationReader.bindNamedSubgraph(defaults, ann, "subgraphs", element.getSubgraph(), classLoaderAccess);
            JPAXMLOverriddenAnnotationReader.bindNamedSubgraph(defaults, ann, "subclassSubgraphs", element.getSubclassSubgraph(), classLoaderAccess);
            namedEntityGraphList.add((NamedEntityGraph)AnnotationFactory.create((AnnotationDescriptor)ann));
        }
        return namedEntityGraphList;
    }

    private static void bindNamedSubgraph(XMLContext.Default defaults, AnnotationDescriptor ann, String annotationAttributeName, List<JaxbNamedSubgraph> subgraphNodes, ClassLoaderAccess classLoaderAccess) {
        ArrayList<NamedSubgraph> annSubgraphNodes = new ArrayList<NamedSubgraph>();
        for (JaxbNamedSubgraph subgraphNode : subgraphNodes) {
            Class clazz;
            AnnotationDescriptor annSubgraphNode = new AnnotationDescriptor(NamedSubgraph.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(annSubgraphNode, "name", subgraphNode.getName(), true);
            String clazzName = subgraphNode.getClazz();
            try {
                clazz = classLoaderAccess.classForName(XMLContext.buildSafeClassName(clazzName, defaults));
            }
            catch (ClassLoadingException e) {
                throw new AnnotationException("Unable to find entity-class: " + clazzName, (Throwable)((Object)e));
            }
            annSubgraphNode.setValue("type", clazz);
            JPAXMLOverriddenAnnotationReader.bindNamedAttributeNodes(subgraphNode.getNamedAttributeNode(), annSubgraphNode);
            annSubgraphNodes.add((NamedSubgraph)AnnotationFactory.create((AnnotationDescriptor)annSubgraphNode));
        }
        ann.setValue(annotationAttributeName, (Object)annSubgraphNodes.toArray(new NamedSubgraph[annSubgraphNodes.size()]));
    }

    private static void bindNamedAttributeNodes(List<JaxbNamedAttributeNode> elements, AnnotationDescriptor ann) {
        ArrayList<NamedAttributeNode> annNamedAttributeNodes = new ArrayList<NamedAttributeNode>();
        for (JaxbNamedAttributeNode element : elements) {
            AnnotationDescriptor annNamedAttributeNode = new AnnotationDescriptor(NamedAttributeNode.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(annNamedAttributeNode, "value", "name", element.getName(), true);
            JPAXMLOverriddenAnnotationReader.copyAttribute(annNamedAttributeNode, "subgraph", element.getSubgraph(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(annNamedAttributeNode, "key-subgraph", element.getKeySubgraph(), false);
            annNamedAttributeNodes.add((NamedAttributeNode)AnnotationFactory.create((AnnotationDescriptor)annNamedAttributeNode));
        }
        ann.setValue("attributeNodes", (Object)annNamedAttributeNodes.toArray(new NamedAttributeNode[annNamedAttributeNodes.size()]));
    }

    public static List<NamedStoredProcedureQuery> buildNamedStoreProcedureQueries(List<JaxbNamedStoredProcedureQuery> elements, XMLContext.Default defaults, ClassLoaderAccess classLoaderAccess) {
        ArrayList<NamedStoredProcedureQuery> namedStoredProcedureQueries = new ArrayList<NamedStoredProcedureQuery>();
        for (JaxbNamedStoredProcedureQuery element : elements) {
            AnnotationDescriptor ann = new AnnotationDescriptor(NamedStoredProcedureQuery.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ann, "name", element.getName(), true);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ann, "procedure-name", element.getProcedureName(), true);
            ArrayList<StoredProcedureParameter> storedProcedureParameters = new ArrayList<StoredProcedureParameter>();
            for (JaxbStoredProcedureParameter parameterElement : element.getParameter()) {
                Class clazz;
                AnnotationDescriptor parameterDescriptor = new AnnotationDescriptor(StoredProcedureParameter.class);
                JPAXMLOverriddenAnnotationReader.copyAttribute(parameterDescriptor, "name", parameterElement.getName(), false);
                ParameterMode modeValue = parameterElement.getMode();
                if (modeValue == null) {
                    parameterDescriptor.setValue("mode", (Object)ParameterMode.IN);
                } else {
                    parameterDescriptor.setValue("mode", (Object)modeValue);
                }
                String clazzName = parameterElement.getClazz();
                try {
                    clazz = classLoaderAccess.classForName(XMLContext.buildSafeClassName(clazzName, defaults));
                }
                catch (ClassLoadingException e) {
                    throw new AnnotationException("Unable to find entity-class: " + clazzName, (Throwable)((Object)e));
                }
                parameterDescriptor.setValue("type", clazz);
                storedProcedureParameters.add((StoredProcedureParameter)AnnotationFactory.create((AnnotationDescriptor)parameterDescriptor));
            }
            ann.setValue("parameters", (Object)storedProcedureParameters.toArray(new StoredProcedureParameter[storedProcedureParameters.size()]));
            ArrayList returnClasses = new ArrayList();
            for (String clazzName : element.getResultClass()) {
                Class clazz;
                try {
                    clazz = classLoaderAccess.classForName(XMLContext.buildSafeClassName(clazzName, defaults));
                }
                catch (ClassLoadingException e) {
                    throw new AnnotationException("Unable to find entity-class: " + clazzName, (Throwable)((Object)e));
                }
                returnClasses.add(clazz);
            }
            ann.setValue("resultClasses", (Object)returnClasses.toArray(new Class[returnClasses.size()]));
            ann.setValue("resultSetMappings", (Object)element.getResultSetMapping().toArray(new String[0]));
            JPAXMLOverriddenAnnotationReader.buildQueryHints(element.getHint(), ann);
            namedStoredProcedureQueries.add((NamedStoredProcedureQuery)AnnotationFactory.create((AnnotationDescriptor)ann));
        }
        return namedStoredProcedureQueries;
    }

    public static List<SqlResultSetMapping> buildSqlResultsetMappings(List<JaxbSqlResultSetMapping> elements, XMLContext.Default defaults, ClassLoaderAccess classLoaderAccess) {
        ArrayList<SqlResultSetMapping> builtResultSetMappings = new ArrayList<SqlResultSetMapping>();
        for (JaxbSqlResultSetMapping resultSetMappingElement : elements) {
            AnnotationDescriptor resultSetMappingAnnotation = new AnnotationDescriptor(SqlResultSetMapping.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(resultSetMappingAnnotation, "name", resultSetMappingElement.getName(), true);
            ArrayList<EntityResult> entityResultAnnotations = null;
            ArrayList<ColumnResult> columnResultAnnotations = null;
            ArrayList<ConstructorResult> constructorResultAnnotations = null;
            for (JaxbEntityResult jaxbEntityResult : resultSetMappingElement.getEntityResult()) {
                if (entityResultAnnotations == null) {
                    entityResultAnnotations = new ArrayList<EntityResult>();
                }
                entityResultAnnotations.add(JPAXMLOverriddenAnnotationReader.buildEntityResult(jaxbEntityResult, defaults, classLoaderAccess));
            }
            for (JaxbColumnResult jaxbColumnResult : resultSetMappingElement.getColumnResult()) {
                if (columnResultAnnotations == null) {
                    columnResultAnnotations = new ArrayList<ColumnResult>();
                }
                columnResultAnnotations.add(JPAXMLOverriddenAnnotationReader.buildColumnResult(jaxbColumnResult, defaults, classLoaderAccess));
            }
            for (JaxbConstructorResult jaxbConstructorResult : resultSetMappingElement.getConstructorResult()) {
                if (constructorResultAnnotations == null) {
                    constructorResultAnnotations = new ArrayList<ConstructorResult>();
                }
                constructorResultAnnotations.add(JPAXMLOverriddenAnnotationReader.buildConstructorResult(jaxbConstructorResult, defaults, classLoaderAccess));
            }
            if (entityResultAnnotations != null && !entityResultAnnotations.isEmpty()) {
                resultSetMappingAnnotation.setValue("entities", (Object)entityResultAnnotations.toArray(new EntityResult[entityResultAnnotations.size()]));
            }
            if (columnResultAnnotations != null && !columnResultAnnotations.isEmpty()) {
                resultSetMappingAnnotation.setValue("columns", (Object)columnResultAnnotations.toArray(new ColumnResult[columnResultAnnotations.size()]));
            }
            if (constructorResultAnnotations != null && !constructorResultAnnotations.isEmpty()) {
                resultSetMappingAnnotation.setValue("classes", (Object)constructorResultAnnotations.toArray(new ConstructorResult[constructorResultAnnotations.size()]));
            }
            builtResultSetMappings.add((SqlResultSetMapping)AnnotationFactory.create((AnnotationDescriptor)resultSetMappingAnnotation));
        }
        return builtResultSetMappings;
    }

    private static EntityResult buildEntityResult(JaxbEntityResult entityResultElement, XMLContext.Default defaults, ClassLoaderAccess classLoaderAccess) {
        AnnotationDescriptor entityResultDescriptor = new AnnotationDescriptor(EntityResult.class);
        Class entityClass = JPAXMLOverriddenAnnotationReader.resolveClassReference(entityResultElement.getEntityClass(), defaults, classLoaderAccess);
        entityResultDescriptor.setValue("entityClass", (Object)entityClass);
        JPAXMLOverriddenAnnotationReader.copyAttribute(entityResultDescriptor, "discriminator-column", entityResultElement.getDiscriminatorColumn(), false);
        ArrayList<FieldResult> fieldResultAnnotations = new ArrayList<FieldResult>();
        for (JaxbFieldResult fieldResult : entityResultElement.getFieldResult()) {
            AnnotationDescriptor fieldResultDescriptor = new AnnotationDescriptor(FieldResult.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(fieldResultDescriptor, "name", fieldResult.getName(), true);
            JPAXMLOverriddenAnnotationReader.copyAttribute(fieldResultDescriptor, "column", fieldResult.getColumn(), true);
            fieldResultAnnotations.add((FieldResult)AnnotationFactory.create((AnnotationDescriptor)fieldResultDescriptor));
        }
        entityResultDescriptor.setValue("fields", (Object)fieldResultAnnotations.toArray(new FieldResult[fieldResultAnnotations.size()]));
        return (EntityResult)AnnotationFactory.create((AnnotationDescriptor)entityResultDescriptor);
    }

    private static Class resolveClassReference(String className, XMLContext.Default defaults, ClassLoaderAccess classLoaderAccess) {
        if (className == null) {
            throw new AnnotationException("<entity-result> without entity-class. Activate schema validation for more information");
        }
        try {
            return classLoaderAccess.classForName(XMLContext.buildSafeClassName(className, defaults));
        }
        catch (ClassLoadingException e) {
            throw new AnnotationException("Unable to find specified class: " + className, (Throwable)((Object)e));
        }
    }

    private static ColumnResult buildColumnResult(JaxbColumnResult columnResultElement, XMLContext.Default defaults, ClassLoaderAccess classLoaderAccess) {
        AnnotationDescriptor columnResultDescriptor = new AnnotationDescriptor(ColumnResult.class);
        JPAXMLOverriddenAnnotationReader.copyAttribute(columnResultDescriptor, "name", columnResultElement.getName(), true);
        String columnTypeName = columnResultElement.getClazz();
        if (StringHelper.isNotEmpty(columnTypeName)) {
            columnResultDescriptor.setValue("type", (Object)JPAXMLOverriddenAnnotationReader.resolveClassReference(columnTypeName, defaults, classLoaderAccess));
        }
        return (ColumnResult)AnnotationFactory.create((AnnotationDescriptor)columnResultDescriptor);
    }

    private static ConstructorResult buildConstructorResult(JaxbConstructorResult constructorResultElement, XMLContext.Default defaults, ClassLoaderAccess classLoaderAccess) {
        AnnotationDescriptor constructorResultDescriptor = new AnnotationDescriptor(ConstructorResult.class);
        Class entityClass = JPAXMLOverriddenAnnotationReader.resolveClassReference(constructorResultElement.getTargetClass(), defaults, classLoaderAccess);
        constructorResultDescriptor.setValue("targetClass", (Object)entityClass);
        ArrayList<ColumnResult> columnResultAnnotations = new ArrayList<ColumnResult>();
        for (JaxbColumnResult columnResultElement : constructorResultElement.getColumn()) {
            columnResultAnnotations.add(JPAXMLOverriddenAnnotationReader.buildColumnResult(columnResultElement, defaults, classLoaderAccess));
        }
        constructorResultDescriptor.setValue("columns", (Object)columnResultAnnotations.toArray(new ColumnResult[columnResultAnnotations.size()]));
        return (ConstructorResult)AnnotationFactory.create((AnnotationDescriptor)constructorResultDescriptor);
    }

    private void addSqlResultsetMappingIfNeeded(SqlResultSetMapping annotation, List<SqlResultSetMapping> resultsets) {
        if (annotation != null) {
            String resultsetName = annotation.name();
            boolean present = false;
            for (SqlResultSetMapping current : resultsets) {
                if (!current.name().equals(resultsetName)) continue;
                present = true;
                break;
            }
            if (!present) {
                resultsets.add(annotation);
            }
        }
    }

    private NamedQueries getNamedQueries(ManagedType root, XMLContext.Default defaults) {
        ArrayList<NamedQuery> queries;
        List<NamedQuery> list = queries = root instanceof JaxbEntity ? JPAXMLOverriddenAnnotationReader.buildNamedQueries(((JaxbEntity)root).getNamedQuery(), defaults, this.classLoaderAccess) : new ArrayList<NamedQuery>();
        if (defaults.canUseJavaAnnotations()) {
            NamedQuery annotation = this.getPhysicalAnnotation(NamedQuery.class);
            this.addNamedQueryIfNeeded(annotation, queries);
            NamedQueries annotations = this.getPhysicalAnnotation(NamedQueries.class);
            if (annotations != null) {
                for (NamedQuery current : annotations.value()) {
                    this.addNamedQueryIfNeeded(current, queries);
                }
            }
        }
        if (queries.size() > 0) {
            AnnotationDescriptor ad = new AnnotationDescriptor(NamedQueries.class);
            ad.setValue("value", (Object)queries.toArray(new NamedQuery[queries.size()]));
            return (NamedQueries)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        return null;
    }

    private void addNamedQueryIfNeeded(NamedQuery annotation, List<NamedQuery> queries) {
        if (annotation != null) {
            String queryName = annotation.name();
            boolean present = false;
            for (NamedQuery current : queries) {
                if (!current.name().equals(queryName)) continue;
                present = true;
                break;
            }
            if (!present) {
                queries.add(annotation);
            }
        }
    }

    private NamedEntityGraphs getNamedEntityGraphs(ManagedType root, XMLContext.Default defaults) {
        ArrayList<NamedEntityGraph> queries;
        List<NamedEntityGraph> list = queries = root instanceof JaxbEntity ? JPAXMLOverriddenAnnotationReader.buildNamedEntityGraph(((JaxbEntity)root).getNamedEntityGraph(), defaults, this.classLoaderAccess) : new ArrayList<NamedEntityGraph>();
        if (defaults.canUseJavaAnnotations()) {
            NamedEntityGraph annotation = this.getPhysicalAnnotation(NamedEntityGraph.class);
            this.addNamedEntityGraphIfNeeded(annotation, queries);
            NamedEntityGraphs annotations = this.getPhysicalAnnotation(NamedEntityGraphs.class);
            if (annotations != null) {
                for (NamedEntityGraph current : annotations.value()) {
                    this.addNamedEntityGraphIfNeeded(current, queries);
                }
            }
        }
        if (queries.size() > 0) {
            AnnotationDescriptor ad = new AnnotationDescriptor(NamedEntityGraphs.class);
            ad.setValue("value", (Object)queries.toArray(new NamedEntityGraph[queries.size()]));
            return (NamedEntityGraphs)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        return null;
    }

    private void addNamedEntityGraphIfNeeded(NamedEntityGraph annotation, List<NamedEntityGraph> queries) {
        if (annotation != null) {
            String queryName = annotation.name();
            boolean present = false;
            for (NamedEntityGraph current : queries) {
                if (!current.name().equals(queryName)) continue;
                present = true;
                break;
            }
            if (!present) {
                queries.add(annotation);
            }
        }
    }

    private NamedStoredProcedureQueries getNamedStoredProcedureQueries(ManagedType root, XMLContext.Default defaults) {
        ArrayList<NamedStoredProcedureQuery> queries;
        List<NamedStoredProcedureQuery> list = queries = root instanceof JaxbEntity ? JPAXMLOverriddenAnnotationReader.buildNamedStoreProcedureQueries(((JaxbEntity)root).getNamedStoredProcedureQuery(), defaults, this.classLoaderAccess) : new ArrayList<NamedStoredProcedureQuery>();
        if (defaults.canUseJavaAnnotations()) {
            NamedStoredProcedureQuery annotation = this.getPhysicalAnnotation(NamedStoredProcedureQuery.class);
            this.addNamedStoredProcedureQueryIfNeeded(annotation, queries);
            NamedStoredProcedureQueries annotations = this.getPhysicalAnnotation(NamedStoredProcedureQueries.class);
            if (annotations != null) {
                for (NamedStoredProcedureQuery current : annotations.value()) {
                    this.addNamedStoredProcedureQueryIfNeeded(current, queries);
                }
            }
        }
        if (queries.size() > 0) {
            AnnotationDescriptor ad = new AnnotationDescriptor(NamedStoredProcedureQueries.class);
            ad.setValue("value", (Object)queries.toArray(new NamedStoredProcedureQuery[queries.size()]));
            return (NamedStoredProcedureQueries)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        return null;
    }

    private void addNamedStoredProcedureQueryIfNeeded(NamedStoredProcedureQuery annotation, List<NamedStoredProcedureQuery> queries) {
        if (annotation != null) {
            String queryName = annotation.name();
            boolean present = false;
            for (NamedStoredProcedureQuery current : queries) {
                if (!current.name().equals(queryName)) continue;
                present = true;
                break;
            }
            if (!present) {
                queries.add(annotation);
            }
        }
    }

    private NamedNativeQueries getNamedNativeQueries(ManagedType root, XMLContext.Default defaults) {
        ArrayList<NamedNativeQuery> queries;
        List<NamedNativeQuery> list = queries = root instanceof JaxbEntity ? JPAXMLOverriddenAnnotationReader.buildNamedNativeQueries(((JaxbEntity)root).getNamedNativeQuery(), defaults, this.classLoaderAccess) : new ArrayList<NamedNativeQuery>();
        if (defaults.canUseJavaAnnotations()) {
            NamedNativeQuery annotation = this.getPhysicalAnnotation(NamedNativeQuery.class);
            this.addNamedNativeQueryIfNeeded(annotation, queries);
            NamedNativeQueries annotations = this.getPhysicalAnnotation(NamedNativeQueries.class);
            if (annotations != null) {
                for (NamedNativeQuery current : annotations.value()) {
                    this.addNamedNativeQueryIfNeeded(current, queries);
                }
            }
        }
        if (queries.size() > 0) {
            AnnotationDescriptor ad = new AnnotationDescriptor(NamedNativeQueries.class);
            ad.setValue("value", (Object)queries.toArray(new NamedNativeQuery[queries.size()]));
            return (NamedNativeQueries)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        return null;
    }

    private void addNamedNativeQueryIfNeeded(NamedNativeQuery annotation, List<NamedNativeQuery> queries) {
        if (annotation != null) {
            String queryName = annotation.name();
            boolean present = false;
            for (NamedNativeQuery current : queries) {
                if (!current.name().equals(queryName)) continue;
                present = true;
                break;
            }
            if (!present) {
                queries.add(annotation);
            }
        }
    }

    private static void buildQueryHints(List<JaxbQueryHint> elements, AnnotationDescriptor ann) {
        ArrayList<QueryHint> queryHints = new ArrayList<QueryHint>(elements.size());
        for (JaxbQueryHint hint : elements) {
            AnnotationDescriptor hintDescriptor = new AnnotationDescriptor(QueryHint.class);
            String value = hint.getName();
            if (value == null) {
                throw new AnnotationException("<hint> without name. Activate schema validation for more information");
            }
            hintDescriptor.setValue("name", (Object)value);
            value = hint.getValue();
            if (value == null) {
                throw new AnnotationException("<hint> without value. Activate schema validation for more information");
            }
            hintDescriptor.setValue("value", (Object)value);
            queryHints.add((QueryHint)AnnotationFactory.create((AnnotationDescriptor)hintDescriptor));
        }
        ann.setValue("hints", (Object)queryHints.toArray(new QueryHint[queryHints.size()]));
    }

    public static List<NamedQuery> buildNamedQueries(List<JaxbNamedQuery> elements, XMLContext.Default defaults, ClassLoaderAccess classLoaderAccess) {
        ArrayList<NamedQuery> namedQueries = new ArrayList<NamedQuery>();
        for (JaxbNamedQuery element : elements) {
            AnnotationDescriptor ann = new AnnotationDescriptor(NamedQuery.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ann, "name", element.getName(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ann, "query", element.getQuery(), true);
            JPAXMLOverriddenAnnotationReader.buildQueryHints(element.getHint(), ann);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ann, "lock-mode", element.getLockMode(), false);
            namedQueries.add((NamedQuery)AnnotationFactory.create((AnnotationDescriptor)ann));
        }
        return namedQueries;
    }

    public static List<NamedNativeQuery> buildNamedNativeQueries(List<JaxbNamedNativeQuery> elements, XMLContext.Default defaults, ClassLoaderAccess classLoaderAccess) {
        ArrayList<NamedNativeQuery> namedQueries = new ArrayList<NamedNativeQuery>();
        for (JaxbNamedNativeQuery element : elements) {
            AnnotationDescriptor ann = new AnnotationDescriptor(NamedNativeQuery.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ann, "name", element.getName(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ann, "query", element.getQuery(), true);
            JPAXMLOverriddenAnnotationReader.buildQueryHints(element.getHint(), ann);
            String clazzName = element.getResultClass();
            if (StringHelper.isNotEmpty(clazzName)) {
                Class clazz;
                try {
                    clazz = classLoaderAccess.classForName(XMLContext.buildSafeClassName(clazzName, defaults));
                }
                catch (ClassLoadingException e) {
                    throw new AnnotationException("Unable to find entity-class: " + clazzName, (Throwable)((Object)e));
                }
                ann.setValue("resultClass", clazz);
            }
            JPAXMLOverriddenAnnotationReader.copyAttribute(ann, "result-set-mapping", element.getResultSetMapping(), false);
            namedQueries.add((NamedNativeQuery)AnnotationFactory.create((AnnotationDescriptor)ann));
        }
        return namedQueries;
    }

    private TableGenerator getTableGenerator(ManagedType root, XMLContext.Default defaults) {
        return this.getTableGenerator(root instanceof JaxbEntity ? ((JaxbEntity)root).getTableGenerator() : null, defaults);
    }

    private TableGenerator getTableGenerator(JaxbTableGenerator element, XMLContext.Default defaults) {
        if (element != null) {
            return JPAXMLOverriddenAnnotationReader.buildTableGeneratorAnnotation(element, defaults);
        }
        if (defaults.canUseJavaAnnotations() && this.isPhysicalAnnotationPresent(TableGenerator.class)) {
            TableGenerator tableAnn = this.getPhysicalAnnotation(TableGenerator.class);
            if (StringHelper.isNotEmpty(defaults.getSchema()) || StringHelper.isNotEmpty(defaults.getCatalog())) {
                AnnotationDescriptor annotation = new AnnotationDescriptor(TableGenerator.class);
                annotation.setValue("name", (Object)tableAnn.name());
                annotation.setValue("table", (Object)tableAnn.table());
                annotation.setValue("catalog", (Object)tableAnn.table());
                if (StringHelper.isEmpty((String)annotation.valueOf("catalog")) && StringHelper.isNotEmpty(defaults.getCatalog())) {
                    annotation.setValue("catalog", (Object)defaults.getCatalog());
                }
                annotation.setValue("schema", (Object)tableAnn.table());
                if (StringHelper.isEmpty((String)annotation.valueOf("schema")) && StringHelper.isNotEmpty(defaults.getSchema())) {
                    annotation.setValue("catalog", (Object)defaults.getSchema());
                }
                annotation.setValue("pkColumnName", (Object)tableAnn.pkColumnName());
                annotation.setValue("valueColumnName", (Object)tableAnn.valueColumnName());
                annotation.setValue("pkColumnValue", (Object)tableAnn.pkColumnValue());
                annotation.setValue("initialValue", (Object)tableAnn.initialValue());
                annotation.setValue("allocationSize", (Object)tableAnn.allocationSize());
                annotation.setValue("uniqueConstraints", (Object)tableAnn.uniqueConstraints());
                return (TableGenerator)AnnotationFactory.create((AnnotationDescriptor)annotation);
            }
            return tableAnn;
        }
        return null;
    }

    public static TableGenerator buildTableGeneratorAnnotation(JaxbTableGenerator element, XMLContext.Default defaults) {
        AnnotationDescriptor ad = new AnnotationDescriptor(TableGenerator.class);
        JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "name", element.getName(), false);
        JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "table", element.getTable(), false);
        JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "catalog", element.getCatalog(), false);
        JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "schema", element.getSchema(), false);
        JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "pk-column-name", element.getPkColumnName(), false);
        JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "value-column-name", element.getValueColumnName(), false);
        JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "pk-column-value", element.getPkColumnValue(), false);
        JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "initial-value", element.getInitialValue(), false);
        JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "allocation-size", element.getAllocationSize(), false);
        JPAXMLOverriddenAnnotationReader.buildUniqueConstraints(ad, element.getUniqueConstraint());
        if (StringHelper.isEmpty((String)ad.valueOf("schema")) && StringHelper.isNotEmpty(defaults.getSchema())) {
            ad.setValue("schema", (Object)defaults.getSchema());
        }
        if (StringHelper.isEmpty((String)ad.valueOf("catalog")) && StringHelper.isNotEmpty(defaults.getCatalog())) {
            ad.setValue("catalog", (Object)defaults.getCatalog());
        }
        return (TableGenerator)AnnotationFactory.create((AnnotationDescriptor)ad);
    }

    private SequenceGenerator getSequenceGenerator(ManagedType root, XMLContext.Default defaults) {
        return this.getSequenceGenerator(root instanceof JaxbEntity ? ((JaxbEntity)root).getSequenceGenerator() : null, defaults);
    }

    private SequenceGenerator getSequenceGenerator(JaxbSequenceGenerator element, XMLContext.Default defaults) {
        if (element != null) {
            return JPAXMLOverriddenAnnotationReader.buildSequenceGeneratorAnnotation(element);
        }
        if (defaults.canUseJavaAnnotations()) {
            return this.getPhysicalAnnotation(SequenceGenerator.class);
        }
        return null;
    }

    public static SequenceGenerator buildSequenceGeneratorAnnotation(JaxbSequenceGenerator element) {
        if (element != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(SequenceGenerator.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "name", element.getName(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "sequence-name", element.getSequenceName(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "initial-value", element.getInitialValue(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "allocation-size", element.getAllocationSize(), false);
            return (SequenceGenerator)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        return null;
    }

    private DiscriminatorColumn getDiscriminatorColumn(ManagedType root, XMLContext.Default defaults) {
        JaxbDiscriminatorColumn element;
        JaxbDiscriminatorColumn jaxbDiscriminatorColumn = element = root instanceof JaxbEntity ? ((JaxbEntity)root).getDiscriminatorColumn() : null;
        if (element != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(DiscriminatorColumn.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "name", element.getName(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "column-definition", element.getColumnDefinition(), false);
            DiscriminatorType type = element.getDiscriminatorType();
            if (type != null) {
                ad.setValue("discriminatorType", (Object)type);
            }
            JPAXMLOverriddenAnnotationReader.copyAttribute(ad, "length", element.getLength(), false);
            return (DiscriminatorColumn)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        if (defaults.canUseJavaAnnotations()) {
            return this.getPhysicalAnnotation(DiscriminatorColumn.class);
        }
        return null;
    }

    private DiscriminatorValue getDiscriminatorValue(ManagedType root, XMLContext.Default defaults) {
        String element;
        String string = element = root instanceof JaxbEntity ? ((JaxbEntity)root).getDiscriminatorValue() : null;
        if (element != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(DiscriminatorValue.class);
            ad.setValue("value", (Object)element);
            return (DiscriminatorValue)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        if (defaults.canUseJavaAnnotations()) {
            return this.getPhysicalAnnotation(DiscriminatorValue.class);
        }
        return null;
    }

    private Inheritance getInheritance(ManagedType root, XMLContext.Default defaults) {
        JaxbInheritance element;
        JaxbInheritance jaxbInheritance = element = root instanceof JaxbEntity ? ((JaxbEntity)root).getInheritance() : null;
        if (element != null) {
            AnnotationDescriptor ad = new AnnotationDescriptor(Inheritance.class);
            InheritanceType strategy = element.getStrategy();
            if (strategy != null) {
                ad.setValue("strategy", (Object)strategy);
            }
            return (Inheritance)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        if (defaults.canUseJavaAnnotations()) {
            return this.getPhysicalAnnotation(Inheritance.class);
        }
        return null;
    }

    private IdClass getIdClass(ManagedType root, XMLContext.Default defaults) {
        JaxbIdClass element;
        JaxbIdClass jaxbIdClass = element = root instanceof EntityOrMappedSuperclass ? ((EntityOrMappedSuperclass)root).getIdClass() : null;
        if (element != null) {
            String className = element.getClazz();
            if (className != null) {
                Class clazz;
                AnnotationDescriptor ad = new AnnotationDescriptor(IdClass.class);
                try {
                    clazz = this.classLoaderAccess.classForName(XMLContext.buildSafeClassName(className, defaults));
                }
                catch (ClassLoadingException e) {
                    throw new AnnotationException("Unable to find id-class: " + className, (Throwable)((Object)e));
                }
                ad.setValue("value", clazz);
                return (IdClass)AnnotationFactory.create((AnnotationDescriptor)ad);
            }
            throw new AnnotationException("id-class without class. Activate schema validation for more information");
        }
        if (defaults.canUseJavaAnnotations()) {
            return this.getPhysicalAnnotation(IdClass.class);
        }
        return null;
    }

    private PrimaryKeyJoinColumns getPrimaryKeyJoinColumns(ManagedType root, XMLContext.Default defaults) {
        return this.getPrimaryKeyJoinColumns(root instanceof JaxbEntity ? ((JaxbEntity)root).getPrimaryKeyJoinColumn() : Collections.emptyList(), defaults, true);
    }

    private PrimaryKeyJoinColumns getPrimaryKeyJoinColumns(List<JaxbPrimaryKeyJoinColumn> elements, XMLContext.Default defaults, boolean mergeWithAnnotations) {
        PrimaryKeyJoinColumn[] columns = this.buildPrimaryKeyJoinColumns(elements);
        if (mergeWithAnnotations && columns.length == 0 && defaults.canUseJavaAnnotations()) {
            PrimaryKeyJoinColumn annotation = this.getPhysicalAnnotation(PrimaryKeyJoinColumn.class);
            if (annotation != null) {
                columns = new PrimaryKeyJoinColumn[]{annotation};
            } else {
                PrimaryKeyJoinColumns annotations = this.getPhysicalAnnotation(PrimaryKeyJoinColumns.class);
                PrimaryKeyJoinColumn[] primaryKeyJoinColumnArray = columns = annotations != null ? annotations.value() : columns;
            }
        }
        if (columns.length > 0) {
            AnnotationDescriptor ad = new AnnotationDescriptor(PrimaryKeyJoinColumns.class);
            ad.setValue("value", (Object)columns);
            return (PrimaryKeyJoinColumns)AnnotationFactory.create((AnnotationDescriptor)ad);
        }
        return null;
    }

    private Entity getEntity(ManagedType element, XMLContext.Default defaults) {
        if (element == null) {
            return defaults.canUseJavaAnnotations() ? this.getPhysicalAnnotation(Entity.class) : null;
        }
        if (element instanceof JaxbEntity) {
            Entity javaAnn;
            JaxbEntity entityElement = (JaxbEntity)element;
            AnnotationDescriptor entity = new AnnotationDescriptor(Entity.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(entity, "name", entityElement.getName(), false);
            if (defaults.canUseJavaAnnotations() && StringHelper.isEmpty((String)entity.valueOf("name")) && (javaAnn = this.getPhysicalAnnotation(Entity.class)) != null) {
                entity.setValue("name", (Object)javaAnn.name());
            }
            return (Entity)AnnotationFactory.create((AnnotationDescriptor)entity);
        }
        return null;
    }

    private MappedSuperclass getMappedSuperclass(ManagedType element, XMLContext.Default defaults) {
        if (element == null) {
            return defaults.canUseJavaAnnotations() ? this.getPhysicalAnnotation(MappedSuperclass.class) : null;
        }
        if (element instanceof JaxbMappedSuperclass) {
            AnnotationDescriptor entity = new AnnotationDescriptor(MappedSuperclass.class);
            return (MappedSuperclass)AnnotationFactory.create((AnnotationDescriptor)entity);
        }
        return null;
    }

    private Embeddable getEmbeddable(ManagedType element, XMLContext.Default defaults) {
        if (element == null) {
            return defaults.canUseJavaAnnotations() ? this.getPhysicalAnnotation(Embeddable.class) : null;
        }
        if (element instanceof JaxbEmbeddable) {
            AnnotationDescriptor entity = new AnnotationDescriptor(Embeddable.class);
            return (Embeddable)AnnotationFactory.create((AnnotationDescriptor)entity);
        }
        return null;
    }

    private Table getTable(ManagedType root, XMLContext.Default defaults) {
        JaxbTable element;
        JaxbTable jaxbTable = element = root instanceof JaxbEntity ? ((JaxbEntity)root).getTable() : null;
        if (element == null) {
            if (StringHelper.isNotEmpty(defaults.getCatalog()) || StringHelper.isNotEmpty(defaults.getSchema())) {
                Table table;
                AnnotationDescriptor annotation = new AnnotationDescriptor(Table.class);
                if (defaults.canUseJavaAnnotations() && (table = this.getPhysicalAnnotation(Table.class)) != null) {
                    annotation.setValue("name", (Object)table.name());
                    annotation.setValue("schema", (Object)table.schema());
                    annotation.setValue("catalog", (Object)table.catalog());
                    annotation.setValue("uniqueConstraints", (Object)table.uniqueConstraints());
                    annotation.setValue("indexes", (Object)table.indexes());
                }
                if (StringHelper.isEmpty((String)annotation.valueOf("schema")) && StringHelper.isNotEmpty(defaults.getSchema())) {
                    annotation.setValue("schema", (Object)defaults.getSchema());
                }
                if (StringHelper.isEmpty((String)annotation.valueOf("catalog")) && StringHelper.isNotEmpty(defaults.getCatalog())) {
                    annotation.setValue("catalog", (Object)defaults.getCatalog());
                }
                return (Table)AnnotationFactory.create((AnnotationDescriptor)annotation);
            }
            if (defaults.canUseJavaAnnotations()) {
                return this.getPhysicalAnnotation(Table.class);
            }
            return null;
        }
        AnnotationDescriptor annotation = new AnnotationDescriptor(Table.class);
        JPAXMLOverriddenAnnotationReader.copyAttribute(annotation, "name", element.getName(), false);
        JPAXMLOverriddenAnnotationReader.copyAttribute(annotation, "catalog", element.getCatalog(), false);
        if (StringHelper.isNotEmpty(defaults.getCatalog()) && StringHelper.isEmpty((String)annotation.valueOf("catalog"))) {
            annotation.setValue("catalog", (Object)defaults.getCatalog());
        }
        JPAXMLOverriddenAnnotationReader.copyAttribute(annotation, "schema", element.getSchema(), false);
        if (StringHelper.isNotEmpty(defaults.getSchema()) && StringHelper.isEmpty((String)annotation.valueOf("schema"))) {
            annotation.setValue("schema", (Object)defaults.getSchema());
        }
        JPAXMLOverriddenAnnotationReader.buildUniqueConstraints(annotation, element.getUniqueConstraint());
        JPAXMLOverriddenAnnotationReader.buildIndex(annotation, element.getIndex());
        return (Table)AnnotationFactory.create((AnnotationDescriptor)annotation);
    }

    private SecondaryTables getSecondaryTables(ManagedType root, XMLContext.Default defaults) {
        List<JaxbSecondaryTable> elements = root instanceof JaxbEntity ? ((JaxbEntity)root).getSecondaryTable() : Collections.emptyList();
        ArrayList<SecondaryTable> secondaryTables = new ArrayList<SecondaryTable>(3);
        for (JaxbSecondaryTable element : elements) {
            AnnotationDescriptor annotation = new AnnotationDescriptor(SecondaryTable.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(annotation, "name", element.getName(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(annotation, "catalog", element.getCatalog(), false);
            if (StringHelper.isNotEmpty(defaults.getCatalog()) && StringHelper.isEmpty((String)annotation.valueOf("catalog"))) {
                annotation.setValue("catalog", (Object)defaults.getCatalog());
            }
            JPAXMLOverriddenAnnotationReader.copyAttribute(annotation, "schema", element.getSchema(), false);
            if (StringHelper.isNotEmpty(defaults.getSchema()) && StringHelper.isEmpty((String)annotation.valueOf("schema"))) {
                annotation.setValue("schema", (Object)defaults.getSchema());
            }
            JPAXMLOverriddenAnnotationReader.buildUniqueConstraints(annotation, element.getUniqueConstraint());
            JPAXMLOverriddenAnnotationReader.buildIndex(annotation, element.getIndex());
            annotation.setValue("pkJoinColumns", (Object)this.buildPrimaryKeyJoinColumns(element.getPrimaryKeyJoinColumn()));
            secondaryTables.add((SecondaryTable)AnnotationFactory.create((AnnotationDescriptor)annotation));
        }
        if (secondaryTables.size() == 0 && defaults.canUseJavaAnnotations()) {
            SecondaryTable secTableAnn = this.getPhysicalAnnotation(SecondaryTable.class);
            this.overridesDefaultInSecondaryTable(secTableAnn, defaults, secondaryTables);
            SecondaryTables secTablesAnn = this.getPhysicalAnnotation(SecondaryTables.class);
            if (secTablesAnn != null) {
                for (SecondaryTable table : secTablesAnn.value()) {
                    this.overridesDefaultInSecondaryTable(table, defaults, secondaryTables);
                }
            }
        }
        if (secondaryTables.size() > 0) {
            AnnotationDescriptor descriptor = new AnnotationDescriptor(SecondaryTables.class);
            descriptor.setValue("value", (Object)secondaryTables.toArray(new SecondaryTable[secondaryTables.size()]));
            return (SecondaryTables)AnnotationFactory.create((AnnotationDescriptor)descriptor);
        }
        return null;
    }

    private void overridesDefaultInSecondaryTable(SecondaryTable secTableAnn, XMLContext.Default defaults, List<SecondaryTable> secondaryTables) {
        if (secTableAnn != null) {
            if (StringHelper.isNotEmpty(defaults.getCatalog()) || StringHelper.isNotEmpty(defaults.getSchema())) {
                AnnotationDescriptor annotation = new AnnotationDescriptor(SecondaryTable.class);
                annotation.setValue("name", (Object)secTableAnn.name());
                annotation.setValue("schema", (Object)secTableAnn.schema());
                annotation.setValue("catalog", (Object)secTableAnn.catalog());
                annotation.setValue("uniqueConstraints", (Object)secTableAnn.uniqueConstraints());
                annotation.setValue("pkJoinColumns", (Object)secTableAnn.pkJoinColumns());
                if (StringHelper.isEmpty((String)annotation.valueOf("schema")) && StringHelper.isNotEmpty(defaults.getSchema())) {
                    annotation.setValue("schema", (Object)defaults.getSchema());
                }
                if (StringHelper.isEmpty((String)annotation.valueOf("catalog")) && StringHelper.isNotEmpty(defaults.getCatalog())) {
                    annotation.setValue("catalog", (Object)defaults.getCatalog());
                }
                secondaryTables.add((SecondaryTable)AnnotationFactory.create((AnnotationDescriptor)annotation));
            } else {
                secondaryTables.add(secTableAnn);
            }
        }
    }

    private static void buildIndex(AnnotationDescriptor annotation, List<JaxbIndex> elements) {
        Index[] indexes = new Index[elements.size()];
        int i = 0;
        for (JaxbIndex element : elements) {
            AnnotationDescriptor indexAnn = new AnnotationDescriptor(Index.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(indexAnn, "name", element.getName(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(indexAnn, "column-list", element.getColumnList(), true);
            JPAXMLOverriddenAnnotationReader.copyAttribute(indexAnn, "unique", element.isUnique(), false);
            indexes[i++] = (Index)AnnotationFactory.create((AnnotationDescriptor)indexAnn);
        }
        annotation.setValue("indexes", (Object)indexes);
    }

    private static void buildUniqueConstraints(AnnotationDescriptor annotation, List<JaxbUniqueConstraint> elements) {
        UniqueConstraint[] uniqueConstraints = new UniqueConstraint[elements.size()];
        int i = 0;
        for (JaxbUniqueConstraint element : elements) {
            String[] columnNames = element.getColumnName().toArray(new String[0]);
            AnnotationDescriptor ucAnn = new AnnotationDescriptor(UniqueConstraint.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(ucAnn, "name", element.getName(), false);
            ucAnn.setValue("columnNames", (Object)columnNames);
            uniqueConstraints[i++] = (UniqueConstraint)AnnotationFactory.create((AnnotationDescriptor)ucAnn);
        }
        annotation.setValue("uniqueConstraints", (Object)uniqueConstraints);
    }

    private PrimaryKeyJoinColumn[] buildPrimaryKeyJoinColumns(List<JaxbPrimaryKeyJoinColumn> elements) {
        PrimaryKeyJoinColumn[] pkJoinColumns = new PrimaryKeyJoinColumn[elements.size()];
        int i = 0;
        for (JaxbPrimaryKeyJoinColumn element : elements) {
            AnnotationDescriptor pkAnn = new AnnotationDescriptor(PrimaryKeyJoinColumn.class);
            JPAXMLOverriddenAnnotationReader.copyAttribute(pkAnn, "name", element.getName(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(pkAnn, "referenced-column-name", element.getReferencedColumnName(), false);
            JPAXMLOverriddenAnnotationReader.copyAttribute(pkAnn, "column-definition", element.getColumnDefinition(), false);
            pkJoinColumns[i++] = (PrimaryKeyJoinColumn)AnnotationFactory.create((AnnotationDescriptor)pkAnn);
        }
        return pkJoinColumns;
    }

    private static void copyAttribute(AnnotationDescriptor annotation, String attributeName, Object attributeValue, boolean mandatory) {
        JPAXMLOverriddenAnnotationReader.copyAttribute(annotation, JPAXMLOverriddenAnnotationReader.getJavaAttributeNameFromXMLOne(attributeName), attributeName, attributeValue, mandatory);
    }

    private static void copyAttribute(AnnotationDescriptor annotation, String annotationAttributeName, Object attributeName, Object attributeValue, boolean mandatory) {
        if (attributeValue != null) {
            annotation.setValue(annotationAttributeName, attributeValue);
        } else if (mandatory) {
            throw new AnnotationException(annotationToXml.getOrDefault(annotation.type(), annotation.type().getName()) + "." + attributeName + " is mandatory in XML overriding. " + SCHEMA_VALIDATION);
        }
    }

    private static String getJavaAttributeNameFromXMLOne(String attributeName) {
        StringBuilder annotationAttributeName = new StringBuilder(attributeName);
        int index = annotationAttributeName.indexOf(WORD_SEPARATOR);
        while (index != -1) {
            annotationAttributeName.deleteCharAt(index);
            annotationAttributeName.setCharAt(index, Character.toUpperCase(annotationAttributeName.charAt(index)));
            index = annotationAttributeName.indexOf(WORD_SEPARATOR);
        }
        return annotationAttributeName.toString();
    }

    private <T extends Annotation> T getPhysicalAnnotation(Class<T> annotationType) {
        return this.element.getAnnotation(annotationType);
    }

    private <T extends Annotation> boolean isPhysicalAnnotationPresent(Class<T> annotationType) {
        return this.element.isAnnotationPresent(annotationType);
    }

    private Annotation[] getPhysicalAnnotations() {
        return this.element.getAnnotations();
    }

    static {
        annotationToXml.put(Entity.class, "entity");
        annotationToXml.put(MappedSuperclass.class, "mapped-superclass");
        annotationToXml.put(Embeddable.class, "embeddable");
        annotationToXml.put(Table.class, "table");
        annotationToXml.put(SecondaryTable.class, "secondary-table");
        annotationToXml.put(SecondaryTables.class, "secondary-table");
        annotationToXml.put(PrimaryKeyJoinColumn.class, "primary-key-join-column");
        annotationToXml.put(PrimaryKeyJoinColumns.class, "primary-key-join-column");
        annotationToXml.put(IdClass.class, "id-class");
        annotationToXml.put(Inheritance.class, "inheritance");
        annotationToXml.put(DiscriminatorValue.class, "discriminator-value");
        annotationToXml.put(DiscriminatorColumn.class, "discriminator-column");
        annotationToXml.put(SequenceGenerator.class, "sequence-generator");
        annotationToXml.put(TableGenerator.class, "table-generator");
        annotationToXml.put(NamedEntityGraph.class, "named-entity-graph");
        annotationToXml.put(NamedEntityGraphs.class, "named-entity-graph");
        annotationToXml.put(NamedQuery.class, "named-query");
        annotationToXml.put(NamedQueries.class, "named-query");
        annotationToXml.put(NamedNativeQuery.class, "named-native-query");
        annotationToXml.put(NamedNativeQueries.class, "named-native-query");
        annotationToXml.put(NamedStoredProcedureQuery.class, "named-stored-procedure-query");
        annotationToXml.put(NamedStoredProcedureQueries.class, "named-stored-procedure-query");
        annotationToXml.put(SqlResultSetMapping.class, "sql-result-set-mapping");
        annotationToXml.put(SqlResultSetMappings.class, "sql-result-set-mapping");
        annotationToXml.put(ExcludeDefaultListeners.class, "exclude-default-listeners");
        annotationToXml.put(ExcludeSuperclassListeners.class, "exclude-superclass-listeners");
        annotationToXml.put(AccessType.class, "access");
        annotationToXml.put(AttributeOverride.class, "attribute-override");
        annotationToXml.put(AttributeOverrides.class, "attribute-override");
        annotationToXml.put(AttributeOverride.class, "association-override");
        annotationToXml.put(AttributeOverrides.class, "association-override");
        annotationToXml.put(AttributeOverride.class, "map-key-attribute-override");
        annotationToXml.put(AttributeOverrides.class, "map-key-attribute-override");
        annotationToXml.put(Id.class, "id");
        annotationToXml.put(EmbeddedId.class, "embedded-id");
        annotationToXml.put(GeneratedValue.class, "generated-value");
        annotationToXml.put(Column.class, "column");
        annotationToXml.put(Columns.class, "column");
        annotationToXml.put(Temporal.class, "temporal");
        annotationToXml.put(Lob.class, "lob");
        annotationToXml.put(Enumerated.class, "enumerated");
        annotationToXml.put(Version.class, "version");
        annotationToXml.put(Transient.class, "transient");
        annotationToXml.put(Basic.class, "basic");
        annotationToXml.put(Embedded.class, "embedded");
        annotationToXml.put(ManyToOne.class, "many-to-one");
        annotationToXml.put(OneToOne.class, "one-to-one");
        annotationToXml.put(OneToMany.class, "one-to-many");
        annotationToXml.put(ManyToMany.class, "many-to-many");
        annotationToXml.put(Any.class, "any");
        annotationToXml.put(ManyToAny.class, "many-to-any");
        annotationToXml.put(JoinTable.class, "join-table");
        annotationToXml.put(JoinColumn.class, "join-column");
        annotationToXml.put(JoinColumns.class, "join-column");
        annotationToXml.put(MapKey.class, "map-key");
        annotationToXml.put(OrderBy.class, "order-by");
        annotationToXml.put(EntityListeners.class, "entity-listeners");
        annotationToXml.put(PrePersist.class, "pre-persist");
        annotationToXml.put(PreRemove.class, "pre-remove");
        annotationToXml.put(PreUpdate.class, "pre-update");
        annotationToXml.put(PostPersist.class, "post-persist");
        annotationToXml.put(PostRemove.class, "post-remove");
        annotationToXml.put(PostUpdate.class, "post-update");
        annotationToXml.put(PostLoad.class, "post-load");
        annotationToXml.put(CollectionTable.class, "collection-table");
        annotationToXml.put(MapKeyClass.class, "map-key-class");
        annotationToXml.put(MapKeyTemporal.class, "map-key-temporal");
        annotationToXml.put(MapKeyEnumerated.class, "map-key-enumerated");
        annotationToXml.put(MapKeyColumn.class, "map-key-column");
        annotationToXml.put(MapKeyJoinColumn.class, "map-key-join-column");
        annotationToXml.put(MapKeyJoinColumns.class, "map-key-join-column");
        annotationToXml.put(OrderColumn.class, "order-column");
        annotationToXml.put(Cacheable.class, "cacheable");
        annotationToXml.put(Index.class, "index");
        annotationToXml.put(ForeignKey.class, "foreign-key");
        annotationToXml.put(Convert.class, "convert");
        annotationToXml.put(Converts.class, "convert");
        annotationToXml.put(ConstructorResult.class, "constructor-result");
    }

    private static enum PropertyType {
        PROPERTY,
        FIELD,
        METHOD;

    }
}

