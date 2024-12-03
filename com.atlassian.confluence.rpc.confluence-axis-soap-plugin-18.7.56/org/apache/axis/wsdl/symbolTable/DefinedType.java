/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.symbolTable;

import javax.xml.namespace.QName;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.Type;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.w3c.dom.Node;

public class DefinedType
extends Type {
    protected TypeEntry extensionBase;
    protected boolean searchedForExtensionBase = false;

    public DefinedType(QName pqName, Node pNode) {
        super(pqName, pNode);
    }

    public DefinedType(QName pqName, TypeEntry refType, Node pNode, String dims) {
        super(pqName, refType, pNode, dims);
    }

    public TypeEntry getComplexTypeExtensionBase(SymbolTable symbolTable) {
        if (!this.searchedForExtensionBase) {
            if (null == this.extensionBase) {
                this.extensionBase = SchemaUtils.getComplexElementExtensionBase(this.getNode(), symbolTable);
            }
            this.searchedForExtensionBase = true;
        }
        return this.extensionBase;
    }
}

