/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.LockOptions;

public interface ExceptionConverter {
    public RuntimeException convertCommitException(RuntimeException var1);

    public RuntimeException convert(HibernateException var1, LockOptions var2);

    public RuntimeException convert(HibernateException var1);

    public RuntimeException convert(RuntimeException var1);

    public RuntimeException convert(RuntimeException var1, LockOptions var2);

    public JDBCException convert(SQLException var1, String var2);
}

