/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.symbolTable;

import javax.xml.namespace.QName;
import org.apache.axis.wsdl.symbolTable.MimeInfo;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

public class Parameter {
    public static final byte IN = 1;
    public static final byte OUT = 2;
    public static final byte INOUT = 3;
    private QName qname;
    private String name;
    private MimeInfo mimeInfo = null;
    private TypeEntry type;
    private byte mode = 1;
    private boolean inHeader = false;
    private boolean outHeader = false;
    private boolean omittable = false;

    public String toString() {
        return "(" + this.type + (this.mimeInfo == null ? "" : "(" + this.mimeInfo + ")") + ", " + this.getName() + ", " + (this.mode == 1 ? "IN)" : (this.mode == 3 ? "INOUT)" : "OUT)" + (this.inHeader ? "(IN soap:header)" : "") + (this.outHeader ? "(OUT soap:header)" : "")));
    }

    public QName getQName() {
        return this.qname;
    }

    public String getName() {
        if (this.name == null && this.qname != null) {
            return this.qname.getLocalPart();
        }
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        if (this.qname == null) {
            this.qname = new QName("", name);
        }
    }

    public void setQName(QName qname) {
        this.qname = qname;
    }

    public MimeInfo getMIMEInfo() {
        return this.mimeInfo;
    }

    public void setMIMEInfo(MimeInfo mimeInfo) {
        this.mimeInfo = mimeInfo;
    }

    public TypeEntry getType() {
        return this.type;
    }

    public void setType(TypeEntry type) {
        this.type = type;
    }

    public byte getMode() {
        return this.mode;
    }

    public void setMode(byte mode) {
        if (mode <= 3 && mode >= 1) {
            this.mode = mode;
        }
    }

    public boolean isInHeader() {
        return this.inHeader;
    }

    public void setInHeader(boolean inHeader) {
        this.inHeader = inHeader;
    }

    public boolean isOutHeader() {
        return this.outHeader;
    }

    public void setOutHeader(boolean outHeader) {
        this.outHeader = outHeader;
    }

    public boolean isOmittable() {
        return this.omittable;
    }

    public void setOmittable(boolean omittable) {
        this.omittable = omittable;
    }
}

