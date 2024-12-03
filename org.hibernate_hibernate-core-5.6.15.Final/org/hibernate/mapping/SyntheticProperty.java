/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import org.hibernate.mapping.Property;

public class SyntheticProperty
extends Property {
    @Override
    public boolean isSynthetic() {
        return true;
    }
}

