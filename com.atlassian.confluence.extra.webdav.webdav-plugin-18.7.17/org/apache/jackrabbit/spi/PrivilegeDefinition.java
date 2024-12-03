/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import java.util.Set;
import org.apache.jackrabbit.spi.Name;

public interface PrivilegeDefinition {
    public Name getName();

    public boolean isAbstract();

    public Set<Name> getDeclaredAggregateNames();
}

