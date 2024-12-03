/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.Type
 */
package net.sf.cglib.core;

import org.objectweb.asm.Type;

public class Local {
    private Type type;
    private int index;

    public Local(int index, Type type) {
        this.type = type;
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public Type getType() {
        return this.type;
    }
}

