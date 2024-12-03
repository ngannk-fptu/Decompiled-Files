/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import java.util.Map;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.PluralJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.MapAttribute;

public interface MapJoin<Z, K, V>
extends PluralJoin<Z, Map<K, V>, V> {
    public MapJoin<Z, K, V> on(Expression<Boolean> var1);

    public MapJoin<Z, K, V> on(Predicate ... var1);

    @Override
    public MapAttribute<? super Z, K, V> getModel();

    public Path<K> key();

    public Path<V> value();

    public Expression<Map.Entry<K, V>> entry();
}

