/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.SingularAttribute
 */
package org.hibernate.metamodel.model.domain.internal;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import javax.persistence.metamodel.SingularAttribute;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.model.domain.internal.AbstractManagedType;
import org.hibernate.metamodel.model.domain.spi.IdentifiableTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.PersistentAttributeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SingularPersistentAttribute;

public abstract class AbstractIdentifiableType<J>
extends AbstractManagedType<J>
implements IdentifiableTypeDescriptor<J>,
Serializable {
    private final boolean hasIdentifierProperty;
    private final boolean hasIdClass;
    private SingularPersistentAttribute<J, ?> id;
    private Set<SingularPersistentAttribute<? super J, ?>> idClassAttributes;
    private final boolean isVersioned;
    private SingularPersistentAttribute<J, ?> versionAttribute;

    public AbstractIdentifiableType(Class<J> javaType, String typeName, IdentifiableTypeDescriptor<? super J> superType, boolean hasIdClass, boolean hasIdentifierProperty, boolean versioned, SessionFactoryImplementor sessionFactory) {
        super(javaType, typeName, superType, sessionFactory);
        this.hasIdClass = hasIdClass;
        this.hasIdentifierProperty = hasIdentifierProperty;
        this.isVersioned = versioned;
    }

    protected IdentifiableTypeDescriptor.InFlightAccess createInFlightAccess() {
        return new InFlightAccessImpl(super.createInFlightAccess());
    }

    @Override
    public IdentifiableTypeDescriptor.InFlightAccess<J> getInFlightAccess() {
        return (IdentifiableTypeDescriptor.InFlightAccess)super.getInFlightAccess();
    }

    @Override
    public boolean hasIdClass() {
        return this.hasIdClass;
    }

    public boolean hasSingleIdAttribute() {
        return !this.hasIdClass() && this.hasIdentifierProperty;
    }

    @Override
    public IdentifiableTypeDescriptor<? super J> getSuperType() {
        return (IdentifiableTypeDescriptor)super.getSuperType();
    }

    @Override
    public <Y> SingularPersistentAttribute<? super J, Y> getId(Class<Y> javaType) {
        this.ensureNoIdClass();
        SingularPersistentAttribute id = this.locateIdAttribute();
        if (id != null) {
            this.checkType(id, javaType);
        }
        return id;
    }

    private void ensureNoIdClass() {
        if (this.hasIdClass()) {
            throw new IllegalArgumentException("Illegal call to IdentifiableType#getId for class [" + this.getTypeName() + "] defined with @IdClass");
        }
    }

    @Override
    public SingularPersistentAttribute locateIdAttribute() {
        SingularPersistentAttribute id;
        if (this.id != null) {
            return this.id;
        }
        if (this.getSuperType() != null && (id = this.getSuperType().locateIdAttribute()) != null) {
            return id;
        }
        return null;
    }

    private void checkType(SingularPersistentAttribute attribute, Class javaType) {
        if (!javaType.isAssignableFrom(attribute.getType().getJavaType())) {
            throw new IllegalArgumentException(String.format("Attribute [%s#%s : %s] not castable to requested type [%s]", this.getTypeName(), attribute.getName(), attribute.getType().getJavaType().getName(), javaType.getName()));
        }
    }

    @Override
    public <Y> SingularPersistentAttribute<J, Y> getDeclaredId(Class<Y> javaType) {
        this.ensureNoIdClass();
        if (this.id == null) {
            throw new IllegalArgumentException("The id attribute is not declared on this type [" + this.getTypeName() + "]");
        }
        this.checkType(this.id, javaType);
        return this.id;
    }

    @Override
    public SimpleTypeDescriptor<?> getIdType() {
        SingularPersistentAttribute id = this.locateIdAttribute();
        if (id != null) {
            return id.getType();
        }
        Set<SingularPersistentAttribute<J, ?>> idClassAttributes = this.getIdClassAttributesSafely();
        if (idClassAttributes != null && idClassAttributes.size() == 1) {
            return idClassAttributes.iterator().next().getType();
        }
        return null;
    }

    public Set<SingularPersistentAttribute<? super J, ?>> getIdClassAttributesSafely() {
        if (!this.hasIdClass()) {
            return null;
        }
        HashSet attributes = new HashSet();
        this.collectIdClassAttributes(attributes);
        if (attributes.isEmpty()) {
            return null;
        }
        return attributes;
    }

    public Set<SingularAttribute<? super J, ?>> getIdClassAttributes() {
        if (!this.hasIdClass()) {
            throw new IllegalArgumentException("This class [" + this.getJavaType() + "] does not define an IdClass");
        }
        HashSet attributes = new HashSet();
        this.collectIdClassAttributes(attributes);
        if (attributes.isEmpty()) {
            throw new IllegalArgumentException("Unable to locate IdClass attributes [" + this.getJavaType() + "]");
        }
        return attributes;
    }

    @Override
    public void collectIdClassAttributes(Set<SingularPersistentAttribute<? super J, ?>> attributes) {
        if (this.idClassAttributes != null) {
            if (this.idClassAttributes == Collections.EMPTY_SET) {
                this.idClassAttributes = new HashSet();
            }
            attributes.addAll(this.idClassAttributes);
        } else if (this.getSuperType() != null) {
            this.getSuperType().collectIdClassAttributes(attributes);
        }
    }

    @Override
    public void visitIdClassAttributes(Consumer<SingularPersistentAttribute<? super J, ?>> attributeConsumer) {
        if (this.idClassAttributes != null) {
            this.idClassAttributes.forEach(attributeConsumer);
        } else if (this.getSuperType() != null) {
            this.getSuperType().visitIdClassAttributes(attributeConsumer);
        }
    }

    public boolean hasVersionAttribute() {
        return this.isVersioned;
    }

    public boolean hasDeclaredVersionAttribute() {
        return this.isVersioned && this.versionAttribute != null;
    }

    @Override
    public <Y> SingularPersistentAttribute<? super J, Y> getVersion(Class<Y> javaType) {
        if (!this.hasVersionAttribute()) {
            return null;
        }
        SingularPersistentAttribute version = this.locateVersionAttribute();
        if (version != null) {
            this.checkType(version, javaType);
        }
        return version;
    }

    @Override
    public SingularPersistentAttribute locateVersionAttribute() {
        if (this.versionAttribute != null) {
            return this.versionAttribute;
        }
        if (this.getSuperType() != null) {
            return this.getSuperType().locateVersionAttribute();
        }
        return null;
    }

    @Override
    public <Y> SingularPersistentAttribute<J, Y> getDeclaredVersion(Class<Y> javaType) {
        this.checkDeclaredVersion();
        this.checkType(this.versionAttribute, javaType);
        return this.versionAttribute;
    }

    private void checkDeclaredVersion() {
        if (this.versionAttribute == null || this.getSuperType() != null && this.getSuperType().hasVersionAttribute()) {
            throw new IllegalArgumentException("The version attribute is not declared by this type [" + this.getJavaType() + "]");
        }
    }

    public SingularAttribute<J, ?> getDeclaredVersion() {
        this.checkDeclaredVersion();
        return this.versionAttribute;
    }

    private class InFlightAccessImpl
    implements IdentifiableTypeDescriptor.InFlightAccess<J> {
        private final ManagedTypeDescriptor.InFlightAccess managedTypeAccess;

        private InFlightAccessImpl(ManagedTypeDescriptor.InFlightAccess managedTypeAccess) {
            this.managedTypeAccess = managedTypeAccess;
        }

        @Override
        public void applyIdAttribute(SingularPersistentAttribute<J, ?> idAttribute) {
            AbstractIdentifiableType.this.id = idAttribute;
            this.managedTypeAccess.addAttribute(idAttribute);
        }

        @Override
        public void applyIdClassAttributes(Set<SingularPersistentAttribute<? super J, ?>> idClassAttributes) {
            if (idClassAttributes.isEmpty()) {
                AbstractIdentifiableType.this.idClassAttributes = Collections.EMPTY_SET;
            } else {
                for (SingularAttribute singularAttribute : idClassAttributes) {
                    if (AbstractIdentifiableType.this != singularAttribute.getDeclaringType()) continue;
                    SingularPersistentAttribute declaredAttribute = (SingularPersistentAttribute)singularAttribute;
                    this.addAttribute((PersistentAttributeDescriptor)declaredAttribute);
                }
                AbstractIdentifiableType.this.idClassAttributes = idClassAttributes;
            }
        }

        @Override
        public void applyVersionAttribute(SingularPersistentAttribute<J, ?> versionAttribute) {
            AbstractIdentifiableType.this.versionAttribute = versionAttribute;
            this.managedTypeAccess.addAttribute(versionAttribute);
        }

        @Override
        public void addAttribute(PersistentAttributeDescriptor attribute) {
            this.managedTypeAccess.addAttribute(attribute);
        }

        @Override
        public void finishUp() {
            this.managedTypeAccess.finishUp();
        }
    }
}

