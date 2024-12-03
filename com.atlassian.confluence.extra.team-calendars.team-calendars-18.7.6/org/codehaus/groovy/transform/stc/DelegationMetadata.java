/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.stc;

import org.codehaus.groovy.ast.ClassNode;

class DelegationMetadata {
    private final DelegationMetadata parent;
    private final ClassNode type;
    private final int strategy;

    public DelegationMetadata(ClassNode type, int strategy, DelegationMetadata parent) {
        this.strategy = strategy;
        this.type = type;
        this.parent = parent;
    }

    public DelegationMetadata(ClassNode type, int strategy) {
        this(type, strategy, null);
    }

    public int getStrategy() {
        return this.strategy;
    }

    public ClassNode getType() {
        return this.type;
    }

    public DelegationMetadata getParent() {
        return this.parent;
    }
}

