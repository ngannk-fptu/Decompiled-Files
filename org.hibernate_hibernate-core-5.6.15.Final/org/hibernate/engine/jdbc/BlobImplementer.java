/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import java.sql.SQLException;
import org.hibernate.engine.jdbc.BinaryStream;

public interface BlobImplementer {
    public BinaryStream getUnderlyingStream() throws SQLException;
}

