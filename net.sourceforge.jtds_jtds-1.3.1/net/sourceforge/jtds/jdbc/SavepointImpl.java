/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.sql.SQLException;
import java.sql.Savepoint;
import net.sourceforge.jtds.jdbc.Messages;

class SavepointImpl
implements Savepoint {
    private final int id;
    private final String name;

    SavepointImpl(int id) {
        this(id, null);
    }

    SavepointImpl(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getSavepointId() throws SQLException {
        if (this.name != null) {
            throw new SQLException(Messages.get("error.savepoint.named"), "HY024");
        }
        return this.id;
    }

    @Override
    public String getSavepointName() throws SQLException {
        if (this.name == null) {
            throw new SQLException(Messages.get("error.savepoint.unnamed"), "HY024");
        }
        return this.name;
    }

    int getId() {
        return this.id;
    }
}

