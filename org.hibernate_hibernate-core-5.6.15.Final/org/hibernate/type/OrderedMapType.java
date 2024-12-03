/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.util.LinkedHashMap;
import org.hibernate.type.MapType;
import org.hibernate.type.TypeFactory;

public class OrderedMapType
extends MapType {
    @Deprecated
    public OrderedMapType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
        this(role, propertyRef);
    }

    public OrderedMapType(String role, String propertyRef) {
        super(role, propertyRef);
    }

    @Override
    public Object instantiate(int anticipatedSize) {
        return anticipatedSize > 0 ? new LinkedHashMap(anticipatedSize) : new LinkedHashMap();
    }
}

