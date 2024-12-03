/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.ddl;

import net.java.ao.schema.ddl.DDLAction;
import net.java.ao.schema.ddl.DDLActionType;
import net.java.ao.schema.ddl.DDLForeignKey;
import net.java.ao.schema.ddl.DDLTable;
import net.java.ao.schema.ddl.DDLValue;

public final class DDLActions {
    private DDLActions() {
    }

    public static DDLAction newAlterAddKey(DDLForeignKey key) {
        DDLAction action = DDLActions.newAction(DDLActionType.ALTER_ADD_KEY);
        action.setKey(key);
        return action;
    }

    public static DDLAction newInsert(DDLTable table, DDLValue[] values) {
        DDLAction action = DDLActions.newAction(DDLActionType.INSERT);
        action.setTable(table);
        action.setValues(values);
        return action;
    }

    private static DDLAction newAction(DDLActionType actionType) {
        return new DDLAction(actionType);
    }
}

