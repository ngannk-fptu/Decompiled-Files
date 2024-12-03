/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor;

import java.util.TimeZone;
import org.hibernate.engine.jdbc.LobCreator;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public interface WrapperOptions {
    public boolean useStreamForLobBinding();

    public LobCreator getLobCreator();

    public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor var1);

    public TimeZone getJdbcTimeZone();
}

