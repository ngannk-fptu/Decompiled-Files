/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.newexport.processor;

import com.atlassian.migration.agent.newexport.Query;
import com.atlassian.migration.agent.newexport.processor.RowProcessor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserKeyColumnExtractor
implements RowProcessor {
    private static final Logger log = LoggerFactory.getLogger(UserKeyColumnExtractor.class);
    private final RowProcessor delegateProcessor;
    private final List<String> userKeyColumnNames;
    private final Set<String> extractedUsers;

    public UserKeyColumnExtractor(RowProcessor processor, Set<String> users, List<String> userKeyColumnNames) {
        this.delegateProcessor = processor;
        this.extractedUsers = users;
        this.userKeyColumnNames = userKeyColumnNames;
    }

    @Override
    public void initialise(ResultSet rs, Query query) {
        this.delegateProcessor.initialise(rs, query);
    }

    @Override
    public void process(ResultSet rs) {
        this.delegateProcessor.process(rs);
        for (String columnName : this.userKeyColumnNames) {
            try {
                String userKey = rs.getString(columnName);
                if (userKey == null) continue;
                this.extractedUsers.add(userKey);
            }
            catch (SQLException e) {
                log.error("Error while retrieving user key. ", (Throwable)e);
            }
        }
    }
}

