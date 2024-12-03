/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.templates.variables;

import com.atlassian.confluence.pages.templates.variables.Variable;
import java.util.List;

public class ListVariable
implements Variable {
    public static final String TYPE = "list";
    private String name;
    private String value;
    private List<String> options;

    public ListVariable(String name, String value, List<String> options) {
        this.name = name;
        this.value = value;
        this.options = options;
    }

    public ListVariable(String name, List<String> options) {
        this.name = name;
        this.options = options;
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

    public List<String> getOptions() {
        return this.options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ListVariable that = (ListVariable)o;
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        if (this.options != null ? !this.options.equals(that.options) : that.options != null) {
            return false;
        }
        return !(this.value != null ? !this.value.equals(that.value) : that.value != null);
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        result = 31 * result + (this.options != null ? this.options.hashCode() : 0);
        return result;
    }
}

