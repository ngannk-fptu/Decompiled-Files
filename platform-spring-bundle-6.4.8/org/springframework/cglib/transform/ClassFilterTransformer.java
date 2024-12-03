/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.transform;

import org.springframework.cglib.transform.AbstractClassFilterTransformer;
import org.springframework.cglib.transform.ClassFilter;
import org.springframework.cglib.transform.ClassTransformer;

public class ClassFilterTransformer
extends AbstractClassFilterTransformer {
    private ClassFilter filter;

    public ClassFilterTransformer(ClassFilter filter2, ClassTransformer pass) {
        super(pass);
        this.filter = filter2;
    }

    protected boolean accept(int version, int access, String name, String signature, String superName, String[] interfaces) {
        return this.filter.accept(name.replace('/', '.'));
    }
}

