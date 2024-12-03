/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.metamodel;

import java.util.Set;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

public interface IdentifiableType<X>
extends ManagedType<X> {
    public <Y> SingularAttribute<? super X, Y> getId(Class<Y> var1);

    public <Y> SingularAttribute<X, Y> getDeclaredId(Class<Y> var1);

    public <Y> SingularAttribute<? super X, Y> getVersion(Class<Y> var1);

    public <Y> SingularAttribute<X, Y> getDeclaredVersion(Class<Y> var1);

    public IdentifiableType<? super X> getSupertype();

    public boolean hasSingleIdAttribute();

    public boolean hasVersionAttribute();

    public Set<SingularAttribute<? super X, ?>> getIdClassAttributes();

    public Type<?> getIdType();
}

