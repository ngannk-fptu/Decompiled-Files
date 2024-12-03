/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.toJava;

public class FactoryProperty {
    private String name_;
    private String value_;

    public String getName() {
        return this.name_;
    }

    public String getValue() {
        return this.value_;
    }

    public void setName(String string) {
        this.name_ = string;
    }

    public void setValue(String string) {
        this.value_ = string;
    }

    public String toString() {
        return this.name_ + "=" + this.value_;
    }

    public boolean equals(Object rhs) {
        if (rhs == null) {
            return false;
        }
        if (rhs instanceof String) {
            return ((String)rhs).equals(this.name_);
        }
        if (rhs instanceof FactoryProperty) {
            return ((FactoryProperty)rhs).equals(this.name_);
        }
        return false;
    }
}

