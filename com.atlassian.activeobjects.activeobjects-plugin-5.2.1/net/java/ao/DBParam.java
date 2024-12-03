/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

public class DBParam {
    private String field;
    private Object value;

    public DBParam(String field, Object value) {
        if (field == null) {
            throw new NullPointerException("Field cannot be null");
        }
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        if (field == null) {
            throw new NullPointerException("Field cannot be null");
        }
        this.field = field;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof DBParam) {
            boolean valueEquals;
            DBParam param = (DBParam)obj;
            boolean bl = param.value == null ? param.value == this.value : (valueEquals = param.value.equals(this.value));
            if (param.field.equals(this.field) && valueEquals) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return this.field.hashCode() + (this.value == null ? 0 : this.value.hashCode());
    }
}

