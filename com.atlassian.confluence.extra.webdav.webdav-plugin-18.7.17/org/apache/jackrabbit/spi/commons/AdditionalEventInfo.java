/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.util.Set;
import javax.jcr.UnsupportedRepositoryOperationException;
import org.apache.jackrabbit.spi.Name;

public interface AdditionalEventInfo {
    public Name getPrimaryNodeTypeName() throws UnsupportedRepositoryOperationException;

    public Set<Name> getMixinTypeNames() throws UnsupportedRepositoryOperationException;

    public Object getSessionAttribute(String var1) throws UnsupportedRepositoryOperationException;
}

