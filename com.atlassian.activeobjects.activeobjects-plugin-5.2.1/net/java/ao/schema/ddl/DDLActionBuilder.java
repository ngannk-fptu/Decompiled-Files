/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.ddl;

import net.java.ao.schema.ddl.DDLAction;
import net.java.ao.schema.ddl.DDLActionType;
import net.java.ao.schema.ddl.DDLField;
import net.java.ao.schema.ddl.DDLForeignKey;
import net.java.ao.schema.ddl.DDLIndex;
import net.java.ao.schema.ddl.DDLTable;
import net.java.ao.schema.ddl.DDLValue;

public class DDLActionBuilder {
    private DDLActionType actionType;
    private DDLTable table;
    private DDLField oldField;
    private DDLField field;
    private DDLForeignKey key;
    private DDLIndex index;
    private DDLValue[] values;

    public DDLActionBuilder(DDLActionType actionType) {
        this.actionType = actionType;
    }

    public DDLActionBuilder setTable(DDLTable table) {
        this.table = table;
        return this;
    }

    public DDLActionBuilder setOldField(DDLField oldField) {
        this.oldField = oldField;
        return this;
    }

    public DDLActionBuilder setField(DDLField field) {
        this.field = field;
        return this;
    }

    public DDLActionBuilder setKey(DDLForeignKey key) {
        this.key = key;
        return this;
    }

    public DDLActionBuilder setIndex(DDLIndex index) {
        this.index = index;
        return this;
    }

    public DDLActionBuilder setValues(DDLValue[] values) {
        this.values = values;
        return this;
    }

    public DDLAction build() {
        return new DDLAction(this.actionType, this.table, this.oldField, this.field, this.key, this.index, this.values);
    }
}

