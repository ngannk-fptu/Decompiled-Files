/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.symbolTable;

import javax.xml.namespace.QName;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.w3c.dom.Node;

public abstract class Type
extends TypeEntry {
    private boolean generated;

    protected Type(QName pqName) {
        super(pqName);
    }

    protected Type(QName pqName, TypeEntry refType, Node pNode, String dims) {
        super(pqName, refType, pNode, dims);
    }

    protected Type(QName pqName, Node pNode) {
        super(pqName, pNode);
    }

    public void setGenerated(boolean b) {
        this.generated = b;
    }

    public boolean isGenerated() {
        return this.generated;
    }
}

