/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.ClassVisitor
 */
package net.sf.cglib.transform;

import net.sf.cglib.core.Constants;
import org.objectweb.asm.ClassVisitor;

public abstract class ClassTransformer
extends ClassVisitor {
    public ClassTransformer() {
        super(Constants.ASM_API);
    }

    public ClassTransformer(int opcode) {
        super(opcode);
    }

    public abstract void setTarget(ClassVisitor var1);
}

