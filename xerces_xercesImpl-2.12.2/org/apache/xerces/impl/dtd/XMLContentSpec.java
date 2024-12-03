/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd;

public class XMLContentSpec {
    public static final short CONTENTSPECNODE_LEAF = 0;
    public static final short CONTENTSPECNODE_ZERO_OR_ONE = 1;
    public static final short CONTENTSPECNODE_ZERO_OR_MORE = 2;
    public static final short CONTENTSPECNODE_ONE_OR_MORE = 3;
    public static final short CONTENTSPECNODE_CHOICE = 4;
    public static final short CONTENTSPECNODE_SEQ = 5;
    public static final short CONTENTSPECNODE_ANY = 6;
    public static final short CONTENTSPECNODE_ANY_OTHER = 7;
    public static final short CONTENTSPECNODE_ANY_LOCAL = 8;
    public static final short CONTENTSPECNODE_ANY_LAX = 22;
    public static final short CONTENTSPECNODE_ANY_OTHER_LAX = 23;
    public static final short CONTENTSPECNODE_ANY_LOCAL_LAX = 24;
    public static final short CONTENTSPECNODE_ANY_SKIP = 38;
    public static final short CONTENTSPECNODE_ANY_OTHER_SKIP = 39;
    public static final short CONTENTSPECNODE_ANY_LOCAL_SKIP = 40;
    public short type;
    public Object value;
    public Object otherValue;

    public XMLContentSpec() {
        this.clear();
    }

    public XMLContentSpec(short s, Object object, Object object2) {
        this.setValues(s, object, object2);
    }

    public XMLContentSpec(XMLContentSpec xMLContentSpec) {
        this.setValues(xMLContentSpec);
    }

    public XMLContentSpec(Provider provider, int n) {
        this.setValues(provider, n);
    }

    public void clear() {
        this.type = (short)-1;
        this.value = null;
        this.otherValue = null;
    }

    public void setValues(short s, Object object, Object object2) {
        this.type = s;
        this.value = object;
        this.otherValue = object2;
    }

    public void setValues(XMLContentSpec xMLContentSpec) {
        this.type = xMLContentSpec.type;
        this.value = xMLContentSpec.value;
        this.otherValue = xMLContentSpec.otherValue;
    }

    public void setValues(Provider provider, int n) {
        if (!provider.getContentSpec(n, this)) {
            this.clear();
        }
    }

    public int hashCode() {
        return this.type << 16 | this.value.hashCode() << 8 | this.otherValue.hashCode();
    }

    public boolean equals(Object object) {
        if (object != null && object instanceof XMLContentSpec) {
            XMLContentSpec xMLContentSpec = (XMLContentSpec)object;
            return this.type == xMLContentSpec.type && this.value == xMLContentSpec.value && this.otherValue == xMLContentSpec.otherValue;
        }
        return false;
    }

    public static interface Provider {
        public boolean getContentSpec(int var1, XMLContentSpec var2);
    }
}

