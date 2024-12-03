/*
 * Decompiled with CFR 0.152.
 */
package jersey.repackaged.org.objectweb.asm;

import jersey.repackaged.org.objectweb.asm.Attribute;
import jersey.repackaged.org.objectweb.asm.Label;
import jersey.repackaged.org.objectweb.asm.TypePath;

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

