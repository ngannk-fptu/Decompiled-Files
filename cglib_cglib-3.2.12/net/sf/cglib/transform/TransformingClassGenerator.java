/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.ClassVisitor
 */
package net.sf.cglib.transform;

import net.sf.cglib.core.ClassGenerator;
import net.sf.cglib.transform.ClassTransformer;
import org.objectweb.asm.ClassVisitor;

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

