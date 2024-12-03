/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry;

public class Utf8CPInfo
extends ConstantPoolEntry {
    private String value;

    public Utf8CPInfo() {
        super(1, 1);
    }

    @Override
    public void read(DataInputStream cpStream) throws IOException {
        this.value = cpStream.readUTF();
    }

    public String toString() {
        return "UTF8 Value = " + this.value;
    }

    public String getValue() {
        return this.value;
    }
}

