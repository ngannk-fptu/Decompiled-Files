/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.toJava;

public class NamespaceSelector {
    private String namespace_ = "";

    public NamespaceSelector() {
    }

    public NamespaceSelector(String namespace) {
        this.namespace_ = namespace;
    }

    public void setNamespace(String value) {
        this.namespace_ = value;
    }

    public String getNamespace() {
        return this.namespace_;
    }

    public String toString() {
        if (this.namespace_ != null) {
            return "namespace=" + this.namespace_;
        }
        return "";
    }

    public boolean equals(Object value) {
        boolean isEqual = false;
        if (value == null) {
            isEqual = false;
        } else if (value instanceof String) {
            isEqual = ((String)value).equals(this.namespace_);
        } else if (value instanceof NamespaceSelector) {
            isEqual = ((NamespaceSelector)value).namespace_.equals(this.namespace_);
        }
        return isEqual;
    }
}

