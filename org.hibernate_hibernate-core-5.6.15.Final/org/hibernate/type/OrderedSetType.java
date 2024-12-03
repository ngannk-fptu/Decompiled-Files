/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.util.LinkedHashSet;
import org.hibernate.type.SetType;
import org.hibernate.type.TypeFactory;

public class OrderedSetType
extends SetType {
    @Deprecated
    public OrderedSetType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
        this(role, propertyRef);
    }

    public OrderedSetType(String role, String propertyRef) {
        super(role, propertyRef);
    }

    @Override
    public Object instantiate(int anticipatedSize) {
        return anticipatedSize > 0 ? new LinkedHashSet(anticipatedSize) : new LinkedHashSet();
    }
}

