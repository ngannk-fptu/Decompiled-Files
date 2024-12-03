/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.jlink;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tools.ant.taskdefs.optional.jlink.ConstantPool;

public class ClassNameReader {
    private static final int CLASS_MAGIC_NUMBER = -889275714;

    public static String getClassName(InputStream input) throws IOException {
        DataInputStream data = new DataInputStream(input);
        int cookie = data.readInt();
        if (cookie != -889275714) {
            return null;
        }
        data.readInt();
        ConstantPool constants = new ConstantPool(data);
        Object[] values = constants.values;
        data.readUnsignedShort();
        int classIndex = data.readUnsignedShort();
        Integer stringIndex = (Integer)values[classIndex];
        return (String)values[stringIndex];
    }
}

