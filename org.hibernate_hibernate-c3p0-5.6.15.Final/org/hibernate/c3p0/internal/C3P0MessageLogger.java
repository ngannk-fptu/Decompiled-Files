/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.internal.log.ConnectionPoolingLogger
 *  org.jboss.logging.Logger$Level
 *  org.jboss.logging.annotations.Cause
 *  org.jboss.logging.annotations.LogMessage
 *  org.jboss.logging.annotations.Message
 *  org.jboss.logging.annotations.MessageLogger
 */
package org.hibernate.c3p0.internal;

import java.sql.SQLException;
import org.hibernate.internal.log.ConnectionPoolingLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode="HHH")
public interface C3P0MessageLogger
extends ConnectionPoolingLogger {
    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Both hibernate-style property '%1$s' and c3p0-style property '%2$s' have been set in Hibernate properties.  Hibernate-style property '%1$s' will be used and c3p0-style property '%2$s' will be ignored!", id=10001)
    public void bothHibernateAndC3p0StylesSet(String var1, String var2);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="C3P0 using driver: %s at URL: %s", id=10002)
    public void c3p0UsingDriver(String var1, String var2);

    @Message(value="JDBC Driver class not found: %s", id=10003)
    public String jdbcDriverNotFound(String var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Could not destroy C3P0 connection pool", id=10004)
    public void unableToDestroyC3p0ConnectionPool(@Cause SQLException var1);

    @Message(value="Could not instantiate C3P0 connection pool", id=10005)
    public String unableToInstantiateC3p0ConnectionPool();
}

