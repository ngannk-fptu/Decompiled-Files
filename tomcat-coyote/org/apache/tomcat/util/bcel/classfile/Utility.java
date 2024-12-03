/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import org.apache.tomcat.util.bcel.classfile.ConstantClass;
import org.apache.tomcat.util.bcel.classfile.ConstantPool;
import org.apache.tomcat.util.bcel.classfile.ConstantUtf8;

final class Utility {
    static String compactClassName(String str) {
        return str.replace('/', '.');
    }

    static String getClassName(ConstantPool constantPool, int index) {
        Object c = constantPool.getConstant(index, (byte)7);
        int i = ((ConstantClass)c).getNameIndex();
        c = constantPool.getConstant(i, (byte)1);
        String name = ((ConstantUtf8)c).getBytes();
        return Utility.compactClassName(name);
    }

    static void skipFully(DataInput file, int length) throws IOException {
        int total = file.skipBytes(length);
        if (total != length) {
            throw new EOFException();
        }
    }

    static void swallowAttribute(DataInput file) throws IOException {
        Utility.skipFully(file, 2);
        int length = file.readInt();
        Utility.skipFully(file, length);
    }

    private Utility() {
    }
}

