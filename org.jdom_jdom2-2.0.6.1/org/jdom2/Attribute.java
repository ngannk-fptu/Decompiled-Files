/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import org.jdom2.AttributeType;
import org.jdom2.CloneBase;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.IllegalDataException;
import org.jdom2.IllegalNameException;
import org.jdom2.Namespace;
import org.jdom2.NamespaceAware;
import org.jdom2.Verifier;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Attribute
extends CloneBase
implements NamespaceAware,
Serializable,
Cloneable {
    private static final long serialVersionUID = 200L;
    public static final AttributeType UNDECLARED_TYPE = AttributeType.UNDECLARED;
    public static final AttributeType CDATA_TYPE = AttributeType.CDATA;
    public static final AttributeType ID_TYPE = AttributeType.ID;
    public static final AttributeType IDREF_TYPE = AttributeType.IDREF;
    public static final AttributeType IDREFS_TYPE = AttributeType.IDREFS;
    public static final AttributeType ENTITY_TYPE = AttributeType.ENTITY;
    public static final AttributeType ENTITIES_TYPE = AttributeType.ENTITIES;
    public static final AttributeType NMTOKEN_TYPE = AttributeType.NMTOKEN;
    public static final AttributeType NMTOKENS_TYPE = AttributeType.NMTOKENS;
    public static final AttributeType NOTATION_TYPE = AttributeType.NOTATION;
    public static final AttributeType ENUMERATED_TYPE = AttributeType.ENUMERATION;
    protected String name;
    protected Namespace namespace;
    protected String value;
    protected AttributeType type = AttributeType.UNDECLARED;
    protected boolean specified = true;
    protected transient Element parent;

    protected Attribute() {
    }

    public Attribute(String name, String value, Namespace namespace) {
        this(name, value, AttributeType.UNDECLARED, namespace);
    }

    @Deprecated
    public Attribute(String name, String value, int type, Namespace namespace) {
        this(name, value, AttributeType.byIndex(type), namespace);
    }

    public Attribute(String name, String value, AttributeType type, Namespace namespace) {
        this.setName(name);
        this.setValue(value);
        this.setAttributeType(type);
        this.setNamespace(namespace);
    }

    public Attribute(String name, String value) {
        this(name, value, AttributeType.UNDECLARED, Namespace.NO_NAMESPACE);
    }

    public Attribute(String name, String value, AttributeType type) {
        this(name, value, type, Namespace.NO_NAMESPACE);
    }

    @Deprecated
    public Attribute(String name, String value, int type) {
        this(name, value, type, Namespace.NO_NAMESPACE);
    }

    public Element getParent() {
        return this.parent;
    }

    public Document getDocument() {
        return this.parent == null ? null : this.parent.getDocument();
    }

    public String getName() {
        return this.name;
    }

    public Attribute setName(String name) {
        if (name == null) {
            throw new NullPointerException("Can not set a null name for an Attribute.");
        }
        String reason = Verifier.checkAttributeName(name);
        if (reason != null) {
            throw new IllegalNameException(name, "attribute", reason);
        }
        this.name = name;
        this.specified = true;
        return this;
    }

    public String getQualifiedName() {
        String prefix = this.namespace.getPrefix();
        if ("".equals(prefix)) {
            return this.getName();
        }
        return prefix + ':' + this.getName();
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
        this.specified = true;
        return this;
    }

    public String getValue() {
        return this.value;
    }

    public Attribute setValue(String value) {
        if (value == null) {
            throw new NullPointerException("Can not set a null value for an Attribute");
        }
        String reason = Verifier.checkCharacterData(value);
        if (reason != null) {
            throw new IllegalDataException(value, "attribute", reason);
        }
        this.value = value;
        this.specified = true;
        return this;
    }

    public AttributeType getAttributeType() {
        return this.type;
    }

    public Attribute setAttributeType(AttributeType type) {
        this.type = type == null ? AttributeType.UNDECLARED : type;
        this.specified = true;
        return this;
    }

    @Deprecated
    public Attribute setAttributeType(int type) {
        this.setAttributeType(AttributeType.byIndex(type));
        return this;
    }

    public boolean isSpecified() {
        return this.specified;
    }

    public void setSpecified(boolean specified) {
        this.specified = specified;
    }

    public String toString() {
        return "[Attribute: " + this.getQualifiedName() + "=\"" + this.value + "\"" + "]";
    }

    @Override
    public Attribute clone() {
        Attribute clone = (Attribute)super.clone();
        clone.parent = null;
        return clone;
    }

    public Attribute detach() {
        if (this.parent != null) {
            this.parent.removeAttribute(this);
        }
        return this;
    }

    protected Attribute setParent(Element parent) {
        this.parent = parent;
        return this;
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

    @Override
    public List<Namespace> getNamespacesInScope() {
        if (this.getParent() == null) {
            ArrayList<Namespace> ret = new ArrayList<Namespace>(3);
            ret.add(this.getNamespace());
            ret.add(Namespace.XML_NAMESPACE);
            return Collections.unmodifiableList(ret);
        }
        return Attribute.orderFirst(this.getNamespace(), this.getParent().getNamespacesInScope());
    }

    @Override
    public List<Namespace> getNamespacesIntroduced() {
        if (this.getParent() == null) {
            return Collections.singletonList(this.getNamespace());
        }
        return Collections.emptyList();
    }

    @Override
    public List<Namespace> getNamespacesInherited() {
        if (this.getParent() == null) {
            return Collections.singletonList(Namespace.XML_NAMESPACE);
        }
        return Attribute.orderFirst(this.getNamespace(), this.getParent().getNamespacesInScope());
    }

    private static final List<Namespace> orderFirst(Namespace nsa, List<Namespace> nsl) {
        if (nsl.get(0) == nsa) {
            return nsl;
        }
        TreeMap<String, Namespace> tm = new TreeMap<String, Namespace>();
        for (Namespace ns : nsl) {
            if (ns == nsa) continue;
            tm.put(ns.getPrefix(), ns);
        }
        ArrayList<Namespace> ret = new ArrayList<Namespace>(tm.size() + 1);
        ret.add(nsa);
        ret.addAll(tm.values());
        return Collections.unmodifiableList(ret);
    }
}

