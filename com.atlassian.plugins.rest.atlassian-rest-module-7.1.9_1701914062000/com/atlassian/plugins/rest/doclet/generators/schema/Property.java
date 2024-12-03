/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.doclet.generators.schema;

import com.atlassian.plugins.rest.doclet.generators.schema.ModelClass;
import java.util.Objects;

public final class Property {
    public final ModelClass model;
    public final String name;
    public final boolean required;

    public Property(ModelClass model, String name, boolean required) {
        this.model = model;
        this.name = name;
        this.required = required || model.getActualClass().isPrimitive();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Property that = (Property)o;
        return Objects.equals(this.model, that.model) && Objects.equals(this.name, that.name) && Objects.equals(this.required, that.required);
    }

    public int hashCode() {
        return Objects.hash(this.model, this.name, this.required);
    }

    public String toString() {
        return "Property[" + this.name + "]";
    }
}

