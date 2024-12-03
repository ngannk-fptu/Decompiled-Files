/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.internal.DriverNameExtractor;
import java.sql.Connection;
import java.sql.SQLException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.sql.DataSource;
import net.java.ao.ActiveObjectsException;

@ParametersAreNonnullByDefault
public final class DriverNameExtractorImpl
implements DriverNameExtractor {
    @Override
    @Nonnull
    public String getDriverName(DataSource dataSource) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            String string = connection.getMetaData().getDriverName();
            return string;
        }
        catch (SQLException e) {
            throw new ActiveObjectsException(e);
        }
        finally {
            DriverNameExtractorImpl.closeQuietly(connection);
        }
    }

    private static void closeQuietly(@Nullable Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

