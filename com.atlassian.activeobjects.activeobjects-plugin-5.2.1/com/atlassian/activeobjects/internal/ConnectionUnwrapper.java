/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConnectionUnwrapper {
    private static final Logger log = LoggerFactory.getLogger(ConnectionUnwrapper.class);

    private ConnectionUnwrapper() {
    }

    @Nonnull
    public static Optional<Connection> tryUnwrapConnection(@Nonnull Connection origin) {
        return ConnectionUnwrapper.tryUnwrapConnection(origin, Connection.class);
    }

    @Nonnull
    public static <T extends Connection> Optional<T> tryUnwrapConnection(@Nonnull Connection origin, Class<T> targetType) {
        try {
            Connection withoutProxies = origin.getMetaData().getConnection();
            Connection targetInstance = (Connection)withoutProxies.unwrap(targetType);
            String actualClassName = targetInstance.getClass().getName();
            if (actualClassName.equals("com.mchange.v2.c3p0.impl.NewProxyConnection")) {
                try {
                    Field inner = targetInstance.getClass().getDeclaredField("inner");
                    inner.setAccessible(true);
                    return Optional.ofNullable(targetType.cast(inner.get(targetInstance)));
                }
                catch (ClassCastException | IllegalAccessException | NoSuchFieldException e) {
                    log.warn("Wasn't able to unwrap NewProxyConnection", (Throwable)e);
                    return Optional.of(targetInstance);
                }
            }
            if (actualClassName.equals("org.apache.commons.dbcp.PoolingDataSource.PoolGuardConnectionWrapper") || actualClassName.equals("org.apache.commons.dbcp2.PoolingDataSource$PoolGuardConnectionWrapper") || actualClassName.equals("org.apache.tomcat.dbcp.dbcp2.PoolingDataSource$PoolGuardConnectionWrapper")) {
                try {
                    Method getInnermostDelegate = targetInstance.getClass().getMethod("getInnermostDelegate", new Class[0]);
                    getInnermostDelegate.setAccessible(true);
                    return Optional.ofNullable(targetType.cast(getInnermostDelegate.invoke((Object)targetInstance, new Object[0])));
                }
                catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    log.warn("Wasn't able to unwrap PoolGuardConnectionWrapper", (Throwable)e);
                    return Optional.of(targetInstance);
                }
            }
            return Optional.of(targetInstance);
        }
        catch (SQLException e) {
            log.warn("Couldn't unwrap the connection {}", (Object)origin, (Object)e);
            return Optional.empty();
        }
    }
}

