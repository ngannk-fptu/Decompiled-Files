/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck.database.mysql;

import com.atlassian.troubleshooting.api.healthcheck.DatabaseService;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.confluence.format.ByteSizeFormat;
import com.atlassian.troubleshooting.confluence.healthcheck.database.mysql.AbstractMySQLCheck;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;

public class InnoDBLogFileSizeCheck
extends AbstractMySQLCheck {
    @VisibleForTesting
    static final String LOG_FILE_SIZE_QUERY = "show session variables like 'innodb_log_file_size';";
    private static final int INNODB_LOG_FILE_SIZE_RECOMMENDED = 0x10000000;
    private static final String SUCCESS_MESSAGE_KEY = "confluence.healthcheck.mysql.innodb.log.file.size.valid";
    private static final String WARNING_MESSAGE_KEY = "confluence.healthcheck.mysql.innodb.log.file.size.fail";
    private final SupportHealthStatusBuilder healthStatusBuilder;
    private final ByteSizeFormat byteFormat = new ByteSizeFormat();

    @Autowired
    public InnoDBLogFileSizeCheck(DatabaseService databaseService, SupportHealthStatusBuilder supportHealthStatusBuilder) {
        super(databaseService);
        this.healthStatusBuilder = supportHealthStatusBuilder;
    }

    @Override
    public SupportHealthStatus check() {
        return this.databaseService.runInConnection(connection -> {
            try (ResultSet resultSet = this.openAndExecuteQuery((Connection)connection, LOG_FILE_SIZE_QUERY);){
                if (resultSet.next()) {
                    long fileSize = resultSet.getLong("Value");
                    SupportHealthStatus supportHealthStatus = fileSize >= 0x10000000L ? this.healthStatusBuilder.ok(this, SUCCESS_MESSAGE_KEY, new Serializable[0]) : this.healthStatusBuilder.warning(this, WARNING_MESSAGE_KEY, new Serializable[]{this.byteFormat.format(fileSize)});
                    return supportHealthStatus;
                }
                SupportHealthStatus supportHealthStatus = this.healthStatusBuilder.critical(this, "confluence.healthcheck.database.query.no.results", new Serializable[0]);
                return supportHealthStatus;
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }
}

