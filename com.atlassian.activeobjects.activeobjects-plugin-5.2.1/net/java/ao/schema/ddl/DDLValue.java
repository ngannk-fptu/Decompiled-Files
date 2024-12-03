/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.ddl;

import net.java.ao.schema.ddl.DDLField;

public class DDLValue {
    private DDLField field;
    private Object value;

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public DDLField getField() {
        return this.field;
    }

    public void setField(DDLField field) {
        this.field = field;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DDLValue ddlValue = (DDLValue)o;
        if (this.field != null ? !this.field.equals(ddlValue.field) : ddlValue.field != null) {
            return false;
        }
        return !(this.value != null ? !this.value.equals(ddlValue.value) : ddlValue.value != null);
    }

    public int hashCode() {
        int result = this.field != null ? this.field.hashCode() : 0;
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }
}

