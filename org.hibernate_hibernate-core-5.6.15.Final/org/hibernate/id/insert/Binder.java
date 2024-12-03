/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.insert;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Binder {
    public void bindValues(PreparedStatement var1) throws SQLException;

    public Object getEntity();
}

