/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.core.ClassGenerator;
import org.springframework.cglib.transform.ClassTransformer;

public class TransformingClassGenerator
implements ClassGenerator {
    private ClassGenerator gen;
    private ClassTransformer t;

    public TransformingClassGenerator(ClassGenerator gen, ClassTransformer t) {
        this.gen = gen;
        this.t = t;
    }

    public void generateClass(ClassVisitor v) throws Exception {
        this.t.setTarget(v);
        this.gen.generateClass(this.t);
    }
}

