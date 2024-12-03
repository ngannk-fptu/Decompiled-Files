/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.model;

import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import java.util.Objects;

@Deprecated
public class PrimitiveProperty
extends ImportedProperty {
    private final String value;
    private final String type;

    public PrimitiveProperty(String name, String type, String value) {
        super(name);
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return super.toString() + (String)(this.type == null ? "" : "(" + this.type + ") ") + this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PrimitiveProperty that = (PrimitiveProperty)o;
        return Objects.equals(this.type, that.type) && Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.value);
    }
}

