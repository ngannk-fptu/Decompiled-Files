/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

import org.hibernate.mapping.Property;

public interface NaturalIdUniqueKeyBinder {
    public void addAttributeBinding(Property var1);

    public void process();
}

