/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.transform.ClassTransformer;

public abstract class AbstractClassTransformer
extends ClassTransformer {
    protected AbstractClassTransformer() {
        super(393216);
    }

    public void setTarget(ClassVisitor target) {
        this.cv = target;
    }
}

