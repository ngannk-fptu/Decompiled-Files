/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantCPInfo;

public class FloatCPInfo
extends ConstantCPInfo {
    public FloatCPInfo() {
        super(4, 1);
    }

    @Override
    public void read(DataInputStream cpStream) throws IOException {
        this.setValue(Float.valueOf(cpStream.readFloat()));
    }

    public String toString() {
        return "Float Constant Pool Entry: " + this.getValue();
    }
}

