/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.jlink;

import java.io.DataInput;
import java.io.IOException;

class ConstantPool {
    static final byte UTF8 = 1;
    static final byte UNUSED = 2;
    static final byte INTEGER = 3;
    static final byte FLOAT = 4;
    static final byte LONG = 5;
    static final byte DOUBLE = 6;
    static final byte CLASS = 7;
    static final byte STRING = 8;
    static final byte FIELDREF = 9;
    static final byte METHODREF = 10;
    static final byte INTERFACEMETHODREF = 11;
    static final byte NAMEANDTYPE = 12;
    byte[] types;
    Object[] values;

    ConstantPool(DataInput data) throws IOException {
        int count = data.readUnsignedShort();
        this.types = new byte[count];
        this.values = new Object[count];
        block9: for (int i = 1; i < count; ++i) {
            byte type;
            this.types[i] = type = data.readByte();
            switch (type) {
                case 1: {
                    this.values[i] = data.readUTF();
                    continue block9;
                }
                case 2: {
                    continue block9;
                }
                case 3: 
                case 9: 
                case 10: 
                case 11: 
                case 12: {
                    this.values[i] = data.readInt();
                    continue block9;
                }
                case 4: {
                    this.values[i] = Float.valueOf(data.readFloat());
                    continue block9;
                }
                case 5: {
                    this.values[i] = data.readLong();
                    ++i;
                    continue block9;
                }
                case 6: {
                    this.values[i] = data.readDouble();
                    ++i;
                    continue block9;
                }
                case 7: 
                case 8: {
                    this.values[i] = data.readUnsignedShort();
                    continue block9;
                }
            }
        }
    }
}

