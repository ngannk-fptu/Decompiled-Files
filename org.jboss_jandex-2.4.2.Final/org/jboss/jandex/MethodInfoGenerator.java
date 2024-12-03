/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.util.AbstractList;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodInternal;

class MethodInfoGenerator
extends AbstractList<MethodInfo> {
    private final MethodInternal[] methods;
    private final ClassInfo clazz;
    private final byte[] positions;

    public MethodInfoGenerator(ClassInfo clazz, MethodInternal[] methods, byte[] positions) {
        this.clazz = clazz;
        this.methods = methods;
        this.positions = positions;
    }

    @Override
    public MethodInfo get(int i) {
        MethodInternal method = this.positions.length > 0 ? this.methods[this.positions[i] & 0xFF] : this.methods[i];
        return new MethodInfo(this.clazz, method);
    }

    @Override
    public int size() {
        return this.methods.length;
    }
}

