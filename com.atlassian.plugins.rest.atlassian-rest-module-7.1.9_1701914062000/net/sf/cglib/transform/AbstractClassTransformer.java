/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.transform;

import net.sf.cglib.transform.ClassTransformer;
import org.objectweb.asm.ClassVisitor;

public abstract class AbstractClassTransformer
extends ClassTransformer {
    protected AbstractClassTransformer() {
        super(262144);
    }

    public void setTarget(ClassVisitor target) {
        this.cv = target;
    }
}

