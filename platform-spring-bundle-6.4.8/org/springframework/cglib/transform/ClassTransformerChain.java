/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.cglib.transform.AbstractClassTransformer;
import org.springframework.cglib.transform.ClassTransformer;

public class ClassTransformerChain
extends AbstractClassTransformer {
    private ClassTransformer[] chain;

    public ClassTransformerChain(ClassTransformer[] chain) {
        this.chain = (ClassTransformer[])chain.clone();
    }

    public void setTarget(ClassVisitor v) {
        super.setTarget(this.chain[0]);
        ClassVisitor next = v;
        for (int i2 = this.chain.length - 1; i2 >= 0; --i2) {
            this.chain[i2].setTarget(next);
            next = this.chain[i2];
        }
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return this.cv.visitMethod(access, name, desc, signature, exceptions);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ClassTransformerChain{");
        for (int i2 = 0; i2 < this.chain.length; ++i2) {
            if (i2 > 0) {
                sb.append(", ");
            }
            sb.append(this.chain[i2].toString());
        }
        sb.append("}");
        return sb.toString();
    }
}

