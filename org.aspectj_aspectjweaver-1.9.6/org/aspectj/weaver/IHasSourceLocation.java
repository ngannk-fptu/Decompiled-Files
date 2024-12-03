/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.bridge.ISourceLocation;
import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.ISourceContext;

public interface IHasSourceLocation
extends IHasPosition {
    public ISourceContext getSourceContext();

    public ISourceLocation getSourceLocation();
}

