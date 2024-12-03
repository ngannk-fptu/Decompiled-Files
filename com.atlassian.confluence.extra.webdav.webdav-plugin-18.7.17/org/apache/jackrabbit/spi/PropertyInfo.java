/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import org.apache.jackrabbit.spi.ItemInfo;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.spi.QValue;

public interface PropertyInfo
extends ItemInfo {
    @Override
    public PropertyId getId();

    public int getType();

    public boolean isMultiValued();

    public QValue[] getValues();
}

