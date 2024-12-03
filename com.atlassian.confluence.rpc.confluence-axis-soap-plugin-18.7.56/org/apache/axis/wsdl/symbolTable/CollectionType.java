/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.symbolTable;

import javax.xml.namespace.QName;
import org.apache.axis.wsdl.symbolTable.CollectionTE;
import org.apache.axis.wsdl.symbolTable.DefinedType;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.w3c.dom.Node;

public class CollectionType
extends DefinedType
implements CollectionTE {
    public CollectionType(QName pqName, TypeEntry refType, Node pNode, String dims) {
        super(pqName, refType, pNode, dims);
    }
}

