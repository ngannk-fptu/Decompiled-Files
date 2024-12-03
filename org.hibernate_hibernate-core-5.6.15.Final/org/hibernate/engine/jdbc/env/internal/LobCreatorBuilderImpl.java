/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.jdbc.env.internal;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.ContextualLobCreator;
import org.hibernate.engine.jdbc.LobCreationContext;
import org.hibernate.engine.jdbc.LobCreator;
import org.hibernate.engine.jdbc.NonContextualLobCreator;
import org.hibernate.engine.jdbc.env.spi.LobCreatorBuilder;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.jboss.logging.Logger;

public class LobCreatorBuilderImpl
implements LobCreatorBuilder {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)LobCreatorBuilderImpl.class.getName());
    private final boolean useContextualLobCreation;
    private static final Class[] NO_ARG_SIG = new Class[0];
    private static final Object[] NO_ARGS = new Object[0];

    private LobCreatorBuilderImpl(boolean useContextualLobCreation) {
        this.useContextualLobCreation = useContextualLobCreation;
    }

    public static LobCreatorBuilderImpl makeLobCreatorBuilder(Dialect dialect, Map configValues, Connection jdbcConnection) {
        return new LobCreatorBuilderImpl(LobCreatorBuilderImpl.useContextualLobCreation(dialect, configValues, jdbcConnection));
    }

    public static LobCreatorBuilderImpl makeLobCreatorBuilder() {
        LOG.disablingContextualLOBCreationSinceConnectionNull();
        return new LobCreatorBuilderImpl(false);
    }

    private static boolean useContextualLobCreation(Dialect dialect, Map configValues, Connection jdbcConnection) {
        block12: {
            boolean isNonContextualLobCreationRequired = ConfigurationHelper.getBoolean("hibernate.jdbc.lob.non_contextual_creation", configValues);
            if (isNonContextualLobCreationRequired) {
                LOG.disablingContextualLOBCreation("hibernate.jdbc.lob.non_contextual_creation");
                return false;
            }
            if (jdbcConnection == null) {
                LOG.disablingContextualLOBCreationSinceConnectionNull();
                return false;
            }
            try {
                try {
                    DatabaseMetaData meta = jdbcConnection.getMetaData();
                    if (meta.getJDBCMajorVersion() < 4) {
                        LOG.disablingContextualLOBCreationSinceOldJdbcVersion(meta.getJDBCMajorVersion());
                        return false;
                    }
                    if (!dialect.supportsJdbcConnectionLobCreation(meta)) {
                        return false;
                    }
                }
                catch (SQLException meta) {
                    // empty catch block
                }
                Class<Connection> connectionClass = Connection.class;
                Method createClobMethod = connectionClass.getMethod("createClob", NO_ARG_SIG);
                if (!createClobMethod.getDeclaringClass().equals(Connection.class)) break block12;
                try {
                    Object clob = createClobMethod.invoke((Object)jdbcConnection, NO_ARGS);
                    try {
                        Method freeMethod = clob.getClass().getMethod("free", NO_ARG_SIG);
                        freeMethod.invoke(clob, NO_ARGS);
                    }
                    catch (Throwable ignore) {
                        LOG.tracef("Unable to free CLOB created to test createClob() implementation : %s", ignore);
                    }
                    return true;
                }
                catch (Throwable t) {
                    LOG.disablingContextualLOBCreationSinceCreateClobFailed(t);
                }
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
        }
        return false;
    }

    @Override
    public LobCreator buildLobCreator(LobCreationContext lobCreationContext) {
        return this.useContextualLobCreation ? new ContextualLobCreator(lobCreationContext) : NonContextualLobCreator.INSTANCE;
    }
}

