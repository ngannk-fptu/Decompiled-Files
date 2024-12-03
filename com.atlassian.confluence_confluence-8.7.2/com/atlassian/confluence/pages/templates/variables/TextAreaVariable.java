/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.templates.variables;

import com.atlassian.confluence.pages.templates.variables.Variable;

public class TextAreaVariable
implements Variable {
    public static final String TYPE = "textarea";
    private String name;
    private String value;
    private int columns;
    private int rows;

    public TextAreaVariable(String name, String value, int rows, int columns) {
        this.name = name;
        this.value = value;
        this.columns = columns;
        this.rows = rows;
    }

    public TextAreaVariable(String name, int rows, int columns) {
        this.name = name;
        this.columns = columns;
        this.rows = rows;
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

    public int getColumns() {
        return this.columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return this.rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TextAreaVariable that = (TextAreaVariable)o;
        if (this.columns != that.columns) {
            return false;
        }
        if (this.rows != that.rows) {
            return false;
        }
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        return !(this.value != null ? !this.value.equals(that.value) : that.value != null);
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        result = 31 * result + this.columns;
        result = 31 * result + this.rows;
        return result;
    }
}

