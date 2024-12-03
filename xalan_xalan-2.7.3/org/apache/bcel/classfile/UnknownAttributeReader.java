/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;

public interface UnknownAttributeReader {
    public Attribute createAttribute(int var1, int var2, DataInput var3, ConstantPool var4);
}

