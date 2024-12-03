/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.transform;

import org.objectweb.asm.ClassVisitor;

public abstract class ClassTransformer
extends ClassVisitor {
    public ClassTransformer() {
        super(262144);
    }

    public ClassTransformer(int opcode) {
        super(opcode);
    }

    public abstract void setTarget(ClassVisitor var1);
}

