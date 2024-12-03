/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import org.hibernate.mapping.Any;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;

public interface ValueVisitor {
    public Object accept(Bag var1);

    public Object accept(IdentifierBag var1);

    public Object accept(List var1);

    public Object accept(PrimitiveArray var1);

    public Object accept(Array var1);

    public Object accept(Map var1);

    public Object accept(OneToMany var1);

    public Object accept(Set var1);

    public Object accept(Any var1);

    public Object accept(SimpleValue var1);

    public Object accept(DependantValue var1);

    public Object accept(Component var1);

    public Object accept(ManyToOne var1);

    public Object accept(OneToOne var1);
}

