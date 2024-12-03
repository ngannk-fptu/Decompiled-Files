/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import java.util.Collection;
import java.util.Map;
import javax.persistence.criteria.Expression;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

public interface Path<X>
extends Expression<X> {
    public Bindable<X> getModel();

    public Path<?> getParentPath();

    public <Y> Path<Y> get(SingularAttribute<? super X, Y> var1);

    public <E, C extends Collection<E>> Expression<C> get(PluralAttribute<X, C, E> var1);

    public <K, V, M extends Map<K, V>> Expression<M> get(MapAttribute<X, K, V> var1);

    public Expression<Class<? extends X>> type();

    public <Y> Path<Y> get(String var1);
}

