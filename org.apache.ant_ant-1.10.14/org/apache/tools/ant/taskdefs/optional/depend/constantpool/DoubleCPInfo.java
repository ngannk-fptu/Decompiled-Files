/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantCPInfo;

public class DoubleCPInfo
extends ConstantCPInfo {
    public DoubleCPInfo() {
        super(6, 2);
    }

    @Override
    public void read(DataInputStream cpStream) throws IOException {
        this.setValue(cpStream.readDouble());
    }

    public String toString() {
        return "Double Constant Pool Entry: " + this.getValue();
    }
}

