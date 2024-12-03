/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler;

import org.eclipse.jdt.internal.compiler.CompilationResult;

public interface IDebugRequestor {
    public void acceptDebugResult(CompilationResult var1);

    public boolean isActive();

    public void activate();

    public void deactivate();

    public void reset();
}

