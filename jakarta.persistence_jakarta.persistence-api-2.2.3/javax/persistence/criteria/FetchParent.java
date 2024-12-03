/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import java.util.Set;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

public interface FetchParent<Z, X> {
    public Set<Fetch<X, ?>> getFetches();

    public <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> var1);

    public <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> var1, JoinType var2);

    public <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> var1);

    public <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> var1, JoinType var2);

    public <X, Y> Fetch<X, Y> fetch(String var1);

    public <X, Y> Fetch<X, Y> fetch(String var1, JoinType var2);
}

