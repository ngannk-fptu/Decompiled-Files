/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInputStream;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;

@Deprecated
public interface AttributeReader {
    public Attribute createAttribute(int var1, int var2, DataInputStream var3, ConstantPool var4);
}

