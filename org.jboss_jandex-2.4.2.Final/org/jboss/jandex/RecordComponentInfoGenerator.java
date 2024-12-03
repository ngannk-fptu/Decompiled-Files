/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.util.AbstractList;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.RecordComponentInfo;
import org.jboss.jandex.RecordComponentInternal;

class RecordComponentInfoGenerator
extends AbstractList<RecordComponentInfo> {
    private final RecordComponentInternal[] recordComponents;
    private final ClassInfo clazz;
    private final byte[] positions;

    public RecordComponentInfoGenerator(ClassInfo clazz, RecordComponentInternal[] recordComponents, byte[] positions) {
        this.clazz = clazz;
        this.recordComponents = recordComponents;
        this.positions = positions;
    }

    @Override
    public RecordComponentInfo get(int i) {
        RecordComponentInternal recordComponent = this.positions.length > 0 ? this.recordComponents[this.positions[i] & 0xFF] : this.recordComponents[i];
        return new RecordComponentInfo(this.clazz, recordComponent);
    }

    @Override
    public int size() {
        return this.recordComponents.length;
    }
}

