/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.metamodel;

import java.util.Set;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

public interface ManagedType<X>
extends Type<X> {
    public Set<Attribute<? super X, ?>> getAttributes();

    public Set<Attribute<X, ?>> getDeclaredAttributes();

    public <Y> SingularAttribute<? super X, Y> getSingularAttribute(String var1, Class<Y> var2);

    public <Y> SingularAttribute<X, Y> getDeclaredSingularAttribute(String var1, Class<Y> var2);

    public Set<SingularAttribute<? super X, ?>> getSingularAttributes();

    public Set<SingularAttribute<X, ?>> getDeclaredSingularAttributes();

    public <E> CollectionAttribute<? super X, E> getCollection(String var1, Class<E> var2);

    public <E> CollectionAttribute<X, E> getDeclaredCollection(String var1, Class<E> var2);

    public <E> SetAttribute<? super X, E> getSet(String var1, Class<E> var2);

    public <E> SetAttribute<X, E> getDeclaredSet(String var1, Class<E> var2);

    public <E> ListAttribute<? super X, E> getList(String var1, Class<E> var2);

    public <E> ListAttribute<X, E> getDeclaredList(String var1, Class<E> var2);

    public <K, V> MapAttribute<? super X, K, V> getMap(String var1, Class<K> var2, Class<V> var3);

    public <K, V> MapAttribute<X, K, V> getDeclaredMap(String var1, Class<K> var2, Class<V> var3);

    public Set<PluralAttribute<? super X, ?, ?>> getPluralAttributes();

    public Set<PluralAttribute<X, ?, ?>> getDeclaredPluralAttributes();

    public Attribute<? super X, ?> getAttribute(String var1);

    public Attribute<X, ?> getDeclaredAttribute(String var1);

    public SingularAttribute<? super X, ?> getSingularAttribute(String var1);

    public SingularAttribute<X, ?> getDeclaredSingularAttribute(String var1);

    public CollectionAttribute<? super X, ?> getCollection(String var1);

    public CollectionAttribute<X, ?> getDeclaredCollection(String var1);

    public SetAttribute<? super X, ?> getSet(String var1);

    public SetAttribute<X, ?> getDeclaredSet(String var1);

    public ListAttribute<? super X, ?> getList(String var1);

    public ListAttribute<X, ?> getDeclaredList(String var1);

    public MapAttribute<? super X, ?, ?> getMap(String var1);

    public MapAttribute<X, ?, ?> getDeclaredMap(String var1);
}

