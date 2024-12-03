/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;

public abstract class ClassTransformer
extends ClassVisitor {
    public ClassTransformer() {
        super(393216);
    }

    public ClassTransformer(int opcode) {
        super(opcode);
    }

    public abstract void setTarget(ClassVisitor var1);
}

