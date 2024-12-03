/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantCPInfo;

public class IntegerCPInfo
extends ConstantCPInfo {
    public IntegerCPInfo() {
        super(3, 1);
    }

    @Override
    public void read(DataInputStream cpStream) throws IOException {
        this.setValue(cpStream.readInt());
    }

    public String toString() {
        return "Integer Constant Pool Entry: " + this.getValue();
    }
}

