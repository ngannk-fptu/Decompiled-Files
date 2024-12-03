/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.metamodel.model.domain.spi;

import java.util.Set;
import java.util.function.Consumer;
import org.hibernate.metamodel.model.domain.IdentifiableDomainType;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SingularPersistentAttribute;

public interface IdentifiableTypeDescriptor<J>
extends IdentifiableDomainType<J>,
ManagedTypeDescriptor<J> {
    public boolean hasIdClass();

    public SingularPersistentAttribute<? super J, ?> locateIdAttribute();

    public void collectIdClassAttributes(Set<SingularPersistentAttribute<? super J, ?>> var1);

    public void visitIdClassAttributes(Consumer<SingularPersistentAttribute<? super J, ?>> var1);

    @Override
    public InFlightAccess<J> getInFlightAccess();

    public SimpleTypeDescriptor<?> getIdType();

    public <Y> SingularPersistentAttribute<J, Y> getDeclaredId(Class<Y> var1);

    public <Y> SingularPersistentAttribute<? super J, Y> getId(Class<Y> var1);

    public SingularPersistentAttribute<? super J, ?> locateVersionAttribute();

    public <Y> SingularPersistentAttribute<? super J, Y> getVersion(Class<Y> var1);

    public <Y> SingularPersistentAttribute<J, Y> getDeclaredVersion(Class<Y> var1);

    @Override
    public IdentifiableTypeDescriptor<? super J> getSuperType();

    default public IdentifiableTypeDescriptor<? super J> getSupertype() {
        return this.getSuperType();
    }

    public static interface InFlightAccess<X>
    extends ManagedTypeDescriptor.InFlightAccess<X> {
        public void applyIdAttribute(SingularPersistentAttribute<X, ?> var1);

        public void applyIdClassAttributes(Set<SingularPersistentAttribute<? super X, ?>> var1);

        public void applyVersionAttribute(SingularPersistentAttribute<X, ?> var1);
    }
}

