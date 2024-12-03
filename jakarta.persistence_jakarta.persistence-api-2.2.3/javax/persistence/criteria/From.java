/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import java.util.Set;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

public interface From<Z, X>
extends Path<X>,
FetchParent<Z, X> {
    public Set<Join<X, ?>> getJoins();

    public boolean isCorrelated();

    public From<Z, X> getCorrelationParent();

    public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> var1);

    public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> var1, JoinType var2);

    public <Y> CollectionJoin<X, Y> join(CollectionAttribute<? super X, Y> var1);

    public <Y> SetJoin<X, Y> join(SetAttribute<? super X, Y> var1);

    public <Y> ListJoin<X, Y> join(ListAttribute<? super X, Y> var1);

    public <K, V> MapJoin<X, K, V> join(MapAttribute<? super X, K, V> var1);

    public <Y> CollectionJoin<X, Y> join(CollectionAttribute<? super X, Y> var1, JoinType var2);

    public <Y> SetJoin<X, Y> join(SetAttribute<? super X, Y> var1, JoinType var2);

    public <Y> ListJoin<X, Y> join(ListAttribute<? super X, Y> var1, JoinType var2);

    public <K, V> MapJoin<X, K, V> join(MapAttribute<? super X, K, V> var1, JoinType var2);

    public <X, Y> Join<X, Y> join(String var1);

    public <X, Y> CollectionJoin<X, Y> joinCollection(String var1);

    public <X, Y> SetJoin<X, Y> joinSet(String var1);

    public <X, Y> ListJoin<X, Y> joinList(String var1);

    public <X, K, V> MapJoin<X, K, V> joinMap(String var1);

    public <X, Y> Join<X, Y> join(String var1, JoinType var2);

    public <X, Y> CollectionJoin<X, Y> joinCollection(String var1, JoinType var2);

    public <X, Y> SetJoin<X, Y> joinSet(String var1, JoinType var2);

    public <X, Y> ListJoin<X, Y> joinList(String var1, JoinType var2);

    public <X, K, V> MapJoin<X, K, V> joinMap(String var1, JoinType var2);
}

