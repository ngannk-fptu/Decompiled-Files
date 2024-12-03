/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Access
 *  javax.persistence.AccessType
 *  javax.persistence.ManyToMany
 *  javax.persistence.ManyToOne
 *  javax.persistence.OneToMany
 *  javax.persistence.OneToOne
 *  javax.persistence.Transient
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XProperty
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.persistence.Access;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import org.hibernate.AnnotationException;
import org.hibernate.MappingException;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.Target;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.SourceType;
import org.hibernate.cfg.AccessType;
import org.hibernate.cfg.annotations.HCANNHelper;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.jboss.logging.Logger;

class PropertyContainer {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)PropertyContainer.class.getName());
    private final XClass xClass;
    private final XClass entityAtStake;
    private final AccessType classLevelAccessType;
    private final List<XProperty> persistentAttributes;

    PropertyContainer(XClass clazz, XClass entityAtStake, AccessType defaultClassLevelAccessType) {
        this.xClass = clazz;
        this.entityAtStake = entityAtStake;
        if (defaultClassLevelAccessType == AccessType.DEFAULT) {
            defaultClassLevelAccessType = AccessType.PROPERTY;
        }
        AccessType localClassLevelAccessType = this.determineLocalClassDefinedAccessStrategy();
        assert (localClassLevelAccessType != null);
        AccessType accessType = this.classLevelAccessType = localClassLevelAccessType != AccessType.DEFAULT ? localClassLevelAccessType : defaultClassLevelAccessType;
        assert (this.classLevelAccessType == AccessType.FIELD || this.classLevelAccessType == AccessType.PROPERTY);
        List fields = this.xClass.getDeclaredProperties(AccessType.FIELD.getType());
        List getters = this.xClass.getDeclaredProperties(AccessType.PROPERTY.getType());
        this.preFilter(fields, getters);
        HashMap<String, XProperty> persistentAttributesFromGetters = new HashMap<String, XProperty>();
        TreeMap<String, XProperty> localAttributeMap = new TreeMap<String, XProperty>();
        PropertyContainer.collectPersistentAttributesUsingLocalAccessType(this.xClass, localAttributeMap, persistentAttributesFromGetters, fields, getters);
        PropertyContainer.collectPersistentAttributesUsingClassLevelAccessType(this.xClass, this.classLevelAccessType, localAttributeMap, persistentAttributesFromGetters, fields, getters);
        this.persistentAttributes = PropertyContainer.verifyAndInitializePersistentAttributes(this.xClass, localAttributeMap);
    }

    private void preFilter(List<XProperty> fields, List<XProperty> getters) {
        XProperty property;
        Iterator<XProperty> propertyIterator = fields.iterator();
        while (propertyIterator.hasNext()) {
            property = propertyIterator.next();
            if (!PropertyContainer.mustBeSkipped(property)) continue;
            propertyIterator.remove();
        }
        propertyIterator = getters.iterator();
        while (propertyIterator.hasNext()) {
            property = propertyIterator.next();
            if (!PropertyContainer.mustBeSkipped(property)) continue;
            propertyIterator.remove();
        }
    }

    private static void collectPersistentAttributesUsingLocalAccessType(XClass xClass, TreeMap<String, XProperty> persistentAttributeMap, Map<String, XProperty> persistentAttributesFromGetters, List<XProperty> fields, List<XProperty> getters) {
        Access localAccessAnnotation;
        XProperty xProperty;
        Iterator<XProperty> propertyIterator = fields.iterator();
        while (propertyIterator.hasNext()) {
            xProperty = propertyIterator.next();
            localAccessAnnotation = (Access)xProperty.getAnnotation(Access.class);
            if (localAccessAnnotation == null || localAccessAnnotation.value() != javax.persistence.AccessType.FIELD) continue;
            propertyIterator.remove();
            persistentAttributeMap.put(xProperty.getName(), xProperty);
        }
        propertyIterator = getters.iterator();
        while (propertyIterator.hasNext()) {
            xProperty = propertyIterator.next();
            localAccessAnnotation = (Access)xProperty.getAnnotation(Access.class);
            if (localAccessAnnotation == null || localAccessAnnotation.value() != javax.persistence.AccessType.PROPERTY) continue;
            propertyIterator.remove();
            String name = xProperty.getName();
            XProperty previous = persistentAttributesFromGetters.get(name);
            if (previous != null) {
                throw new org.hibernate.boot.MappingException(LOG.ambiguousPropertyMethods(xClass.getName(), HCANNHelper.annotatedElementSignature(previous), HCANNHelper.annotatedElementSignature(xProperty)), new Origin(SourceType.ANNOTATION, xClass.getName()));
            }
            persistentAttributeMap.put(name, xProperty);
            persistentAttributesFromGetters.put(name, xProperty);
        }
    }

    private static void collectPersistentAttributesUsingClassLevelAccessType(XClass xClass, AccessType classLevelAccessType, TreeMap<String, XProperty> persistentAttributeMap, Map<String, XProperty> persistentAttributesFromGetters, List<XProperty> fields, List<XProperty> getters) {
        if (classLevelAccessType == AccessType.FIELD) {
            for (XProperty field : fields) {
                if (persistentAttributeMap.containsKey(field.getName())) continue;
                persistentAttributeMap.put(field.getName(), field);
            }
        } else {
            for (XProperty getter : getters) {
                String name = getter.getName();
                XProperty previous = persistentAttributesFromGetters.get(name);
                if (previous != null) {
                    throw new org.hibernate.boot.MappingException(LOG.ambiguousPropertyMethods(xClass.getName(), HCANNHelper.annotatedElementSignature(previous), HCANNHelper.annotatedElementSignature(getter)), new Origin(SourceType.ANNOTATION, xClass.getName()));
                }
                if (persistentAttributeMap.containsKey(name)) continue;
                persistentAttributeMap.put(getter.getName(), getter);
                persistentAttributesFromGetters.put(name, getter);
            }
        }
    }

    public XClass getEntityAtStake() {
        return this.entityAtStake;
    }

    public XClass getDeclaringClass() {
        return this.xClass;
    }

    public AccessType getClassLevelAccessType() {
        return this.classLevelAccessType;
    }

    @Deprecated
    public Collection<XProperty> getProperties() {
        return Collections.unmodifiableCollection(this.persistentAttributes);
    }

    public Iterable<XProperty> propertyIterator() {
        return this.persistentAttributes;
    }

    private static List<XProperty> verifyAndInitializePersistentAttributes(XClass xClass, Map<String, XProperty> localAttributeMap) {
        ArrayList<XProperty> output = new ArrayList<XProperty>(localAttributeMap.size());
        for (XProperty xProperty : localAttributeMap.values()) {
            if (!xProperty.isTypeResolved() && !PropertyContainer.discoverTypeWithoutReflection(xProperty)) {
                String msg = "Property " + StringHelper.qualify(xClass.getName(), xProperty.getName()) + " has an unbound type and no explicit target entity. Resolve this Generic usage issue or set an explicit target attribute (eg @OneToMany(target=) or use an explicit @Type";
                throw new AnnotationException(msg);
            }
            output.add(xProperty);
        }
        return CollectionHelper.toSmallList(output);
    }

    private AccessType determineLocalClassDefinedAccessStrategy() {
        Access access;
        AccessType hibernateDefinedAccessType = AccessType.DEFAULT;
        AccessType jpaDefinedAccessType = AccessType.DEFAULT;
        org.hibernate.annotations.AccessType accessType = (org.hibernate.annotations.AccessType)this.xClass.getAnnotation(org.hibernate.annotations.AccessType.class);
        if (accessType != null) {
            hibernateDefinedAccessType = AccessType.getAccessStrategy(accessType.value());
        }
        if ((access = (Access)this.xClass.getAnnotation(Access.class)) != null) {
            jpaDefinedAccessType = AccessType.getAccessStrategy(access.value());
        }
        if (hibernateDefinedAccessType != AccessType.DEFAULT && jpaDefinedAccessType != AccessType.DEFAULT && hibernateDefinedAccessType != jpaDefinedAccessType) {
            throw new MappingException("@AccessType and @Access specified with contradicting values. Use of @Access only is recommended. ");
        }
        AccessType classDefinedAccessType = hibernateDefinedAccessType != AccessType.DEFAULT ? hibernateDefinedAccessType : jpaDefinedAccessType;
        return classDefinedAccessType;
    }

    private static boolean discoverTypeWithoutReflection(XProperty p) {
        if (p.isAnnotationPresent(OneToOne.class) && !((OneToOne)p.getAnnotation(OneToOne.class)).targetEntity().equals(Void.TYPE)) {
            return true;
        }
        if (p.isAnnotationPresent(OneToMany.class) && !((OneToMany)p.getAnnotation(OneToMany.class)).targetEntity().equals(Void.TYPE)) {
            return true;
        }
        if (p.isAnnotationPresent(ManyToOne.class) && !((ManyToOne)p.getAnnotation(ManyToOne.class)).targetEntity().equals(Void.TYPE)) {
            return true;
        }
        if (p.isAnnotationPresent(ManyToMany.class) && !((ManyToMany)p.getAnnotation(ManyToMany.class)).targetEntity().equals(Void.TYPE)) {
            return true;
        }
        if (p.isAnnotationPresent(Any.class)) {
            return true;
        }
        if (p.isAnnotationPresent(ManyToAny.class)) {
            if (!p.isCollection() && !p.isArray()) {
                throw new AnnotationException("@ManyToAny used on a non collection non array property: " + p.getName());
            }
            return true;
        }
        if (p.isAnnotationPresent(Type.class)) {
            return true;
        }
        return p.isAnnotationPresent(Target.class);
    }

    private static boolean mustBeSkipped(XProperty property) {
        return property.isAnnotationPresent(Transient.class) || "net.sf.cglib.transform.impl.InterceptFieldCallback".equals(property.getType().getName());
    }
}

