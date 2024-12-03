/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.asm;

import org.aspectj.asm.IProgramElement;

public abstract class HierarchyWalker {
    protected void preProcess(IProgramElement node) {
    }

    protected void postProcess(IProgramElement node) {
    }

    public IProgramElement process(IProgramElement node) {
        this.preProcess(node);
        node.walk(this);
        this.postProcess(node);
        return node;
    }
}

