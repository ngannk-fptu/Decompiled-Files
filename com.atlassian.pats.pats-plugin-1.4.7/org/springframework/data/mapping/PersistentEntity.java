/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.mapping;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import org.springframework.data.mapping.Alias;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.AssociationHandler;
import org.springframework.data.mapping.IdentifierAccessor;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.PersistentPropertyPathAccessor;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.mapping.SimpleAssociationHandler;
import org.springframework.data.mapping.SimplePropertyHandler;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public interface PersistentEntity<T, P extends PersistentProperty<P>>
extends Iterable<P> {
    public String getName();

    @Nullable
    public PreferredConstructor<T, P> getPersistenceConstructor();

    public boolean isConstructorArgument(PersistentProperty<?> var1);

    public boolean isIdProperty(PersistentProperty<?> var1);

    public boolean isVersionProperty(PersistentProperty<?> var1);

    @Nullable
    public P getIdProperty();

    default public P getRequiredIdProperty() {
        P property = this.getIdProperty();
        if (property != null) {
            return property;
        }
        throw new IllegalStateException(String.format("Required identifier property not found for %s!", this.getType()));
    }

    @Nullable
    public P getVersionProperty();

    default public P getRequiredVersionProperty() {
        P property = this.getVersionProperty();
        if (property != null) {
            return property;
        }
        throw new IllegalStateException(String.format("Required version property not found for %s!", this.getType()));
    }

    @Nullable
    public P getPersistentProperty(String var1);

    default public P getRequiredPersistentProperty(String name) {
        P property = this.getPersistentProperty(name);
        if (property != null) {
            return property;
        }
        throw new IllegalStateException(String.format("Required property %s not found for %s!", name, this.getType()));
    }

    @Nullable
    default public P getPersistentProperty(Class<? extends Annotation> annotationType) {
        Iterator<P> it = this.getPersistentProperties(annotationType).iterator();
        return (P)(it.hasNext() ? (PersistentProperty)it.next() : null);
    }

    public Iterable<P> getPersistentProperties(Class<? extends Annotation> var1);

    public boolean hasIdProperty();

    public boolean hasVersionProperty();

    public Class<T> getType();

    public Alias getTypeAlias();

    public TypeInformation<T> getTypeInformation();

    public void doWithProperties(PropertyHandler<P> var1);

    public void doWithProperties(SimplePropertyHandler var1);

    public void doWithAssociations(AssociationHandler<P> var1);

    public void doWithAssociations(SimpleAssociationHandler var1);

    default public void doWithAll(PropertyHandler<P> handler) {
        Assert.notNull(handler, (String)"PropertyHandler must not be null!");
        this.doWithProperties(handler);
        this.doWithAssociations((Association<P> association) -> handler.doWithPersistentProperty(association.getInverse()));
    }

    @Nullable
    public <A extends Annotation> A findAnnotation(Class<A> var1);

    default public <A extends Annotation> A getRequiredAnnotation(Class<A> annotationType) throws IllegalStateException {
        A annotation = this.findAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }
        throw new IllegalStateException(String.format("Required annotation %s not found for %s!", annotationType, this.getType()));
    }

    public <A extends Annotation> boolean isAnnotationPresent(Class<A> var1);

    public <B> PersistentPropertyAccessor<B> getPropertyAccessor(B var1);

    public <B> PersistentPropertyPathAccessor<B> getPropertyPathAccessor(B var1);

    public IdentifierAccessor getIdentifierAccessor(Object var1);

    public boolean isNew(Object var1);

    public boolean isImmutable();

    public boolean requiresPropertyPopulation();
}

