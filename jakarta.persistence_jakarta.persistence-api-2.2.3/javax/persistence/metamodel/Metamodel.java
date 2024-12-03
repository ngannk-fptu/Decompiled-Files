/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.metamodel;

import java.util.Set;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;

public interface Metamodel {
    public <X> EntityType<X> entity(Class<X> var1);

    public <X> ManagedType<X> managedType(Class<X> var1);

    public <X> EmbeddableType<X> embeddable(Class<X> var1);

    public Set<ManagedType<?>> getManagedTypes();

    public Set<EntityType<?>> getEntities();

    public Set<EmbeddableType<?>> getEmbeddables();
}

