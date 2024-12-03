/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantCPInfo;

public class LongCPInfo
extends ConstantCPInfo {
    public LongCPInfo() {
        super(5, 2);
    }

    @Override
    public void read(DataInputStream cpStream) throws IOException {
        this.setValue(cpStream.readLong());
    }

    public String toString() {
        return "Long Constant Pool Entry: " + this.getValue();
    }
}

