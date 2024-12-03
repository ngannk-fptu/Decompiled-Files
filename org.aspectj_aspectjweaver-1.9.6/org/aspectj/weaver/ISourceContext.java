/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.bridge.ISourceLocation;
import org.aspectj.weaver.IHasPosition;

public interface ISourceContext {
    public ISourceLocation makeSourceLocation(IHasPosition var1);

    public ISourceLocation makeSourceLocation(int var1, int var2);

    public int getOffset();

    public void tidy();
}

