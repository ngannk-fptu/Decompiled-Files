/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.rdbms.TransactionalExecutor
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  com.atlassian.synchrony.EventId
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.db;

import com.atlassian.confluence.plugins.collaborative.content.feedback.db.Utils;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.entity.CsvFriendly;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.entity.Event;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.entity.Snapshot;
import com.atlassian.confluence.plugins.collaborative.content.feedback.exception.DataFetchException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.rdbms.TransactionalExecutor;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.atlassian.synchrony.EventId;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class SynchronyDao {
    private static final int FETCH_SIZE = 1000;
    private static final String EVENTS_TABLE_NAME = "EVENTS";
    private static final String SNAPSHOTS_TABLE_NAME = "SNAPSHOTS";
    private final TransactionalExecutorFactory transactionalExecutorFactory;
    private final Utils dbUtils;

    public SynchronyDao(@ComponentImport(value="salTransactionalExecutorFactory") TransactionalExecutorFactory transactionalExecutorFactory, Utils dbUtils) {
        this.transactionalExecutorFactory = transactionalExecutorFactory;
        this.dbUtils = dbUtils;
    }

    public void exportEvents(long contentId, File destination) {
        this.exportDataToCsv(EVENTS_TABLE_NAME, contentId, destination, resultSet -> SynchronyDao.executeWrapped(contentId, () -> this.toEvent((ResultSet)resultSet)));
    }

    public void exportSnapshots(long contentId, File destination) {
        this.exportDataToCsv(SNAPSHOTS_TABLE_NAME, contentId, destination, resultSet -> SynchronyDao.executeWrapped(contentId, () -> this.toSnapshot((ResultSet)resultSet)));
    }

    private void exportDataToCsv(String source, long contentId, File destination, Function<ResultSet, CsvFriendly> transformation) {
        this.exportData(source, contentId, resultSet -> {
            try (OutputStreamWriter outputWriter = new OutputStreamWriter(new FileOutputStream(destination));){
                while (resultSet.next()) {
                    outputWriter.write(((CsvFriendly)transformation.apply((ResultSet)resultSet)).toCsvString());
                }
            }
            catch (SQLException e) {
                throw DataFetchException.queryError(contentId, e);
            }
            catch (FileNotFoundException e) {
                throw DataFetchException.queryError("Result file is not found", contentId, e);
            }
            catch (IOException e) {
                throw DataFetchException.queryError("Error dumping results into file", contentId, e);
            }
            return null;
        });
    }

    private void exportData(String source, long contentId, Function<ResultSet, Void> streamResult) {
        TransactionalExecutor transactionalExecutor = this.transactionalExecutorFactory.createReadOnly();
        String schemaPrefix = (String)transactionalExecutor.getSchemaName().map(s -> s + ".").getOrElse((Object)"");
        String sqlQuery = "select *  from " + schemaPrefix + this.dbUtils.escapeIdentifier(source) + " where " + this.dbUtils.escapeIdentifier("contentid") + "=?";
        transactionalExecutor.execute(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);){
                preparedStatement.setLong(1, contentId);
                preparedStatement.setFetchSize(1000);
                preparedStatement.setFetchDirection(1000);
                streamResult.apply(preparedStatement.executeQuery());
            }
            catch (SQLException e) {
                throw DataFetchException.queryError(contentId, e);
            }
            return null;
        });
    }

    private Event toEvent(ResultSet resultSet) throws SQLException {
        EventId eventId = new EventId();
        eventId.setRev(resultSet.getString("rev"));
        eventId.setHistory(resultSet.getString("history"));
        Event event = new Event();
        event.setEventId(eventId);
        event.setPartition(resultSet.getInt("partition"));
        event.setSequence(resultSet.getInt("sequence"));
        event.setEvent(resultSet.getBytes("event"));
        event.setContentId(resultSet.getLong("contentid"));
        event.setInserted(resultSet.getTimestamp("inserted"));
        return event;
    }

    private Snapshot toSnapshot(ResultSet resultSet) throws SQLException {
        Snapshot snapshot = new Snapshot();
        snapshot.setKey(resultSet.getString("key"));
        snapshot.setValue(resultSet.getBytes("value"));
        snapshot.setContentId(resultSet.getLong("contentid"));
        snapshot.setInserted(resultSet.getTimestamp("inserted"));
        return snapshot;
    }

    private static <V> V executeWrapped(long contentId, Callable<V> action) {
        try {
            return action.call();
        }
        catch (Exception e) {
            throw DataFetchException.queryError(contentId, e);
        }
    }
}

