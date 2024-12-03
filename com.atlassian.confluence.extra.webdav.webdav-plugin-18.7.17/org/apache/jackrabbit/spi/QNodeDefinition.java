/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QItemDefinition;

public interface QNodeDefinition
extends QItemDefinition {
    public static final QNodeDefinition[] EMPTY_ARRAY = new QNodeDefinition[0];

    public Name getDefaultPrimaryType();

    public Name[] getRequiredPrimaryTypes();

    public boolean allowsSameNameSiblings();
}

