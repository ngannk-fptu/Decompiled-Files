/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.asm;

import org.springframework.asm.Attribute;
import org.springframework.asm.Label;
import org.springframework.asm.TypePath;

class Context {
    Attribute[] attrs;
    int flags;
    char[] buffer;
    int[] bootstrapMethods;
    int access;
    String name;
    String desc;
    Label[] labels;
    int typeRef;
    TypePath typePath;
    int offset;
    Label[] start;
    Label[] end;
    int[] index;
    int mode;
    int localCount;
    int localDiff;
    Object[] local;
    int stackCount;
    Object[] stack;

    Context() {
    }
}

