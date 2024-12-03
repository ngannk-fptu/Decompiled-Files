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

public class MaxAllowedPacketsCheck
extends AbstractMySQLCheck {
    @VisibleForTesting
    static final String MAX_PACKETS_QUERY = "show session variables like 'max_allowed_packet';";
    private static final int MAX_PACKETS_RECOMMENDED = 0x2200000;
    private static final String SUCCESS_MESSAGE_KEY = "confluence.healthcheck.mysql.max.packets.valid";
    private static final String WARNING_MESSAGE_KEY = "confluence.healthcheck.mysql.max.packets.fail";
    private final SupportHealthStatusBuilder supportHealthStatusBuilder;
    private final ByteSizeFormat byteFormat = new ByteSizeFormat();

    @Autowired
    MaxAllowedPacketsCheck(DatabaseService databaseService, SupportHealthStatusBuilder supportHealthStatusBuilder) {
        super(databaseService);
        this.supportHealthStatusBuilder = supportHealthStatusBuilder;
    }

    @Override
    public SupportHealthStatus check() {
        return this.databaseService.runInConnection(connection -> {
            try (ResultSet resultSet = this.openAndExecuteQuery((Connection)connection, MAX_PACKETS_QUERY);){
                if (resultSet.next()) {
                    int maxPackets = resultSet.getInt("Value");
                    SupportHealthStatus supportHealthStatus = maxPackets >= 0x2200000 ? this.supportHealthStatusBuilder.ok(this, SUCCESS_MESSAGE_KEY, new Serializable[0]) : this.supportHealthStatusBuilder.warning(this, WARNING_MESSAGE_KEY, new Serializable[]{this.byteFormat.format(maxPackets)});
                    return supportHealthStatus;
                }
                SupportHealthStatus supportHealthStatus = this.supportHealthStatusBuilder.critical(this, "confluence.healthcheck.database.query.no.results", new Serializable[0]);
                return supportHealthStatus;
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }
}

