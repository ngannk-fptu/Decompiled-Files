/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import java.util.Collection;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QNodeDefinition;
import org.apache.jackrabbit.spi.QPropertyDefinition;

public interface QNodeTypeDefinition {
    public Name getName();

    public Name[] getSupertypes();

    public Name[] getSupportedMixinTypes();

    public boolean isMixin();

    public boolean isAbstract();

    public boolean isQueryable();

    public boolean hasOrderableChildNodes();

    public Name getPrimaryItemName();

    public QPropertyDefinition[] getPropertyDefs();

    public QNodeDefinition[] getChildNodeDefs();

    public Collection<Name> getDependencies();
}

