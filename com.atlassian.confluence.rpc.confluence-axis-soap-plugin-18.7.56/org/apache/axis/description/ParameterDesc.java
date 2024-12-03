/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.description;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.xml.namespace.QName;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

public class ParameterDesc
implements Serializable {
    public static final byte IN = 1;
    public static final byte OUT = 2;
    public static final byte INOUT = 3;
    private transient QName name;
    public TypeEntry typeEntry;
    private byte mode = 1;
    private QName typeQName;
    private Class javaType = null;
    private int order = -1;
    private boolean isReturn = false;
    private String mimeType = null;
    private QName itemQName;
    private QName itemType;
    private boolean inHeader = false;
    private boolean outHeader = false;
    private String documentation = null;
    static /* synthetic */ Class class$javax$xml$rpc$holders$Holder;

    public ParameterDesc() {
    }

    public ParameterDesc(ParameterDesc copy) {
        this.name = copy.name;
        this.typeEntry = copy.typeEntry;
        this.mode = copy.mode;
        this.typeQName = copy.typeQName;
        this.javaType = copy.javaType;
        this.order = copy.order;
        this.isReturn = copy.isReturn;
        this.mimeType = copy.mimeType;
        this.inHeader = copy.inHeader;
        this.outHeader = copy.outHeader;
    }

    public ParameterDesc(QName name, byte mode, QName typeQName) {
        this.name = name;
        this.mode = mode;
        this.typeQName = typeQName;
    }

    public ParameterDesc(QName name, byte mode, QName typeQName, Class javaType, boolean inHeader, boolean outHeader) {
        this(name, mode, typeQName);
        this.javaType = javaType;
        this.inHeader = inHeader;
        this.outHeader = outHeader;
    }

    public ParameterDesc(QName name, byte mode, QName typeQName, Class javaType) {
        this(name, mode, typeQName, javaType, false, false);
    }

    public String toString() {
        return this.toString("");
    }

    public String toString(String indent) {
        String text = "";
        text = text + indent + "name:       " + this.name + "\n";
        text = text + indent + "typeEntry:  " + this.typeEntry + "\n";
        text = text + indent + "mode:       " + (this.mode == 1 ? "IN" : (this.mode == 3 ? "INOUT" : "OUT")) + "\n";
        text = text + indent + "position:   " + this.order + "\n";
        text = text + indent + "isReturn:   " + this.isReturn + "\n";
        text = text + indent + "typeQName:  " + this.typeQName + "\n";
        text = text + indent + "javaType:   " + this.javaType + "\n";
        text = text + indent + "inHeader:   " + this.inHeader + "\n";
        text = text + indent + "outHeader:  " + this.outHeader + "\n";
        return text;
    }

    public static byte modeFromString(String modeStr) {
        int ret = 1;
        if (modeStr == null) {
            return 1;
        }
        if (modeStr.equalsIgnoreCase("out")) {
            ret = 2;
        } else if (modeStr.equalsIgnoreCase("inout")) {
            ret = 3;
        }
        return (byte)ret;
    }

    public static String getModeAsString(byte mode) {
        if (mode == 3) {
            return "inout";
        }
        if (mode == 2) {
            return "out";
        }
        if (mode == 1) {
            return "in";
        }
        throw new IllegalArgumentException(Messages.getMessage("badParameterMode", Byte.toString(mode)));
    }

    public QName getQName() {
        return this.name;
    }

    public String getName() {
        if (this.name == null) {
            return null;
        }
        return this.name.getLocalPart();
    }

    public void setName(String name) {
        this.name = new QName("", name);
    }

    public void setQName(QName name) {
        this.name = name;
    }

    public QName getTypeQName() {
        return this.typeQName;
    }

    public void setTypeQName(QName typeQName) {
        this.typeQName = typeQName;
    }

    public Class getJavaType() {
        return this.javaType;
    }

    public void setJavaType(Class javaType) {
        if (javaType != null && ((this.mode == 1 || this.isReturn) && (class$javax$xml$rpc$holders$Holder == null ? (class$javax$xml$rpc$holders$Holder = ParameterDesc.class$("javax.xml.rpc.holders.Holder")) : class$javax$xml$rpc$holders$Holder).isAssignableFrom(javaType) || this.mode != 1 && !this.isReturn && !(class$javax$xml$rpc$holders$Holder == null ? (class$javax$xml$rpc$holders$Holder = ParameterDesc.class$("javax.xml.rpc.holders.Holder")) : class$javax$xml$rpc$holders$Holder).isAssignableFrom(javaType))) {
            throw new IllegalArgumentException(Messages.getMessage("setJavaTypeErr00", javaType.getName(), ParameterDesc.getModeAsString(this.mode)));
        }
        this.javaType = javaType;
    }

    public byte getMode() {
        return this.mode;
    }

    public void setMode(byte mode) {
        this.mode = mode;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setInHeader(boolean value) {
        this.inHeader = value;
    }

    public boolean isInHeader() {
        return this.inHeader;
    }

    public void setOutHeader(boolean value) {
        this.outHeader = value;
    }

    public boolean isOutHeader() {
        return this.outHeader;
    }

    public boolean getIsReturn() {
        return this.isReturn;
    }

    public void setIsReturn(boolean value) {
        this.isReturn = value;
    }

    public String getDocumentation() {
        return this.documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if (this.name == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeObject(this.name.getNamespaceURI());
            out.writeObject(this.name.getLocalPart());
        }
        if (this.typeQName == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeObject(this.typeQName.getNamespaceURI());
            out.writeObject(this.typeQName.getLocalPart());
        }
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.name = in.readBoolean() ? new QName((String)in.readObject(), (String)in.readObject()) : null;
        this.typeQName = in.readBoolean() ? new QName((String)in.readObject(), (String)in.readObject()) : null;
        in.defaultReadObject();
    }

    public QName getItemQName() {
        return this.itemQName;
    }

    public void setItemQName(QName itemQName) {
        this.itemQName = itemQName;
    }

    public QName getItemType() {
        return this.itemType;
    }

    public void setItemType(QName itemType) {
        this.itemType = itemType;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

