/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.ddl;

import java.util.Arrays;
import net.java.ao.schema.ddl.DDLActionBuilder;
import net.java.ao.schema.ddl.DDLActionType;
import net.java.ao.schema.ddl.DDLField;
import net.java.ao.schema.ddl.DDLForeignKey;
import net.java.ao.schema.ddl.DDLIndex;
import net.java.ao.schema.ddl.DDLTable;
import net.java.ao.schema.ddl.DDLValue;

public class DDLAction {
    private DDLActionType actionType;
    private DDLTable table;
    private DDLField oldField;
    private DDLField field;
    private DDLForeignKey key;
    private DDLIndex index;
    private DDLValue[] values;

    public static DDLActionBuilder builder(DDLActionType actionType) {
        return new DDLActionBuilder(actionType);
    }

    public DDLAction(DDLActionType actionType) {
        this.actionType = actionType;
    }

    DDLAction(DDLActionType actionType, DDLTable table, DDLField oldField, DDLField field, DDLForeignKey key, DDLIndex index, DDLValue[] values) {
        this.actionType = actionType;
        this.table = table;
        this.oldField = oldField;
        this.field = field;
        this.key = key;
        this.index = index;
        this.values = values;
    }

    public DDLTable getTable() {
        return this.table;
    }

    public void setTable(DDLTable table) {
        this.table = table;
    }

    public DDLField getField() {
        return this.field;
    }

    public void setField(DDLField field) {
        this.field = field;
    }

    public DDLForeignKey getKey() {
        return this.key;
    }

    public void setKey(DDLForeignKey key) {
        this.key = key;
    }

    public DDLActionType getActionType() {
        return this.actionType;
    }

    public DDLField getOldField() {
        return this.oldField;
    }

    public void setOldField(DDLField oldField) {
        this.oldField = oldField;
    }

    public DDLIndex getIndex() {
        return this.index;
    }

    public void setIndex(DDLIndex index) {
        this.index = index;
    }

    public DDLValue[] getValues() {
        return this.values;
    }

    public void setValues(DDLValue[] values) {
        this.values = values;
    }

    public int hashCode() {
        int back = 0;
        if (this.actionType != null) {
            back += this.actionType.hashCode();
        }
        if (this.table != null) {
            back += this.table.hashCode();
        }
        if (this.oldField != null) {
            back += this.oldField.hashCode();
        }
        if (this.field != null) {
            back += this.field.hashCode();
        }
        if (this.key != null) {
            back += this.key.hashCode();
        }
        if (this.index != null) {
            back += this.index.hashCode();
        }
        if (this.values != null) {
            back += Arrays.hashCode(this.values);
        }
        return back %= 65536;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DDLAction) {
            DDLAction action = (DDLAction)obj;
            if (action == this) {
                return true;
            }
            return !(action.getTable() != null && !action.getTable().equals(this.table) || action.getActionType() != this.actionType || action.getOldField() != null && !action.getOldField().equals(this.oldField) || action.getField() != null && !action.getField().equals(this.field) || action.getKey() != null && !action.getKey().equals(this.key) || action.getIndex() != null && !action.getIndex().equals(this.index) || action.getValues() != null && !Arrays.equals(action.getValues(), this.values));
        }
        return super.equals(obj);
    }
}

