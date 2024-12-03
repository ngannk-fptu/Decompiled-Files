/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

public class Attribute {
    private String nsURI;
    private String name;
    private String value;

    public Attribute(String nsURI, String localName, String value) {
        this.nsURI = nsURI;
        this.name = localName;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String lname) {
        this.name = lname;
    }

    public String getNamespace() {
        return this.nsURI;
    }

    public void setNsURI(String nsURI) {
        this.nsURI = nsURI;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return "[attr:{" + this.nsURI + "}" + this.name + "=" + this.value + "]";
    }
}

