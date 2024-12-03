/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.symbolTable;

import java.io.IOException;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

public interface Undefined {
    public void register(TypeEntry var1);

    public void update(TypeEntry var1) throws IOException;
}

