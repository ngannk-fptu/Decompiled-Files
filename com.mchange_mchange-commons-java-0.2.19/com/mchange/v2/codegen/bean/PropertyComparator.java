/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.bean.Property;

class PropertyComparator {
    PropertyComparator() {
    }

    public int compare(Object object, Object object2) {
        Property property = (Property)object;
        Property property2 = (Property)object2;
        return property.getName().compareTo(property2.getName());
    }
}

