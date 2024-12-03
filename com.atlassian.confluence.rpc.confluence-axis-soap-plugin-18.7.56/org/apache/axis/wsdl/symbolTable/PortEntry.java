/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Port
 */
package org.apache.axis.wsdl.symbolTable;

import javax.wsdl.Port;
import javax.xml.namespace.QName;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;

public class PortEntry
extends SymTabEntry {
    private Port port = null;

    public PortEntry(Port port) {
        super(new QName(port.getName()));
        this.port = port;
    }

    public Port getPort() {
        return this.port;
    }
}

