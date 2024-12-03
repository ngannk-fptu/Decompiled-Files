/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.symbolTable;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.Undefined;
import org.apache.axis.wsdl.symbolTable.Utils;
import org.w3c.dom.Node;

public abstract class TypeEntry
extends SymTabEntry
implements Serializable {
    protected Node node;
    protected TypeEntry refType;
    protected String dims = "";
    protected boolean underlTypeNillable = false;
    protected QName componentType = null;
    protected QName itemQName = null;
    protected boolean undefined;
    protected boolean isBaseType;
    protected boolean isSimpleType = false;
    protected boolean onlyLiteralReference = false;
    protected HashSet types = null;
    protected Vector containedElements;
    protected Vector containedAttributes;

    protected TypeEntry(QName pqName, TypeEntry refType, Node pNode, String dims) {
        super(pqName);
        this.node = pNode;
        this.undefined = refType.undefined;
        this.refType = refType;
        if (dims == null) {
            dims = "";
        }
        this.dims = dims;
        if (refType.undefined) {
            TypeEntry uType = refType;
            while (!(uType instanceof Undefined)) {
                uType = uType.refType;
            }
            ((Undefined)((Object)uType)).register(this);
        } else {
            this.isBaseType = refType.isBaseType && refType.dims.equals("") && dims.equals("");
        }
    }

    protected TypeEntry(QName pqName, Node pNode) {
        super(pqName);
        this.node = pNode;
        this.refType = null;
        this.undefined = false;
        this.dims = "";
        this.isBaseType = false;
    }

    protected TypeEntry(QName pqName) {
        super(pqName);
        this.node = null;
        this.undefined = false;
        this.dims = "";
        this.isBaseType = true;
    }

    public Node getNode() {
        return this.node;
    }

    public String getBaseType() {
        if (this.isBaseType) {
            return this.name;
        }
        return null;
    }

    public boolean isBaseType() {
        return this.isBaseType;
    }

    public void setBaseType(boolean baseType) {
        this.isBaseType = baseType;
    }

    public boolean isSimpleType() {
        return this.isSimpleType;
    }

    public void setSimpleType(boolean simpleType) {
        this.isSimpleType = simpleType;
    }

    public boolean isOnlyLiteralReferenced() {
        return this.onlyLiteralReference;
    }

    public void setOnlyLiteralReference(boolean set) {
        this.onlyLiteralReference = set;
    }

    protected TypeEntry getUndefinedTypeRef() {
        if (this instanceof Undefined) {
            return this;
        }
        if (this.undefined && this.refType != null && this.refType.undefined) {
            TypeEntry uType = this.refType;
            while (!(uType instanceof Undefined)) {
                uType = uType.refType;
            }
            return uType;
        }
        return null;
    }

    protected boolean updateUndefined(TypeEntry oldRef, TypeEntry newRef) throws IOException {
        boolean changedState = false;
        if (this.refType == oldRef) {
            this.refType = newRef;
            changedState = true;
            TypeEntry te = this.refType;
            while (te != null && te != this) {
                te = te.refType;
            }
            if (te == this) {
                this.undefined = false;
                this.isBaseType = false;
                this.node = null;
                throw new IOException(Messages.getMessage("undefinedloop00", this.getQName().toString()));
            }
        }
        if (this.refType != null && this.undefined && !this.refType.undefined) {
            this.undefined = false;
            changedState = true;
            this.isBaseType = this.refType.isBaseType && this.refType.dims.equals("") && this.dims.equals("");
        }
        return changedState;
    }

    public TypeEntry getRefType() {
        return this.refType;
    }

    public void setRefType(TypeEntry refType) {
        this.refType = refType;
    }

    public String getDimensions() {
        return this.dims;
    }

    public boolean getUnderlTypeNillable() {
        if (!this.underlTypeNillable && !this.getDimensions().equals("") && this.refType != null) {
            this.underlTypeNillable = this.refType.getUnderlTypeNillable();
        }
        return this.underlTypeNillable;
    }

    public void setUnderlTypeNillable(boolean underlTypeNillable) {
        this.underlTypeNillable = underlTypeNillable;
    }

    public QName getComponentType() {
        return this.componentType;
    }

    public void setComponentType(QName componentType) {
        this.componentType = componentType;
    }

    public QName getItemQName() {
        return this.itemQName;
    }

    public void setItemQName(QName itemQName) {
        this.itemQName = itemQName;
    }

    public String toString() {
        return this.toString("");
    }

    protected String toString(String indent) {
        String refString = indent + "RefType:       null \n";
        if (this.refType != null) {
            refString = indent + "RefType:\n" + this.refType.toString(indent + "  ") + "\n";
        }
        return super.toString(indent) + indent + "Class:         " + this.getClass().getName() + "\n" + indent + "Base?:         " + this.isBaseType + "\n" + indent + "Undefined?:    " + this.undefined + "\n" + indent + "isSimpleType?  " + this.isSimpleType + "\n" + indent + "Node:          " + this.getNode() + "\n" + indent + "Dims:          " + this.dims + "\n" + indent + "isOnlyLiteralReferenced: " + this.onlyLiteralReference + "\n" + refString;
    }

    public HashSet getNestedTypes(SymbolTable symbolTable, boolean derivedFlag) {
        if (this.types == null) {
            this.types = Utils.getNestedTypes(this, symbolTable, derivedFlag);
        }
        return this.types;
    }

    public Vector getContainedAttributes() {
        return this.containedAttributes;
    }

    public void setContainedAttributes(Vector containedAttributes) {
        this.containedAttributes = containedAttributes;
    }

    public Vector getContainedElements() {
        return this.containedElements;
    }

    public void setContainedElements(Vector containedElements) {
        this.containedElements = containedElements;
    }
}

