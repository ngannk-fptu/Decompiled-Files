/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class InnerEmulationDependency {
    public BlockScope scope;
    public boolean wasEnclosingInstanceSupplied;

    public InnerEmulationDependency(BlockScope scope, boolean wasEnclosingInstanceSupplied) {
        this.scope = scope;
        this.wasEnclosingInstanceSupplied = wasEnclosingInstanceSupplied;
    }
}

