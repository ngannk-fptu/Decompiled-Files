/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.ClassVisitor
 *  org.objectweb.asm.MethodVisitor
 */
package net.sf.cglib.transform;

import net.sf.cglib.transform.AbstractClassTransformer;
import net.sf.cglib.transform.ClassTransformer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class ClassTransformerChain
extends AbstractClassTransformer {
    private ClassTransformer[] chain;

    public ClassTransformerChain(ClassTransformer[] chain) {
        this.chain = (ClassTransformer[])chain.clone();
    }

    public void setTarget(ClassVisitor v) {
        super.setTarget(this.chain[0]);
        ClassVisitor next = v;
        for (int i = this.chain.length - 1; i >= 0; --i) {
            this.chain[i].setTarget(next);
            next = this.chain[i];
        }
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return this.cv.visitMethod(access, name, desc, signature, exceptions);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ClassTransformerChain{");
        for (int i = 0; i < this.chain.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(((Object)((Object)this.chain[i])).toString());
        }
        sb.append("}");
        return sb.toString();
    }
}

