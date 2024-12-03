/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.symbolTable;

import javax.xml.namespace.QName;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

public class ContainedEntry
extends SymTabEntry {
    protected TypeEntry type;

    protected ContainedEntry(TypeEntry type, QName qname) {
        super(qname);
        this.type = type;
    }

    public TypeEntry getType() {
        return this.type;
    }

    public void setType(TypeEntry type) {
        this.type = type;
    }
}

