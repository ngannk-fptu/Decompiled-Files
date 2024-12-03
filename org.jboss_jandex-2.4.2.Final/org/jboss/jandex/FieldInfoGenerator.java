/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.util.AbstractList;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.FieldInternal;

class FieldInfoGenerator
extends AbstractList<FieldInfo> {
    private final FieldInternal[] fields;
    private final ClassInfo clazz;
    private final byte[] positions;

    public FieldInfoGenerator(ClassInfo clazz, FieldInternal[] fields, byte[] positions) {
        this.clazz = clazz;
        this.fields = fields;
        this.positions = positions;
    }

    @Override
    public FieldInfo get(int i) {
        FieldInternal field = this.positions.length > 0 ? this.fields[this.positions[i] & 0xFF] : this.fields[i];
        return new FieldInfo(this.clazz, field);
    }

    @Override
    public int size() {
        return this.fields.length;
    }
}

