/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.db.DatabaseDetails
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.ThreadSafe
 *  org.h2.tools.Server
 */
package com.atlassian.h2;

import com.atlassian.config.ConfigurationException;
import com.atlassian.config.db.DatabaseDetails;
import com.atlassian.h2.H2QueryFailedException;
import com.atlassian.h2.ServerLifecycle;
import com.atlassian.h2.ServerView;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import org.h2.tools.Server;

@ThreadSafe
public class DatabaseCreatingServerLifecycle
extends ServerLifecycle {
    private static final String H2_JDBC_URL = "jdbc:h2:%s";
    private static final String H2_JDBC_URL_READ_ONLY = "jdbc:h2:%s;ACCESS_MODE_DATA=r";
    private final Supplier<File> databaseDirectory;
    private final String databaseName;

    public DatabaseCreatingServerLifecycle(@Nonnull Supplier<Server> serverFactory, @Nonnull Supplier<File> databaseDirectory, @Nonnull String databaseName) {
        super(serverFactory);
        this.databaseDirectory = Objects.requireNonNull(databaseDirectory);
        this.databaseName = Objects.requireNonNull(databaseName);
    }

    @Override
    @Nonnull
    public synchronized ServerView start() {
        ServerView view = this.view();
        if (!view.isRunning()) {
            DatabaseDetails details;
            String dbName = new File(this.databaseDirectory.get(), this.databaseName).getAbsolutePath();
            try {
                details = DatabaseDetails.getDefaults((String)"h2");
            }
            catch (ConfigurationException e) {
                throw new RuntimeException("Cannot load database defaults", e);
            }
            this.validateH2DBIfExists(dbName, details);
            this.createH2IfNeeded(dbName, details);
            view = super.start();
        }
        return view;
    }

    private void validateH2DBIfExists(String dbNameForURL, DatabaseDetails details) {
        String h2DBFileName = this.databaseName + ".mv.db";
        File databaseFile = new File(this.databaseDirectory.get(), h2DBFileName);
        if (databaseFile.exists()) {
            this.runSimpleSelectOnH2(String.format(H2_JDBC_URL_READ_ONLY, dbNameForURL), details);
        }
    }

    private void createH2IfNeeded(String dbNameForURL, DatabaseDetails details) {
        this.runSimpleSelectOnH2(String.format(H2_JDBC_URL, dbNameForURL), details);
    }

    private void runSimpleSelectOnH2(String jdbcURL, DatabaseDetails details) {
        try (Connection connection = DriverManager.getConnection(jdbcURL, details.getUserName(), details.getPassword());
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select 1");){
            resultSet.next();
        }
        catch (SQLException e) {
            throw new H2QueryFailedException(String.format("Cannot connect to %s.", this.databaseName), e);
        }
    }
}

