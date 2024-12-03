/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import org.apache.jackrabbit.spi.Name;

public interface QItemDefinition {
    public static final QItemDefinition[] EMPTY_ARRAY = new QItemDefinition[0];

    public Name getName();

    public Name getDeclaringNodeType();

    public boolean isAutoCreated();

    public int getOnParentVersion();

    public boolean isProtected();

    public boolean isMandatory();

    public boolean definesResidual();

    public boolean definesNode();
}

