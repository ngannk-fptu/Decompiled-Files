/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.PortType
 */
package org.apache.axis.wsdl.symbolTable;

import javax.wsdl.PortType;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;

public class PortTypeEntry
extends SymTabEntry {
    private PortType portType;

    public PortTypeEntry(PortType portType) {
        super(portType.getQName());
        this.portType = portType;
    }

    public PortType getPortType() {
        return this.portType;
    }
}

