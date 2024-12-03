/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.UnionSubclass;

public interface PersistentClassVisitor {
    public Object accept(RootClass var1);

    public Object accept(UnionSubclass var1);

    public Object accept(SingleTableSubclass var1);

    public Object accept(JoinedSubclass var1);

    public Object accept(Subclass var1);
}

