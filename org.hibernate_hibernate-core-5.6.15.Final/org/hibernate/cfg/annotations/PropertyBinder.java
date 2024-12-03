/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EmbeddedId
 *  javax.persistence.Id
 *  javax.persistence.Lob
 *  javax.persistence.Version
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XProperty
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg.annotations;

import java.lang.annotation.Annotation;
import java.util.Map;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Version;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.annotations.AttributeAccessor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.annotations.ValueGenerationType;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.AccessType;
import org.hibernate.cfg.AnnotationBinder;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.InheritanceState;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.cfg.PropertyPreloadedData;
import org.hibernate.cfg.annotations.EntityBinder;
import org.hibernate.cfg.annotations.SimpleValueBinder;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.MappedSuperclass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.hibernate.tuple.AnnotationValueGeneration;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.ValueGeneration;
import org.hibernate.tuple.ValueGenerator;
import org.jboss.logging.Logger;

public class PropertyBinder {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)PropertyBinder.class.getName());
    private MetadataBuildingContext buildingContext;
    private String name;
    private String returnedClassName;
    private boolean lazy;
    private String lazyGroup;
    private AccessType accessType;
    private Ejb3Column[] columns;
    private PropertyHolder holder;
    private Value value;
    private boolean insertable = true;
    private boolean updatable = true;
    private String cascade;
    private SimpleValueBinder simpleValueBinder;
    private XClass declaringClass;
    private boolean declaringClassSet;
    private boolean embedded;
    private EntityBinder entityBinder;
    private boolean isXToMany;
    private String referencedEntityName;
    private XProperty property;
    private XClass returnedClass;
    private boolean isId;
    private Map<XClass, InheritanceState> inheritanceStatePerClass;
    private Property mappingProperty;

    public void setReferencedEntityName(String referencedEntityName) {
        this.referencedEntityName = referencedEntityName;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public void setEntityBinder(EntityBinder entityBinder) {
        this.entityBinder = entityBinder;
    }

    public void setInsertable(boolean insertable) {
        this.insertable = insertable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReturnedClassName(String returnedClassName) {
        this.returnedClassName = returnedClassName;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public void setLazyGroup(String lazyGroup) {
        this.lazyGroup = lazyGroup;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public void setColumns(Ejb3Column[] columns) {
        this.insertable = columns[0].isInsertable();
        this.updatable = columns[0].isUpdatable();
        this.columns = columns;
    }

    public void setHolder(PropertyHolder holder) {
        this.holder = holder;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public void setCascade(String cascadeStrategy) {
        this.cascade = cascadeStrategy;
    }

    public void setBuildingContext(MetadataBuildingContext buildingContext) {
        this.buildingContext = buildingContext;
    }

    public void setDeclaringClass(XClass declaringClass) {
        this.declaringClass = declaringClass;
        this.declaringClassSet = true;
    }

    private void validateBind() {
        if (this.property.isAnnotationPresent(Immutable.class)) {
            throw new AnnotationException("@Immutable on property not allowed. Only allowed on entity level or on a collection.");
        }
        if (!this.declaringClassSet) {
            throw new AssertionFailure("declaringClass has not been set before a bind");
        }
    }

    private void validateMake() {
    }

    private Property makePropertyAndValue() {
        this.validateBind();
        LOG.debugf("MetadataSourceProcessor property %s with lazy=%s", this.name, this.lazy);
        String containerClassName = this.holder.getClassName();
        this.holder.startingProperty(this.property);
        this.simpleValueBinder = new SimpleValueBinder();
        this.simpleValueBinder.setBuildingContext(this.buildingContext);
        this.simpleValueBinder.setPropertyName(this.name);
        this.simpleValueBinder.setReturnedClassName(this.returnedClassName);
        this.simpleValueBinder.setColumns(this.columns);
        this.simpleValueBinder.setPersistentClassName(containerClassName);
        this.simpleValueBinder.setType(this.property, this.returnedClass, containerClassName, this.holder.resolveAttributeConverterDescriptor(this.property));
        this.simpleValueBinder.setReferencedEntityName(this.referencedEntityName);
        this.simpleValueBinder.setAccessType(this.accessType);
        SimpleValue propertyValue = this.simpleValueBinder.make();
        this.setValue(propertyValue);
        return this.makeProperty();
    }

    public Property makePropertyAndBind() {
        return this.bind(this.makeProperty());
    }

    public Property makePropertyValueAndBind() {
        return this.bind(this.makePropertyAndValue());
    }

    public void setXToMany(boolean xToMany) {
        this.isXToMany = xToMany;
    }

    private Property bind(Property prop) {
        if (this.isId) {
            RootClass rootClass = (RootClass)this.holder.getPersistentClass();
            if (this.isXToMany || this.entityBinder.wrapIdsInEmbeddedComponents()) {
                Component identifier = (Component)rootClass.getIdentifier();
                if (identifier == null) {
                    identifier = AnnotationBinder.createComponent(this.holder, new PropertyPreloadedData(null, null, null), true, false, this.buildingContext);
                    rootClass.setIdentifier(identifier);
                    identifier.setNullValue("undefined");
                    rootClass.setEmbeddedIdentifier(true);
                    rootClass.setIdentifierMapper(identifier);
                }
                identifier.addProperty(prop);
            } else {
                rootClass.setIdentifier((KeyValue)this.getValue());
                if (this.embedded) {
                    rootClass.setEmbeddedIdentifier(true);
                } else {
                    rootClass.setIdentifierProperty(prop);
                    MappedSuperclass superclass = BinderHelper.getMappedSuperclassOrNull(this.declaringClass, this.inheritanceStatePerClass, this.buildingContext);
                    if (superclass != null) {
                        superclass.setDeclaredIdentifierProperty(prop);
                    } else {
                        rootClass.setDeclaredIdentifierProperty(prop);
                    }
                }
            }
        } else {
            this.holder.addProperty(prop, this.columns, this.declaringClass);
        }
        return prop;
    }

    public Property makeProperty() {
        NaturalId naturalId;
        this.validateMake();
        LOG.debugf("Building property %s", this.name);
        Property prop = new Property();
        prop.setName(this.name);
        prop.setValue(this.value);
        prop.setLazy(this.lazy);
        prop.setLazyGroup(this.lazyGroup);
        prop.setCascade(this.cascade);
        prop.setPropertyAccessorName(this.accessType.getType());
        if (this.property != null) {
            if (this.entityBinder != null) {
                prop.setValueGenerationStrategy(this.determineValueGenerationStrategy(this.property));
            }
            if (this.property.isAnnotationPresent(AttributeAccessor.class)) {
                AttributeAccessor accessor = (AttributeAccessor)this.property.getAnnotation(AttributeAccessor.class);
                prop.setPropertyAccessorName(accessor.value());
            }
        }
        NaturalId naturalId2 = naturalId = this.property != null ? (NaturalId)this.property.getAnnotation(NaturalId.class) : null;
        if (naturalId != null) {
            if (!this.entityBinder.isRootEntity()) {
                throw new AnnotationException("@NaturalId only valid on root entity (or its @MappedSuperclasses)");
            }
            if (!naturalId.mutable()) {
                this.updatable = false;
            }
            prop.setNaturalIdentifier(true);
        }
        Lob lob = this.property != null ? (Lob)this.property.getAnnotation(Lob.class) : null;
        prop.setLob(lob != null);
        prop.setInsertable(this.insertable);
        prop.setUpdateable(this.updatable);
        if (Collection.class.isInstance(this.value)) {
            prop.setOptimisticLocked(((Collection)this.value).isOptimisticLocked());
        } else {
            boolean isOwnedValue;
            OptimisticLock lockAnn;
            OptimisticLock optimisticLock = lockAnn = this.property != null ? (OptimisticLock)this.property.getAnnotation(OptimisticLock.class) : null;
            if (lockAnn != null && lockAnn.excluded() && (this.property.isAnnotationPresent(Version.class) || this.property.isAnnotationPresent(Id.class) || this.property.isAnnotationPresent(EmbeddedId.class))) {
                throw new AnnotationException("@OptimisticLock.exclude=true incompatible with @Id, @EmbeddedId and @Version: " + StringHelper.qualify(this.holder.getPath(), this.name));
            }
            boolean bl = isOwnedValue = !this.isToOneValue(this.value) || this.insertable;
            boolean includeInOptimisticLockChecks = lockAnn != null ? !lockAnn.excluded() : isOwnedValue;
            prop.setOptimisticLocked(includeInOptimisticLockChecks);
        }
        LOG.tracev("Cascading {0} with {1}", this.name, this.cascade);
        this.mappingProperty = prop;
        return prop;
    }

    private ValueGeneration determineValueGenerationStrategy(XProperty property) {
        ValueGeneration valueGeneration = this.getValueGenerationFromAnnotations(property);
        if (valueGeneration == null) {
            return NoValueGeneration.INSTANCE;
        }
        GenerationTiming when = valueGeneration.getGenerationTiming();
        if (valueGeneration.getValueGenerator() == null) {
            this.insertable = false;
            if (when == GenerationTiming.ALWAYS) {
                this.updatable = false;
            }
        }
        return valueGeneration;
    }

    private ValueGeneration getValueGenerationFromAnnotations(XProperty property) {
        AnnotationValueGeneration<Annotation> valueGeneration = null;
        for (Annotation annotation : property.getAnnotations()) {
            AnnotationValueGeneration<Annotation> candidate = this.getValueGenerationFromAnnotation(property, annotation);
            if (candidate == null) continue;
            if (valueGeneration != null) {
                throw new AnnotationException("Only one generator annotation is allowed:" + StringHelper.qualify(this.holder.getPath(), this.name));
            }
            valueGeneration = candidate;
        }
        return valueGeneration;
    }

    private <A extends Annotation> AnnotationValueGeneration<A> getValueGenerationFromAnnotation(XProperty property, A annotation) {
        ValueGenerationType generatorAnnotation = annotation.annotationType().getAnnotation(ValueGenerationType.class);
        if (generatorAnnotation == null) {
            return null;
        }
        Class<? extends AnnotationValueGeneration<?>> generationType = generatorAnnotation.generatedBy();
        AnnotationValueGeneration<A> valueGeneration = this.instantiateAndInitializeValueGeneration(annotation, generationType, property);
        if (annotation.annotationType() == Generated.class && property.isAnnotationPresent(Version.class) && valueGeneration.getGenerationTiming() == GenerationTiming.INSERT) {
            throw new AnnotationException("@Generated(INSERT) on a @Version property not allowed, use ALWAYS (or NEVER): " + StringHelper.qualify(this.holder.getPath(), this.name));
        }
        return valueGeneration;
    }

    private <A extends Annotation> AnnotationValueGeneration<A> instantiateAndInitializeValueGeneration(A annotation, Class<? extends AnnotationValueGeneration<?>> generationType, XProperty property) {
        try {
            AnnotationValueGeneration<?> valueGeneration = generationType.newInstance();
            valueGeneration.initialize(annotation, this.buildingContext.getBootstrapContext().getReflectionManager().toClass(property.getType()));
            return valueGeneration;
        }
        catch (HibernateException e) {
            throw e;
        }
        catch (Exception e) {
            throw new AnnotationException("Exception occurred during processing of generator annotation:" + StringHelper.qualify(this.holder.getPath(), this.name), e);
        }
    }

    private boolean isToOneValue(Value value) {
        return ToOne.class.isInstance(value);
    }

    public void setProperty(XProperty property) {
        this.property = property;
    }

    public void setReturnedClass(XClass returnedClass) {
        this.returnedClass = returnedClass;
    }

    public SimpleValueBinder getSimpleValueBinder() {
        return this.simpleValueBinder;
    }

    public Value getValue() {
        return this.value;
    }

    public void setId(boolean id) {
        this.isId = id;
    }

    public void setInheritanceStatePerClass(Map<XClass, InheritanceState> inheritanceStatePerClass) {
        this.inheritanceStatePerClass = inheritanceStatePerClass;
    }

    private static class NoValueGeneration
    implements ValueGeneration {
        public static final NoValueGeneration INSTANCE = new NoValueGeneration();

        private NoValueGeneration() {
        }

        @Override
        public GenerationTiming getGenerationTiming() {
            return GenerationTiming.NEVER;
        }

        @Override
        public ValueGenerator<?> getValueGenerator() {
            return null;
        }

        @Override
        public boolean referenceColumnInSql() {
            return true;
        }

        @Override
        public String getDatabaseGeneratedReferencedColumnValue() {
            return null;
        }
    }
}

