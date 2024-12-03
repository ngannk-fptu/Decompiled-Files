/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.symbolTable;

import javax.xml.namespace.QName;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.w3c.dom.Node;

public abstract class Element
extends TypeEntry {
    protected Element(QName pqName, TypeEntry refType, Node pNode, String dims) {
        super(pqName, refType, pNode, dims);
    }

    protected Element(QName pqName, Node pNode) {
        super(pqName, pNode);
    }
}

