/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.log4j.Logger
 */
package com.atlassian.confluence.impl.admin.actions;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.logging.LogAppenderController;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@RequiresAnyConfluenceAccess
public class MarkAllLogsAction
extends ConfluenceActionSupport {
    @VisibleForTesting
    static Logger log = Logger.getLogger(MarkAllLogsAction.class);
    @VisibleForTesting
    static Runnable rolloverRunner = LogAppenderController::rolloverAppenders;
    private String nodeId;
    private MarkLogsTask task = new MarkLogsTask();
    private ClusterManager clusterManager;

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public void setLogsMarkMessage(String logsMarkMessage) {
        this.task.message = logsMarkMessage;
    }

    public void setLogsMarkRollover(boolean logsMarkRollover) {
        this.task.rollover = logsMarkRollover;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public void setClusterManager(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    @PermittedMethods(value={HttpMethod.POST})
    public String execute() throws Exception {
        this.clusterManager.submitToNode(StringUtils.trimToNull((String)this.nodeId), this.task, "cluster-manager-executor").getCompletionStage().toCompletableFuture().get();
        return "success";
    }

    private static class MarkLogsTask
    implements Callable<Void>,
    Serializable {
        String message;
        boolean rollover;

        private MarkLogsTask() {
        }

        @Override
        public Void call() throws Exception {
            log.info((Object)"");
            if (this.rollover) {
                rolloverRunner.run();
            }
            log.info((Object)("\n************************************************\n" + this.message + "\n************************************************\n"));
            return null;
        }
    }
}

