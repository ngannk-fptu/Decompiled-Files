/*
 * Decompiled with CFR 0.152.
 */
package org.jdom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.IllegalDataException;
import org.jdom.IllegalNameException;
import org.jdom.Namespace;
import org.jdom.Verifier;

public class Attribute
implements Serializable,
Cloneable {
    private static final String CVS_ID = "@(#) $RCSfile: Attribute.java,v $ $Revision: 1.56 $ $Date: 2007/11/10 05:28:58 $ $Name:  $";
    public static final int UNDECLARED_TYPE = 0;
    public static final int CDATA_TYPE = 1;
    public static final int ID_TYPE = 2;
    public static final int IDREF_TYPE = 3;
    public static final int IDREFS_TYPE = 4;
    public static final int ENTITY_TYPE = 5;
    public static final int ENTITIES_TYPE = 6;
    public static final int NMTOKEN_TYPE = 7;
    public static final int NMTOKENS_TYPE = 8;
    public static final int NOTATION_TYPE = 9;
    public static final int ENUMERATED_TYPE = 10;
    protected String name;
    protected transient Namespace namespace;
    protected String value;
    protected int type = 0;
    protected Element parent;

    protected Attribute() {
    }

    public Attribute(String name, String value, Namespace namespace) {
        this(name, value, 0, namespace);
    }

    public Attribute(String name, String value, int type, Namespace namespace) {
        this.setName(name);
        this.setValue(value);
        this.setAttributeType(type);
        this.setNamespace(namespace);
    }

    public Attribute(String name, String value) {
        this(name, value, 0, Namespace.NO_NAMESPACE);
    }

    public Attribute(String name, String value, int type) {
        this(name, value, type, Namespace.NO_NAMESPACE);
    }

    public Element getParent() {
        return this.parent;
    }

    public Document getDocument() {
        Element parentElement = this.getParent();
        if (parentElement != null) {
            return parentElement.getDocument();
        }
        return null;
    }

    protected Attribute setParent(Element parent) {
        this.parent = parent;
        return this;
    }

    public Attribute detach() {
        Element parentElement = this.getParent();
        if (parentElement != null) {
            parentElement.removeAttribute(this.getName(), this.getNamespace());
        }
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Attribute setName(String name) {
        String reason = Verifier.checkAttributeName(name);
        if (reason != null) {
            throw new IllegalNameException(name, "attribute", reason);
        }
        this.name = name;
        return this;
    }

    public String getQualifiedName() {
        String prefix = this.namespace.getPrefix();
        if (prefix == null || "".equals(prefix)) {
            return this.getName();
        }
        return new StringBuffer(prefix).append(':').append(this.getName()).toString();
    }

    public String getNamespacePrefix() {
        return this.namespace.getPrefix();
    }

    public String getNamespaceURI() {
        return this.namespace.getURI();
    }

    public Namespace getNamespace() {
        return this.namespace;
    }

    public Attribute setNamespace(Namespace namespace) {
        if (namespace == null) {
            namespace = Namespace.NO_NAMESPACE;
        }
        if (namespace != Namespace.NO_NAMESPACE && "".equals(namespace.getPrefix())) {
            throw new IllegalNameException("", "attribute namespace", "An attribute namespace without a prefix can only be the NO_NAMESPACE namespace");
        }
        this.namespace = namespace;
        return this;
    }

    public String getValue() {
        return this.value;
    }

    public Attribute setValue(String value) {
        String reason = Verifier.checkCharacterData(value);
        if (reason != null) {
            throw new IllegalDataException(value, "attribute", reason);
        }
        this.value = value;
        return this;
    }

    public int getAttributeType() {
        return this.type;
    }

    public Attribute setAttributeType(int type) {
        if (type < 0 || type > 10) {
            throw new IllegalDataException(String.valueOf(type), "attribute", "Illegal attribute type");
        }
        this.type = type;
        return this;
    }

    public String toString() {
        return new StringBuffer().append("[Attribute: ").append(this.getQualifiedName()).append("=\"").append(this.value).append("\"").append("]").toString();
    }

    public final boolean equals(Object ob) {
        return ob == this;
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public Object clone() {
        Attribute attribute = null;
        try {
            attribute = (Attribute)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            // empty catch block
        }
        attribute.parent = null;
        return attribute;
    }

    public int getIntValue() throws DataConversionException {
        try {
            return Integer.parseInt(this.value.trim());
        }
        catch (NumberFormatException e) {
            throw new DataConversionException(this.name, "int");
        }
    }

    public long getLongValue() throws DataConversionException {
        try {
            return Long.parseLong(this.value.trim());
        }
        catch (NumberFormatException e) {
            throw new DataConversionException(this.name, "long");
        }
    }

    public float getFloatValue() throws DataConversionException {
        try {
            return Float.valueOf(this.value.trim()).floatValue();
        }
        catch (NumberFormatException e) {
            throw new DataConversionException(this.name, "float");
        }
    }

    public double getDoubleValue() throws DataConversionException {
        try {
            return Double.valueOf(this.value.trim());
        }
        catch (NumberFormatException e) {
            String v = this.value.trim();
            if ("INF".equals(v)) {
                return Double.POSITIVE_INFINITY;
            }
            if ("-INF".equals(v)) {
                return Double.NEGATIVE_INFINITY;
            }
            throw new DataConversionException(this.name, "double");
        }
    }

    public boolean getBooleanValue() throws DataConversionException {
        String valueTrim = this.value.trim();
        if (valueTrim.equalsIgnoreCase("true") || valueTrim.equalsIgnoreCase("on") || valueTrim.equalsIgnoreCase("1") || valueTrim.equalsIgnoreCase("yes")) {
            return true;
        }
        if (valueTrim.equalsIgnoreCase("false") || valueTrim.equalsIgnoreCase("off") || valueTrim.equalsIgnoreCase("0") || valueTrim.equalsIgnoreCase("no")) {
            return false;
        }
        throw new DataConversionException(this.name, "boolean");
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.namespace.getPrefix());
        out.writeObject(this.namespace.getURI());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.namespace = Namespace.getNamespace((String)in.readObject(), (String)in.readObject());
    }
}

