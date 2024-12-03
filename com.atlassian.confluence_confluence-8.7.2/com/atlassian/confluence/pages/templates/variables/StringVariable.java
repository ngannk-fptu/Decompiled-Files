/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.templates.variables;

import com.atlassian.confluence.pages.templates.variables.Variable;

public class StringVariable
implements Variable {
    public static final String TYPE = "string";
    private String name;
    private String value;

    public StringVariable() {
    }

    public StringVariable(String name) {
        this.name = name;
    }

    public StringVariable(String name, String value) {
        this.value = value;
        this.name = name;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StringVariable)) {
            return false;
        }
        StringVariable stringVariable = (StringVariable)o;
        if (!this.name.equals(stringVariable.name)) {
            return false;
        }
        return !(this.value != null ? !this.value.equals(stringVariable.value) : stringVariable.value != null);
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = 29 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }
}

