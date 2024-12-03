/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import org.apache.jackrabbit.spi.QItemDefinition;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.QValueConstraint;

public interface QPropertyDefinition
extends QItemDefinition {
    public static final QPropertyDefinition[] EMPTY_ARRAY = new QPropertyDefinition[0];

    public int getRequiredType();

    public QValueConstraint[] getValueConstraints();

    public QValue[] getDefaultValues();

    public boolean isMultiple();

    public String[] getAvailableQueryOperators();

    public boolean isFullTextSearchable();

    public boolean isQueryOrderable();
}

