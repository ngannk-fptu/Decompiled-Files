/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.status.service.systeminfo;

import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.status.service.systeminfo.ContentUsageInfo;
import com.atlassian.confluence.status.service.systeminfo.SpaceUsageInfo;
import com.atlassian.confluence.status.service.systeminfo.UsageInfoDTO;
import com.atlassian.confluence.util.SQLUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsageInfo {
    private static final Logger log = LoggerFactory.getLogger(UsageInfo.class);
    private static final String[] USER_TABLES_THROUGH_HISTORY = new String[]{"cwd_user", "users", "os_user"};
    private static final String[] GROUP_TABLES_THROUGH_HISTORY = new String[]{"cwd_group", "groups", "os_group"};
    private final int totalSpaces;
    private final int globalSpaces;
    private final int personalSpaces;
    private final int allContent;
    private final int currentContent;
    private final int localUsers;
    private final int localGroups;
    private final int localMemberships;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static UsageInfo getUsageInfo(Connection databaseConnection) {
        Optional<Boolean> currentAutoCommit = UsageInfo.getAutoCommitQuetly(databaseConnection);
        try {
            currentAutoCommit.ifPresent(mode -> UsageInfo.setAutoCommitQuetly(databaseConnection, true));
            String spaceUsageQuery = "SELECT COUNT(*) totalSpace, sum(case when SPACETYPE = '" + SpaceType.GLOBAL + "' then 1 else 0 end) globalSpaces, sum(case when SPACETYPE = '" + SpaceType.PERSONAL + "' then 1 else 0 end) personalSpaces FROM SPACES";
            SpaceUsageInfo spaceUsageInfo = UsageInfo.getCountForQuery(databaseConnection, spaceUsageQuery, rs -> new SpaceUsageInfo(rs.getInt("totalSpace"), rs.getInt("globalSpaces"), rs.getInt("personalSpaces"))).orElseGet(() -> new SpaceUsageInfo(0, 0, 0));
            String contentUsageQuery = "SELECT COUNT(*) allContent, sum(case when PREVVER IS NULL then 1 else 0 end) currentContents FROM CONTENT";
            ContentUsageInfo contentUsageInfo = UsageInfo.getCountForQuery(databaseConnection, contentUsageQuery, rs -> new ContentUsageInfo(rs.getInt("allContent"), rs.getInt("currentContents"))).orElseGet(() -> new ContentUsageInfo(0, 0));
            UsageInfo usageInfo = UsageInfo.builder().totalSpaces(spaceUsageInfo.getTotalSpaces()).globalSpaces(spaceUsageInfo.getGlobalSpaces()).personalSpaces(spaceUsageInfo.getPersonalSpaces()).allContent(contentUsageInfo.getAllContent()).currentContent(contentUsageInfo.getCurrentContent()).localUsers(UsageInfo.getLocalUsersCount(databaseConnection)).localGroups(UsageInfo.getLocalGroupsCount(databaseConnection)).localMemberships(UsageInfo.getLocalMembershipsCount(databaseConnection)).build();
            return usageInfo;
        }
        finally {
            currentAutoCommit.ifPresent(mode -> UsageInfo.setAutoCommitQuetly(databaseConnection, mode));
        }
    }

    public static Optional<Boolean> getAutoCommitQuetly(Connection databaseConnection) {
        try {
            return Optional.of(databaseConnection.getAutoCommit());
        }
        catch (SQLException e) {
            log.debug("Couldn't get autocommit mode of the connection in order to change it.");
            return Optional.empty();
        }
    }

    private static void setAutoCommitQuetly(Connection databaseConnection, boolean autoCommit) {
        try {
            databaseConnection.setAutoCommit(autoCommit);
        }
        catch (SQLException e) {
            log.debug("Wasn't able to set autocommit to {} for the connection. Ignoring it.", (Object)autoCommit, (Object)e);
        }
    }

    private static int getLocalGroupsCount(Connection databaseConnection) {
        for (String groupTable : GROUP_TABLES_THROUGH_HISTORY) {
            int localGroups = UsageInfo.getCountForQuery(databaseConnection, "SELECT COUNT(*) FROM " + groupTable);
            if (localGroups <= 0) continue;
            return localGroups;
        }
        return 0;
    }

    private static int getLocalUsersCount(Connection databaseConnection) {
        for (String userTable : USER_TABLES_THROUGH_HISTORY) {
            int localUsers = UsageInfo.getCountForQuery(databaseConnection, "SELECT COUNT(*) FROM " + userTable);
            if (localUsers <= 0) continue;
            return localUsers;
        }
        return 0;
    }

    private static int getLocalMembershipsCount(Connection databaseConnection) {
        return UsageInfo.getCountForQuery(databaseConnection, "SELECT COUNT(*) FROM cwd_membership");
    }

    public static UsageInfo errorInstance() {
        return UsageInfo.builder().build();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static <T> Optional<T> getCountForQuery(Connection connection, String query, ResultSetTransformer<T> resultSetTransformer) {
        Optional<T> optional;
        ResultSet rs;
        Statement st;
        block5: {
            st = null;
            rs = null;
            st = connection.createStatement();
            rs = st.executeQuery(query);
            if (rs.next()) break block5;
            log.warn("Unable to execute usage info query: no data returned.");
            Optional optional2 = Optional.empty();
            SQLUtils.closeResultSetQuietly(rs);
            SQLUtils.closeStatementQuietly(st);
            return optional2;
        }
        try {
            optional = Optional.of(resultSetTransformer.apply(rs));
        }
        catch (SQLException e) {
            Optional optional3;
            try {
                log.warn("Unable to execute usage info query: " + query + " - " + e.getMessage());
                log.debug("Full stack trace of query error", (Throwable)e);
                optional3 = Optional.empty();
            }
            catch (Throwable throwable) {
                SQLUtils.closeResultSetQuietly(rs);
                SQLUtils.closeStatementQuietly(st);
                throw throwable;
            }
            SQLUtils.closeResultSetQuietly(rs);
            SQLUtils.closeStatementQuietly(st);
            return optional3;
        }
        SQLUtils.closeResultSetQuietly(rs);
        SQLUtils.closeStatementQuietly(st);
        return optional;
    }

    private static int getCountForQuery(Connection connection, String query) {
        return UsageInfo.getCountForQuery(connection, query, resultSet -> resultSet.getInt(1)).orElse(-1);
    }

    @Deprecated
    public UsageInfo(int totalSpaces, int globalSpaces, int personalSpaces, int allContent, int currentContent, int localUsers, int localGroups) {
        this.totalSpaces = totalSpaces;
        this.globalSpaces = globalSpaces;
        this.personalSpaces = personalSpaces;
        this.allContent = allContent;
        this.currentContent = currentContent;
        this.localUsers = localUsers;
        this.localGroups = localGroups;
        this.localMemberships = -1;
    }

    private UsageInfo(Builder builder) {
        this.totalSpaces = builder.totalSpaces;
        this.globalSpaces = builder.globalSpaces;
        this.personalSpaces = builder.personalSpaces;
        this.allContent = builder.allContent;
        this.currentContent = builder.currentContent;
        this.localUsers = builder.localUsers;
        this.localGroups = builder.localGroups;
        this.localMemberships = builder.localMemberships;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UsageInfoDTO getUsageInfoDTO() {
        return new UsageInfoDTO(this.getTotalSpaces(), this.getGlobalSpaces(), this.getPersonalSpaces(), this.getAllContent(), this.getCurrentContent(), this.getLocalUsers(), this.getLocalGroups());
    }

    public static Logger getLog() {
        return log;
    }

    public int getTotalSpaces() {
        return this.totalSpaces;
    }

    public int getGlobalSpaces() {
        return this.globalSpaces;
    }

    public int getPersonalSpaces() {
        return this.personalSpaces;
    }

    public int getAllContent() {
        return this.allContent;
    }

    public int getCurrentContent() {
        return this.currentContent;
    }

    public int getLocalUsers() {
        return this.localUsers;
    }

    public int getLocalGroups() {
        return this.localGroups;
    }

    public int getLocalMemberships() {
        return this.localMemberships;
    }

    public static class Builder {
        private int totalSpaces = -1;
        private int globalSpaces = -1;
        private int personalSpaces = -1;
        private int allContent = -1;
        private int currentContent = -1;
        private int localUsers = -1;
        private int localGroups = -1;
        private int localMemberships = -1;

        private Builder() {
        }

        public Builder totalSpaces(int totalSpaces) {
            this.totalSpaces = totalSpaces;
            return this;
        }

        public Builder globalSpaces(int globalSpaces) {
            this.globalSpaces = globalSpaces;
            return this;
        }

        public Builder personalSpaces(int personalSpaces) {
            this.personalSpaces = personalSpaces;
            return this;
        }

        public Builder allContent(int allContent) {
            this.allContent = allContent;
            return this;
        }

        public Builder currentContent(int currentContent) {
            this.currentContent = currentContent;
            return this;
        }

        public Builder localUsers(int localUsers) {
            this.localUsers = localUsers;
            return this;
        }

        public Builder localGroups(int localGroups) {
            this.localGroups = localGroups;
            return this;
        }

        public Builder localMemberships(int localMemberships) {
            this.localMemberships = localMemberships;
            return this;
        }

        public UsageInfo build() {
            return new UsageInfo(this);
        }
    }

    private static interface ResultSetTransformer<T> {
        public T apply(ResultSet var1) throws SQLException;
    }
}

