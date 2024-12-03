/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.ddl;

import java.util.Objects;

public final class SQLAction {
    private final String statement;
    private final SQLAction undoAction;

    private SQLAction(String statement, SQLAction undoAction) {
        this.statement = Objects.requireNonNull(statement, "statement can't be null");
        this.undoAction = undoAction;
    }

    public static SQLAction of(CharSequence statement) {
        return new SQLAction(statement.toString(), null);
    }

    public SQLAction withUndoAction(SQLAction undoAction) {
        return new SQLAction(this.statement, undoAction);
    }

    public String getStatement() {
        return this.statement;
    }

    public SQLAction getUndoAction() {
        return this.undoAction;
    }

    public String toString() {
        return "SQLAction{statement='" + this.statement + '\'' + ", undoAction=" + this.undoAction + '}';
    }
}

