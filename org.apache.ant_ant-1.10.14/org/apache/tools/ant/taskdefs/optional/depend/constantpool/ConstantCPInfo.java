/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry;

public abstract class ConstantCPInfo
extends ConstantPoolEntry {
    private Object value;

    protected ConstantCPInfo(int tagValue, int entries) {
        super(tagValue, entries);
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object newValue) {
        this.value = newValue;
    }
}

