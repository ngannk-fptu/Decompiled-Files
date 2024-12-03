/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.symbolTable;

import javax.xml.namespace.QName;
import org.apache.axis.wsdl.symbolTable.ContainedEntry;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

public class ContainedAttribute
extends ContainedEntry {
    private boolean optional = false;

    protected ContainedAttribute(TypeEntry type, QName qname) {
        super(type, qname);
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean getOptional() {
        return this.optional;
    }
}

