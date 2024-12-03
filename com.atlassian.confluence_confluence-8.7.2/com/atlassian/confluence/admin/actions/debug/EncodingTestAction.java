/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.sal.spring.connection.SpringHostConnectionAccessor$ConnectionProvider
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  org.hibernate.HibernateException
 */
package com.atlassian.confluence.admin.actions.debug;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.util.FileNameEncodingTester;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.sal.spring.connection.SpringHostConnectionAccessor;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import org.hibernate.HibernateException;

@WebSudoRequired
@AdminOnly
public class EncodingTestAction
extends ConfluenceActionSupport {
    public static final String TEST_STRING = "I\u00f1t\u00ebrn\u00e2ti\u00f4n\u00e0liz\u00e6ti\u00f8n";
    public static final String TEST_STRING_CHANGED = "I\u00f1t\u00ebrn\u00e2ti\u00f4n\u00e0liz\u00e6ti\u00f9n";
    private String submittedTestString;
    private String databasedTestString;
    private String databasedLowercaseTestString;
    private FileNameEncodingTester fileNameEncodingTester;
    private ApplicationConfiguration applicationConfig;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public boolean isFileCreatedSuccessfully() {
        return this.fileNameEncodingTester.isFileCreationSuccessful();
    }

    public Throwable getFileCreationException() {
        return this.fileNameEncodingTester.getFileCreationException();
    }

    public boolean isFileNameMangled() {
        return this.fileNameEncodingTester.isFileNameMangled();
    }

    public String getRawTestString() {
        return TEST_STRING;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String execute() throws HibernateException, SQLException {
        SpringHostConnectionAccessor.ConnectionProvider connectionProvider = (SpringHostConnectionAccessor.ConnectionProvider)ContainerManager.getComponent((String)"salConnectionProvider");
        Connection conn = connectionProvider.getConnection();
        long contentId = this.getUseableContentId(conn);
        long bodyContentId = this.getUseableBodyContentId(conn);
        try {
            this.createPage(conn, contentId, bodyContentId, this.submittedTestString.trim());
            this.databasedTestString = this.retrieveLowerDatabaseTestString(conn, contentId);
        }
        finally {
            this.deleteTestData(conn, contentId);
        }
        long contentTwoId = this.getUseableContentId(conn);
        long bodyContentTwoId = this.getUseableBodyContentId(conn);
        try {
            this.createPage(conn, contentTwoId, bodyContentTwoId, this.submittedTestString.trim());
            this.databasedLowercaseTestString = this.retrieveUpperDatabaseTestString(conn, contentTwoId);
        }
        finally {
            this.deleteTestData(conn, contentId);
        }
        File confluenceTemp = new File(this.getBootstrapManager().getFilePathProperty("struts.multipart.saveDir"));
        this.fileNameEncodingTester = new FileNameEncodingTester(confluenceTemp);
        return "success";
    }

    public String getDefaultEncoding() {
        return this.getGlobalSettings().getDefaultEncoding();
    }

    private boolean isSqlServer() {
        return ((String)this.applicationConfig.getProperty((Object)"hibernate.dialect")).contains("SQLServer");
    }

    private String retrieveLowerDatabaseTestString(Connection conn, long id) throws SQLException {
        String query = this.isSqlServer() ? "select LOWER(SUBSTRING(bc.BODY,1,DATALENGTH(bc.BODY))) from CONTENT c inner join BODYCONTENT bc on bc.CONTENTID = c.CONTENTID where c.CONTENTID = ?" : "select LOWER(bc.BODY) from CONTENT c inner join BODYCONTENT bc on bc.CONTENTID = c.CONTENTID where c.CONTENTID = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);){
            String string;
            block12: {
                ps.setLong(1, id);
                ResultSet rs = ps.executeQuery();
                try {
                    rs.next();
                    string = rs.getString(1);
                    if (rs == null) break block12;
                }
                catch (Throwable throwable) {
                    if (rs != null) {
                        try {
                            rs.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                rs.close();
            }
            return string;
        }
    }

    private String retrieveUpperDatabaseTestString(Connection conn, long id) throws SQLException {
        String query = this.isSqlServer() ? "select UPPER(SUBSTRING(bc.BODY,1,DATALENGTH(bc.BODY))) from CONTENT c inner join BODYCONTENT bc on bc.CONTENTID = c.CONTENTID where c.CONTENTID = ?" : "select UPPER(bc.BODY) from CONTENT c inner join BODYCONTENT bc on bc.CONTENTID = c.CONTENTID where c.CONTENTID = ?";
        try (PreparedStatement ps = conn.prepareStatement(query);){
            String string;
            block12: {
                ps.setLong(1, id);
                ResultSet rs = ps.executeQuery();
                try {
                    rs.next();
                    string = rs.getString(1);
                    if (rs == null) break block12;
                }
                catch (Throwable throwable) {
                    if (rs != null) {
                        try {
                            rs.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                rs.close();
            }
            return string;
        }
    }

    private void createPage(Connection conn, long id, long bodyId, String content) throws SQLException {
        try (PreparedStatement contentPs = conn.prepareStatement("insert into CONTENT (CONTENTID, CONTENTTYPE) values (?, ?)");
             PreparedStatement bodyContentPs = conn.prepareStatement("insert into BODYCONTENT (BODYCONTENTID, BODY, CONTENTID) values (?, ?, ?)");){
            contentPs.setLong(1, id);
            contentPs.setString(2, "CHEESE");
            contentPs.execute();
            bodyContentPs.setLong(1, bodyId);
            bodyContentPs.setString(2, content);
            bodyContentPs.setLong(3, id);
            bodyContentPs.execute();
            conn.commit();
        }
    }

    private long getUseableContentId(Connection conn) throws SQLException {
        try (Statement statement = conn.createStatement();){
            long l;
            block12: {
                ResultSet rs = statement.executeQuery("select max(CONTENTID) from CONTENT");
                try {
                    rs.next();
                    l = rs.getLong(1) + 1000L;
                    if (rs == null) break block12;
                }
                catch (Throwable throwable) {
                    if (rs != null) {
                        try {
                            rs.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                rs.close();
            }
            return l;
        }
    }

    private long getUseableBodyContentId(Connection conn) throws SQLException {
        try (Statement statement = conn.createStatement();){
            long l;
            block12: {
                ResultSet rs = statement.executeQuery("select max(BODYCONTENTID) from BODYCONTENT");
                try {
                    rs.next();
                    l = rs.getLong(1) + 1000L;
                    if (rs == null) break block12;
                }
                catch (Throwable throwable) {
                    if (rs != null) {
                        try {
                            rs.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                rs.close();
            }
            return l;
        }
    }

    private void deleteTestData(Connection conn, long contentId) throws SQLException {
        List<String> deletes = Arrays.asList("delete from BODYCONTENT where CONTENTID = ?", "delete from CONTENT where CONTENTID = ?");
        for (String delete : deletes) {
            PreparedStatement statement = conn.prepareStatement(delete);
            try {
                statement.setLong(1, contentId);
                statement.execute();
            }
            finally {
                if (statement == null) continue;
                statement.close();
            }
        }
    }

    public String getSubmittedTestString() {
        return this.submittedTestString;
    }

    public void setSubmittedTestString(String submittedTestString) {
        this.submittedTestString = submittedTestString;
    }

    public String getDatabasedTestString() {
        return this.databasedTestString;
    }

    public void setDatabasedTestString(String databasedTestString) {
        this.databasedTestString = databasedTestString;
    }

    public String getDatabasedLowercaseTestString() {
        return this.databasedLowercaseTestString;
    }

    public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
        this.applicationConfig = applicationConfig;
    }
}

