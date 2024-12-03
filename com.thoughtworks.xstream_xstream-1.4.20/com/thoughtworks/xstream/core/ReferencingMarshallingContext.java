/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.path.Path;

public interface ReferencingMarshallingContext
extends MarshallingContext {
    public Path currentPath();

    public Object lookupReference(Object var1);

    public void replace(Object var1, Object var2);

    public void registerImplicit(Object var1);
}

