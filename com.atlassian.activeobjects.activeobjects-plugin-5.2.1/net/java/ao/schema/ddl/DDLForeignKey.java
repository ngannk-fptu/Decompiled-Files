/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.ddl;

import net.java.ao.schema.Case;

public class DDLForeignKey {
    private String field = "";
    private String domesticTable = "";
    private String table = "";
    private String foreignField = "";

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getForeignField() {
        return this.foreignField;
    }

    public void setForeignField(String foreignField) {
        this.foreignField = foreignField;
    }

    public String getFKName() {
        return "fk_" + Case.LOWER.apply(this.getDomesticTable()) + '_' + Case.LOWER.apply(this.getField());
    }

    public String getDomesticTable() {
        return this.domesticTable;
    }

    public void setDomesticTable(String domesticTable) {
        this.domesticTable = domesticTable;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof DDLForeignKey) {
            DDLForeignKey key = (DDLForeignKey)obj;
            if (key.field.equals(this.field) && key.foreignField.equals(this.foreignField) && key.table.equals(this.table) && key.domesticTable.equals(this.domesticTable)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return this.field.hashCode() + this.table.hashCode() + this.foreignField.hashCode() + this.domesticTable.hashCode();
    }

    public String toString() {
        return this.getDomesticTable() + "." + this.getField() + " => " + this.getTable() + "." + this.getForeignField();
    }
}

