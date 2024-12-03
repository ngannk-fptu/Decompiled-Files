/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceAuthenticator
 *  com.atlassian.confluence.user.ConfluenceCrowdSSOAuthenticator
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.seraph.auth.Authenticator
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck.directory.internal;

import com.atlassian.confluence.user.ConfluenceAuthenticator;
import com.atlassian.confluence.user.ConfluenceCrowdSSOAuthenticator;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.troubleshooting.api.healthcheck.DatabaseService;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.confluence.healthcheck.analytics.InternalAdminCheckFallbackEvent;
import com.atlassian.troubleshooting.confluence.healthcheck.directory.internal.AuthenticatorProvider;
import com.atlassian.troubleshooting.confluence.healthcheck.directory.internal.InternalAdminCheckFallback;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class InternalAdminCheck
implements SupportHealthCheck {
    private static final Logger LOGGER = LoggerFactory.getLogger(InternalAdminCheck.class);
    private static final char ACTIVE = 'T';
    private static final List<String> ADMINISTRATOR_PERM_TYPES = Arrays.asList("ADMINISTRATECONFLUENCE", "SYSTEMADMINISTRATOR");
    private final CrowdDirectoryService crowdDirectoryService;
    private final DatabaseService databaseService;
    private final SupportHealthStatusBuilder supportHealthStatusBuilder;
    private final InternalAdminCheckFallback internalAdminCheckFallback;
    private final EventPublisher eventPublisher;
    private final AuthenticatorProvider authenticatorProvider;

    @Autowired
    public InternalAdminCheck(CrowdDirectoryService crowdDirectoryService, DatabaseService databaseService, SupportHealthStatusBuilder supportHealthStatusBuilder, InternalAdminCheckFallback internalAdminCheckFallback, AuthenticatorProvider authenticatorProvider, EventPublisher eventPublisher) {
        this.databaseService = databaseService;
        this.supportHealthStatusBuilder = supportHealthStatusBuilder;
        this.crowdDirectoryService = crowdDirectoryService;
        this.internalAdminCheckFallback = internalAdminCheckFallback;
        this.eventPublisher = eventPublisher;
        this.authenticatorProvider = authenticatorProvider;
    }

    @Override
    public boolean isNodeSpecific() {
        return false;
    }

    @Override
    public SupportHealthStatus check() {
        Authenticator authenticator = this.authenticatorProvider.getAuthenticator();
        if (authenticator instanceof ConfluenceCrowdSSOAuthenticator) {
            return this.supportHealthStatusBuilder.ok(this, "confluence.healthcheck.directory.internal.sso.present", new Serializable[0]);
        }
        if (!(authenticator instanceof ConfluenceAuthenticator)) {
            return this.supportHealthStatusBuilder.ok(this, "confluence.healthcheck.directory.internal.custom.authenticator", new Serializable[0]);
        }
        boolean hasInternalDirectory = this.crowdDirectoryService.findAllDirectories().stream().anyMatch(directory -> directory.getType() == DirectoryType.INTERNAL && directory.isActive());
        if (!hasInternalDirectory) {
            return this.supportHealthStatusBuilder.major(this, "confluence.healthcheck.directory.internal.has.internal.dir.fail", new Serializable[0]);
        }
        try {
            int users = this.databaseService.runInConnection(this::countUsersInAdminGroupQuery);
            if (users == 0 && (users = this.databaseService.runInConnection(this::countUsersWithAdminPermissionQuery).intValue()) == 0) {
                return this.supportHealthStatusBuilder.major(this, "confluence.healthcheck.directory.internal.admin.fail", new Serializable[0]);
            }
        }
        catch (Exception ex) {
            LOGGER.error("An error occurred when performing the Internal Administrator User health check, we're going to try to fallback: ", (Throwable)ex);
            this.eventPublisher.publish((Object)new InternalAdminCheckFallbackEvent());
            return this.internalAdminCheckFallback.check();
        }
        return this.supportHealthStatusBuilder.ok(this, "confluence.healthcheck.directory.internal.admin.ok", new Serializable[0]);
    }

    private int countUsersInAdminGroupQuery(Connection connection) {
        String query = String.format("SELECT COUNT(DISTINCT cwd_user.user_name) FROM cwd_membership cm LEFT OUTER JOIN cwd_user ON cm.child_user_id = cwd_user.id LEFT OUTER JOIN cwd_group ON cm.parent_id = cwd_group.id LEFT OUTER JOIN cwd_directory ON cwd_directory.id = cwd_group.directory_id WHERE cwd_group.lower_group_name IN (SELECT sp.PERMGROUPNAME FROM SPACEPERMISSIONS sp WHERE sp.PERMTYPE IN (%s)) AND cwd_user.active = ? AND cwd_group.active = ? AND cwd_directory.directory_type = ?", ADMINISTRATOR_PERM_TYPES.stream().map(v -> "?").collect(Collectors.joining(",")));
        ArrayList<String> params = new ArrayList<String>(ADMINISTRATOR_PERM_TYPES);
        params.add(Character.toString('T'));
        params.add(Character.toString('T'));
        params.add(DirectoryType.INTERNAL.name());
        return this.getQueryResult(connection, query, params);
    }

    private int countUsersWithAdminPermissionQuery(Connection connection) {
        String query = String.format("SELECT COUNT(DISTINCT cu.user_name) FROM cwd_user cu LEFT OUTER JOIN cwd_directory ON cu.directory_id = cwd_directory.id WHERE cu.user_name IN (SELECT user_mapping.username FROM SPACEPERMISSIONS sp LEFT OUTER JOIN user_mapping ON user_mapping.user_key = sp.PERMUSERNAME WHERE sp.PERMTYPE IN (%s)) AND cu.active = ? AND cwd_directory.directory_type = ?", ADMINISTRATOR_PERM_TYPES.stream().map(v -> "?").collect(Collectors.joining(",")));
        ArrayList<String> params = new ArrayList<String>(ADMINISTRATOR_PERM_TYPES);
        params.add(Character.toString('T'));
        params.add(DirectoryType.INTERNAL.name());
        return this.getQueryResult(connection, query, params);
    }

    /*
     * Exception decompiling
     */
    private int getQueryResult(Connection connection, String query, List<String> params) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }
}

