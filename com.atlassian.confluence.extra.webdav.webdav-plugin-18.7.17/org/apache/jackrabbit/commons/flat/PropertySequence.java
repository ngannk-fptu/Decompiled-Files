/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.flat;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import org.apache.jackrabbit.commons.flat.Sequence;

public interface PropertySequence
extends Sequence<Property> {
    public Property addProperty(String var1, Value var2) throws RepositoryException;

    public void removeProperty(String var1) throws RepositoryException;
}

