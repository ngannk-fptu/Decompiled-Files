/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.status.service;

import com.atlassian.confluence.status.service.PlatformAwareClusteredDatabaseMetadataRetriever;
import com.atlassian.confluence.status.service.systeminfo.ClusteredDatabasePlatformMetadata;
import com.atlassian.confluence.status.service.systeminfo.ClusteredDatabasePlatformType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AwsClusteredDatabaseMetadataRetriever
implements PlatformAwareClusteredDatabaseMetadataRetriever {
    private static final Logger logger = LoggerFactory.getLogger(AwsClusteredDatabaseMetadataRetriever.class);
    static final String AURORA_VERSION_QUERY = "select AURORA_VERSION() as aurora_version";
    static final String AURORA_COUNT_CLUSTER_MEMBERS_QUERY = "SELECT count(server_id) as member_count FROM AURORA_REPLICA_STATUS();";

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Optional<ClusteredDatabasePlatformMetadata> getClusteredDatabaseMetadata(Connection connection) {
        String version;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(AURORA_VERSION_QUERY);){
            if (!resultSet.next()) {
                Optional<ClusteredDatabasePlatformMetadata> optional = Optional.empty();
                return optional;
            }
            version = resultSet.getString("aurora_version");
        }
        catch (SQLException ex) {
            logger.debug("Exception retrieving aurora database metadata", (Throwable)ex);
            return Optional.empty();
        }
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(AURORA_COUNT_CLUSTER_MEMBERS_QUERY);){
            if (resultSet.next()) {
                int memberCount = resultSet.getInt("member_count");
                return Optional.of(new ClusteredDatabasePlatformMetadata(ClusteredDatabasePlatformType.AWS_AURORA, version, memberCount));
            }
            Optional<ClusteredDatabasePlatformMetadata> optional = Optional.empty();
            return optional;
        }
        catch (SQLException ex) {
            logger.debug("Exception retrieving aurora database metadata", (Throwable)ex);
            return Optional.empty();
        }
    }
}

