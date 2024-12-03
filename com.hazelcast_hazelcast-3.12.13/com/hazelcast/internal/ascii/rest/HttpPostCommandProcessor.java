/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.rest;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.cp.CPSubsystem;
import com.hazelcast.cp.CPSubsystemManagementService;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.ascii.rest.HttpCommand;
import com.hazelcast.internal.ascii.rest.HttpCommandProcessor;
import com.hazelcast.internal.ascii.rest.HttpPostCommand;
import com.hazelcast.internal.ascii.rest.RestValue;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.json.Json;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.dto.WanReplicationConfigDTO;
import com.hazelcast.internal.management.operation.SetLicenseOperation;
import com.hazelcast.internal.util.InvocationUtil;
import com.hazelcast.logging.ILogger;
import com.hazelcast.security.SecurityContext;
import com.hazelcast.security.UsernamePasswordCredentials;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.JsonUtil;
import com.hazelcast.util.StringUtil;
import com.hazelcast.util.function.Supplier;
import com.hazelcast.version.Version;
import com.hazelcast.wan.AddWanConfigResult;
import com.hazelcast.wan.WanReplicationService;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public class HttpPostCommandProcessor
extends HttpCommandProcessor<HttpPostCommand> {
    private static final byte[] QUEUE_SIMPLE_VALUE_CONTENT_TYPE = StringUtil.stringToBytes("text/plain");
    private final ILogger logger;

    public HttpPostCommandProcessor(TextCommandService textCommandService) {
        super(textCommandService);
        this.logger = textCommandService.getNode().getLogger(HttpPostCommandProcessor.class);
    }

    @Override
    public void handle(HttpPostCommand command) {
        boolean sendResponse = true;
        try {
            String uri = command.getURI();
            if (uri.startsWith("/hazelcast/rest/maps/")) {
                this.handleMap(command, uri);
            } else if (uri.startsWith("/hazelcast/rest/mancenter/changeurl")) {
                this.handleManagementCenterUrlChange(command);
            } else if (uri.startsWith("/hazelcast/rest/queues/")) {
                this.handleQueue(command, uri);
            } else if (uri.startsWith("/hazelcast/rest/management/cluster/state")) {
                this.handleGetClusterState(command);
            } else if (uri.startsWith("/hazelcast/rest/management/cluster/changeState")) {
                this.handleChangeClusterState(command);
            } else if (uri.startsWith("/hazelcast/rest/management/cluster/version")) {
                this.handleChangeClusterVersion(command);
            } else {
                if (uri.startsWith("/hazelcast/rest/management/cluster/clusterShutdown")) {
                    this.handleClusterShutdown(command);
                    return;
                }
                if (uri.startsWith("/hazelcast/rest/management/cluster/forceStart")) {
                    this.handleForceStart(command);
                } else if (uri.startsWith("/hazelcast/rest/management/cluster/hotBackupInterrupt")) {
                    this.handleHotRestartBackupInterrupt(command);
                } else if (uri.startsWith("/hazelcast/rest/management/cluster/hotBackup")) {
                    this.handleHotRestartBackup(command);
                } else if (uri.startsWith("/hazelcast/rest/management/cluster/partialStart")) {
                    this.handlePartialStart(command);
                } else if (uri.startsWith("/hazelcast/rest/management/cluster/nodes")) {
                    this.handleListNodes(command);
                } else if (uri.startsWith("/hazelcast/rest/management/cluster/memberShutdown")) {
                    this.handleShutdownNode(command);
                } else if (uri.startsWith("/hazelcast/rest/mancenter/wan/sync/map") || uri.startsWith("/hazelcast/rest/wan/sync/map")) {
                    this.handleWanSyncMap(command);
                } else if (uri.startsWith("/hazelcast/rest/mancenter/wan/sync/allmaps") || uri.startsWith("/hazelcast/rest/wan/sync/allmaps")) {
                    this.handleWanSyncAllMaps(command);
                } else if (uri.startsWith("/hazelcast/rest/mancenter/wan/clearWanQueues") || uri.startsWith("/hazelcast/rest/mancenter/clearWanQueues")) {
                    this.handleWanClearQueues(command);
                } else if (uri.startsWith("/hazelcast/rest/mancenter/wan/addWanConfig") || uri.startsWith("/hazelcast/rest/wan/addWanConfig")) {
                    this.handleAddWanConfig(command);
                } else if (uri.startsWith("/hazelcast/rest/mancenter/wan/pausePublisher")) {
                    this.handleWanPausePublisher(command);
                } else if (uri.startsWith("/hazelcast/rest/mancenter/wan/stopPublisher")) {
                    this.handleWanStopPublisher(command);
                } else if (uri.startsWith("/hazelcast/rest/mancenter/wan/resumePublisher")) {
                    this.handleWanResumePublisher(command);
                } else if (uri.startsWith("/hazelcast/rest/mancenter/wan/consistencyCheck/map")) {
                    this.handleWanConsistencyCheck(command);
                } else if (uri.startsWith("/hazelcast/rest/mancenter/security/permissions")) {
                    this.handleUpdatePermissions(command);
                } else if (uri.startsWith("/hazelcast/rest/cp-subsystem/members")) {
                    this.handleCPMember(command);
                    sendResponse = false;
                } else if (uri.startsWith("/hazelcast/rest/cp-subsystem/groups")) {
                    this.handleCPGroup(command);
                    sendResponse = false;
                } else if (uri.startsWith("/hazelcast/rest/cp-subsystem/restart")) {
                    this.handleResetAndInitCPSubsystem(command);
                    sendResponse = false;
                } else if (uri.startsWith("/hazelcast/rest/license")) {
                    this.handleSetLicense(command);
                } else {
                    command.send404();
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            command.send400();
        }
        catch (Exception e) {
            command.send500();
        }
        if (sendResponse) {
            this.textCommandService.sendResponse(command);
        }
    }

    private void handleChangeClusterState(HttpPostCommand command) throws UnsupportedEncodingException {
        String res;
        byte[] data = command.getData();
        String[] strList = StringUtil.bytesToString(data).split("&");
        try {
            Node node = this.textCommandService.getNode();
            ClusterServiceImpl clusterService = node.getClusterService();
            if (this.authenticate(command, strList[0], strList.length > 1 ? strList[1] : null)) {
                String stateParam = URLDecoder.decode(strList[2], "UTF-8");
                ClusterState state = ClusterState.valueOf(StringUtil.upperCaseInternal(stateParam));
                if (!state.equals((Object)clusterService.getClusterState())) {
                    clusterService.changeClusterState(state);
                    res = HttpPostCommandProcessor.response(ResponseType.SUCCESS, "state", state.toString().toLowerCase(StringUtil.LOCALE_INTERNAL));
                } else {
                    res = HttpPostCommandProcessor.response(ResponseType.FAIL, "state", state.toString().toLowerCase(StringUtil.LOCALE_INTERNAL));
                }
            } else {
                res = HttpPostCommandProcessor.response(ResponseType.FORBIDDEN, new Object[0]);
            }
        }
        catch (Throwable throwable) {
            this.logger.warning("Error occurred while changing cluster state", throwable);
            res = HttpPostCommandProcessor.exceptionResponse(throwable);
        }
        command.setResponse(HttpCommand.CONTENT_TYPE_JSON, StringUtil.stringToBytes(res));
    }

    private void handleGetClusterState(HttpPostCommand command) {
        String res;
        try {
            Node node = this.textCommandService.getNode();
            ClusterServiceImpl clusterService = node.getClusterService();
            if (!this.checkCredentials(command)) {
                res = HttpPostCommandProcessor.response(ResponseType.FORBIDDEN, new Object[0]);
            } else {
                ClusterState clusterState = clusterService.getClusterState();
                res = HttpPostCommandProcessor.response(ResponseType.SUCCESS, "state", StringUtil.lowerCaseInternal(clusterState.toString()));
            }
        }
        catch (Throwable throwable) {
            this.logger.warning("Error occurred while getting cluster state", throwable);
            res = HttpPostCommandProcessor.exceptionResponse(throwable);
        }
        command.setResponse(HttpCommand.CONTENT_TYPE_JSON, StringUtil.stringToBytes(res));
    }

    private void handleChangeClusterVersion(HttpPostCommand command) throws UnsupportedEncodingException {
        String res;
        byte[] data = command.getData();
        String[] strList = StringUtil.bytesToString(data).split("&");
        try {
            Node node = this.textCommandService.getNode();
            ClusterServiceImpl clusterService = node.getClusterService();
            if (this.authenticate(command, strList[0], strList.length > 1 ? strList[1] : null)) {
                String versionParam = URLDecoder.decode(strList[2], "UTF-8");
                Version version = Version.of(versionParam);
                clusterService.changeClusterVersion(version);
                res = HttpPostCommandProcessor.response(ResponseType.SUCCESS, "version", clusterService.getClusterVersion().toString());
            } else {
                res = HttpPostCommandProcessor.response(ResponseType.FORBIDDEN, new Object[0]);
            }
        }
        catch (Throwable throwable) {
            this.logger.warning("Error occurred while changing cluster version", throwable);
            res = HttpPostCommandProcessor.exceptionResponse(throwable);
        }
        command.setResponse(HttpCommand.CONTENT_TYPE_JSON, StringUtil.stringToBytes(res));
    }

    private void handleForceStart(HttpPostCommand command) {
        String res;
        try {
            boolean success;
            Node node = this.textCommandService.getNode();
            res = !this.checkCredentials(command) ? HttpPostCommandProcessor.response(ResponseType.FORBIDDEN, new Object[0]) : HttpPostCommandProcessor.response((success = node.getNodeExtension().getInternalHotRestartService().triggerForceStart()) ? ResponseType.SUCCESS : ResponseType.FAIL, new Object[0]);
        }
        catch (Throwable throwable) {
            this.logger.warning("Error occurred while handling force start", throwable);
            res = HttpPostCommandProcessor.exceptionResponse(throwable);
        }
        this.sendResponse(command, res);
    }

    private void handlePartialStart(HttpPostCommand command) {
        String res;
        try {
            boolean success;
            Node node = this.textCommandService.getNode();
            res = !this.checkCredentials(command) ? HttpPostCommandProcessor.response(ResponseType.FORBIDDEN, new Object[0]) : HttpPostCommandProcessor.response((success = node.getNodeExtension().getInternalHotRestartService().triggerPartialStart()) ? ResponseType.SUCCESS : ResponseType.FAIL, new Object[0]);
        }
        catch (Throwable throwable) {
            this.logger.warning("Error occurred while handling partial start", throwable);
            res = HttpPostCommandProcessor.exceptionResponse(throwable);
        }
        this.sendResponse(command, res);
    }

    private void handleHotRestartBackup(HttpPostCommand command) {
        String res;
        try {
            if (this.checkCredentials(command)) {
                this.textCommandService.getNode().getNodeExtension().getHotRestartService().backup();
                res = HttpPostCommandProcessor.response(ResponseType.SUCCESS, new Object[0]);
            } else {
                res = HttpPostCommandProcessor.response(ResponseType.FORBIDDEN, new Object[0]);
            }
        }
        catch (Throwable throwable) {
            this.logger.warning("Error occurred while invoking hot backup", throwable);
            res = HttpPostCommandProcessor.exceptionResponse(throwable);
        }
        this.sendResponse(command, res);
    }

    private void handleHotRestartBackupInterrupt(HttpPostCommand command) {
        String res;
        try {
            if (this.checkCredentials(command)) {
                this.textCommandService.getNode().getNodeExtension().getHotRestartService().interruptBackupTask();
                res = HttpPostCommandProcessor.response(ResponseType.SUCCESS, new Object[0]);
            } else {
                res = HttpPostCommandProcessor.response(ResponseType.FORBIDDEN, new Object[0]);
            }
        }
        catch (Throwable throwable) {
            this.logger.warning("Error occurred while interrupting hot backup", throwable);
            res = HttpPostCommandProcessor.exceptionResponse(throwable);
        }
        this.sendResponse(command, res);
    }

    private void handleClusterShutdown(HttpPostCommand command) {
        String res;
        try {
            Node node = this.textCommandService.getNode();
            ClusterServiceImpl clusterService = node.getClusterService();
            if (this.checkCredentials(command)) {
                String res2 = HttpPostCommandProcessor.response(ResponseType.SUCCESS, new Object[0]);
                this.sendResponse(command, res2);
                clusterService.shutdown();
                return;
            }
            res = HttpPostCommandProcessor.response(ResponseType.FORBIDDEN, new Object[0]);
        }
        catch (Throwable throwable) {
            this.logger.warning("Error occurred while shutting down cluster", throwable);
            res = HttpPostCommandProcessor.exceptionResponse(throwable);
        }
        this.sendResponse(command, res);
    }

    private void handleListNodes(HttpPostCommand command) {
        String res;
        try {
            Node node = this.textCommandService.getNode();
            ClusterServiceImpl clusterService = node.getClusterService();
            if (this.checkCredentials(command)) {
                String responseTxt = clusterService.getMembers().toString() + "\n" + node.getBuildInfo().getVersion() + "\n" + System.getProperty("java.version");
                String res2 = HttpPostCommandProcessor.response(ResponseType.SUCCESS, "response", responseTxt);
                this.sendResponse(command, res2);
                return;
            }
            res = HttpPostCommandProcessor.response(ResponseType.FORBIDDEN, new Object[0]);
        }
        catch (Throwable throwable) {
            this.logger.warning("Error occurred while listing nodes", throwable);
            res = HttpPostCommandProcessor.exceptionResponse(throwable);
        }
        this.sendResponse(command, res);
    }

    private void handleShutdownNode(HttpPostCommand command) {
        String res;
        try {
            Node node = this.textCommandService.getNode();
            if (this.checkCredentials(command)) {
                String res2 = HttpPostCommandProcessor.response(ResponseType.SUCCESS, new Object[0]);
                this.sendResponse(command, res2);
                node.hazelcastInstance.shutdown();
                return;
            }
            res = HttpPostCommandProcessor.response(ResponseType.FORBIDDEN, new Object[0]);
        }
        catch (Throwable throwable) {
            this.logger.warning("Error occurred while shutting down", throwable);
            res = HttpPostCommandProcessor.exceptionResponse(throwable);
        }
        this.sendResponse(command, res);
    }

    private void handleQueue(HttpPostCommand command, String uri) {
        byte[] contentType;
        byte[] data;
        String queueName;
        String simpleValue = null;
        String suffix = uri.endsWith("/") ? uri.substring("/hazelcast/rest/queues/".length(), uri.length() - 1) : uri.substring("/hazelcast/rest/queues/".length());
        int indexSlash = suffix.lastIndexOf(47);
        if (indexSlash == -1) {
            queueName = suffix;
        } else {
            queueName = suffix.substring(0, indexSlash);
            simpleValue = suffix.substring(indexSlash + 1);
        }
        if (simpleValue == null) {
            data = command.getData();
            contentType = command.getContentType();
        } else {
            data = StringUtil.stringToBytes(simpleValue);
            contentType = QUEUE_SIMPLE_VALUE_CONTENT_TYPE;
        }
        boolean offerResult = this.textCommandService.offer(queueName, new RestValue(data, contentType));
        if (offerResult) {
            command.send200();
        } else {
            command.setResponse(HttpCommand.RES_503);
        }
    }

    private void handleManagementCenterUrlChange(HttpPostCommand command) throws UnsupportedEncodingException {
        byte[] res;
        HazelcastProperties properties = this.textCommandService.getNode().getProperties();
        if (!properties.getBoolean(GroupProperty.MC_URL_CHANGE_ENABLED)) {
            this.logger.warning("Hazelcast property " + GroupProperty.MC_URL_CHANGE_ENABLED.getName() + " is deprecated.");
            command.setResponse(HttpCommand.RES_503);
            return;
        }
        String[] strList = StringUtil.bytesToString(command.getData()).split("&");
        if (this.authenticate(command, strList[0], strList.length > 1 ? strList[1] : null)) {
            ManagementCenterService managementCenterService = this.textCommandService.getNode().getManagementCenterService();
            if (managementCenterService != null) {
                String url = URLDecoder.decode(strList[2], "UTF-8");
                res = managementCenterService.clusterWideUpdateManagementCenterUrl(url);
            } else {
                this.logger.warning("Unable to change URL of ManagementCenter as the ManagementCenterService is not running on this member.");
                res = HttpCommand.RES_204;
            }
        } else {
            res = HttpCommand.RES_403;
        }
        command.setResponse(res);
    }

    private void handleMap(HttpPostCommand command, String uri) {
        int indexEnd = uri.indexOf(47, "/hazelcast/rest/maps/".length());
        String mapName = uri.substring("/hazelcast/rest/maps/".length(), indexEnd);
        String key = uri.substring(indexEnd + 1);
        byte[] data = command.getData();
        this.textCommandService.put(mapName, key, new RestValue(data, command.getContentType()), -1);
        command.send200();
    }

    private void handleWanSyncMap(HttpPostCommand command) throws UnsupportedEncodingException {
        String res;
        String[] params = HttpPostCommandProcessor.decodeParams(command, 3);
        String wanRepName = params[0];
        String publisherId = params[1];
        String mapName = params[2];
        try {
            this.textCommandService.getNode().getNodeEngine().getWanReplicationService().syncMap(wanRepName, publisherId, mapName);
            res = HttpPostCommandProcessor.response(ResponseType.SUCCESS, "message", "Sync initiated");
        }
        catch (Exception ex) {
            this.logger.warning("Error occurred while syncing map", ex);
            res = HttpPostCommandProcessor.exceptionResponse(ex);
        }
        this.sendResponse(command, res);
    }

    private void handleWanSyncAllMaps(HttpPostCommand command) throws UnsupportedEncodingException {
        String res;
        String[] params = HttpPostCommandProcessor.decodeParams(command, 2);
        String wanRepName = params[0];
        String publisherId = params[1];
        try {
            this.textCommandService.getNode().getNodeEngine().getWanReplicationService().syncAllMaps(wanRepName, publisherId);
            res = HttpPostCommandProcessor.response(ResponseType.SUCCESS, "message", "Sync initiated");
        }
        catch (Exception ex) {
            this.logger.warning("Error occurred while syncing maps", ex);
            res = HttpPostCommandProcessor.exceptionResponse(ex);
        }
        this.sendResponse(command, res);
    }

    private void handleWanConsistencyCheck(HttpPostCommand command) throws UnsupportedEncodingException {
        String res;
        String[] params = HttpPostCommandProcessor.decodeParams(command, 3);
        String wanReplicationName = params[0];
        String publisherId = params[1];
        String mapName = params[2];
        WanReplicationService service = this.textCommandService.getNode().getNodeEngine().getWanReplicationService();
        try {
            service.consistencyCheck(wanReplicationName, publisherId, mapName);
            res = HttpPostCommandProcessor.response(ResponseType.SUCCESS, "message", "Consistency check initiated");
        }
        catch (Exception ex) {
            this.logger.warning("Error occurred while initiating consistency check", ex);
            res = HttpPostCommandProcessor.exceptionResponse(ex);
        }
        this.sendResponse(command, res);
    }

    private void handleWanClearQueues(HttpPostCommand command) throws UnsupportedEncodingException {
        String res;
        String[] params = HttpPostCommandProcessor.decodeParams(command, 2);
        String wanRepName = params[0];
        String publisherId = params[1];
        try {
            this.textCommandService.getNode().getNodeEngine().getWanReplicationService().clearQueues(wanRepName, publisherId);
            res = HttpPostCommandProcessor.response(ResponseType.SUCCESS, "message", "WAN replication queues are cleared.");
        }
        catch (Exception ex) {
            this.logger.warning("Error occurred while clearing queues", ex);
            res = HttpPostCommandProcessor.exceptionResponse(ex);
        }
        this.sendResponse(command, res);
    }

    private void handleAddWanConfig(HttpPostCommand command) throws UnsupportedEncodingException {
        String res;
        String[] params = HttpPostCommandProcessor.decodeParams(command, 1);
        String wanConfigJson = params[0];
        try {
            WanReplicationConfigDTO dto = new WanReplicationConfigDTO(new WanReplicationConfig());
            dto.fromJson(Json.parse(wanConfigJson).asObject());
            AddWanConfigResult result = this.textCommandService.getNode().getNodeEngine().getWanReplicationService().addWanReplicationConfig(dto.getConfig());
            res = HttpPostCommandProcessor.response(ResponseType.SUCCESS, "message", "WAN configuration added.", "addedPublisherIds", result.getAddedPublisherIds(), "ignoredPublisherIds", result.getIgnoredPublisherIds());
        }
        catch (Exception ex) {
            this.logger.warning("Error occurred while adding WAN config", ex);
            res = HttpPostCommandProcessor.exceptionResponse(ex);
        }
        command.setResponse(HttpCommand.CONTENT_TYPE_JSON, StringUtil.stringToBytes(res));
    }

    private void handleWanPausePublisher(HttpPostCommand command) throws UnsupportedEncodingException {
        String res;
        String[] params = HttpPostCommandProcessor.decodeParams(command, 2);
        String wanReplicationName = params[0];
        String publisherId = params[1];
        WanReplicationService service = this.textCommandService.getNode().getNodeEngine().getWanReplicationService();
        try {
            service.pause(wanReplicationName, publisherId);
            res = HttpPostCommandProcessor.response(ResponseType.SUCCESS, "message", "WAN publisher paused");
        }
        catch (Exception ex) {
            this.logger.warning("Error occurred while pausing WAN publisher", ex);
            res = HttpPostCommandProcessor.exceptionResponse(ex);
        }
        this.sendResponse(command, res);
    }

    private void handleWanStopPublisher(HttpPostCommand command) throws UnsupportedEncodingException {
        String res;
        String[] params = HttpPostCommandProcessor.decodeParams(command, 2);
        String wanReplicationName = params[0];
        String publisherId = params[1];
        WanReplicationService service = this.textCommandService.getNode().getNodeEngine().getWanReplicationService();
        try {
            service.stop(wanReplicationName, publisherId);
            res = HttpPostCommandProcessor.response(ResponseType.SUCCESS, "message", "WAN publisher stopped");
        }
        catch (Exception ex) {
            this.logger.warning("Error occurred while stopping WAN publisher", ex);
            res = HttpPostCommandProcessor.exceptionResponse(ex);
        }
        this.sendResponse(command, res);
    }

    private void handleWanResumePublisher(HttpPostCommand command) throws UnsupportedEncodingException {
        String res;
        String[] params = HttpPostCommandProcessor.decodeParams(command, 2);
        String wanReplicationName = params[0];
        String publisherId = params[1];
        WanReplicationService service = this.textCommandService.getNode().getNodeEngine().getWanReplicationService();
        try {
            service.resume(wanReplicationName, publisherId);
            res = HttpPostCommandProcessor.response(ResponseType.SUCCESS, "message", "WAN publisher resumed");
        }
        catch (Exception ex) {
            this.logger.warning("Error occurred while resuming WAN publisher", ex);
            res = HttpPostCommandProcessor.exceptionResponse(ex);
        }
        this.sendResponse(command, res);
    }

    private void handleUpdatePermissions(HttpPostCommand command) {
        String res = HttpPostCommandProcessor.response(ResponseType.FORBIDDEN, new Object[0]);
        command.setResponse(HttpCommand.CONTENT_TYPE_JSON, StringUtil.stringToBytes(res));
    }

    private void handleCPMember(HttpPostCommand command) throws UnsupportedEncodingException {
        if (!this.checkCredentials(command)) {
            command.send403();
            this.textCommandService.sendResponse(command);
            return;
        }
        String uri = command.getURI();
        if (uri.endsWith("/remove") || uri.endsWith("/remove/")) {
            this.handleRemoveCPMember(command);
        } else {
            this.handlePromoteToCPMember(command);
        }
    }

    private void handlePromoteToCPMember(final HttpPostCommand command) {
        if (this.getCpSubsystem().getLocalCPMember() != null) {
            command.send200();
            this.textCommandService.sendResponse(command);
            return;
        }
        this.getCpSubsystemManagementService().promoteToCPMember().andThen(new ExecutionCallback<Void>(){

            @Override
            public void onResponse(Void response) {
                command.send200();
                HttpPostCommandProcessor.this.textCommandService.sendResponse(command);
            }

            @Override
            public void onFailure(Throwable t) {
                HttpPostCommandProcessor.this.logger.warning("Error while promoting CP member.", t);
                command.send500();
                HttpPostCommandProcessor.this.textCommandService.sendResponse(command);
            }
        });
    }

    private void handleRemoveCPMember(final HttpPostCommand command) {
        String uri = command.getURI();
        String prefix = "/hazelcast/rest/cp-subsystem/members/";
        final String cpMemberUid = uri.substring(prefix.length(), uri.indexOf(47, prefix.length())).trim();
        this.getCpSubsystem().getCPSubsystemManagementService().removeCPMember(cpMemberUid).andThen(new ExecutionCallback<Void>(){

            @Override
            public void onResponse(Void response) {
                command.send200();
                HttpPostCommandProcessor.this.textCommandService.sendResponse(command);
            }

            @Override
            public void onFailure(Throwable t) {
                HttpPostCommandProcessor.this.logger.warning("Error while removing CP member " + cpMemberUid, t);
                if (ExceptionUtil.peel(t) instanceof IllegalArgumentException) {
                    command.send400();
                } else {
                    command.send500();
                }
                HttpPostCommandProcessor.this.textCommandService.sendResponse(command);
            }
        });
    }

    private void handleCPGroup(HttpPostCommand command) throws UnsupportedEncodingException {
        if (!this.checkCredentials(command)) {
            command.send403();
            this.textCommandService.sendResponse(command);
            return;
        }
        String uri = command.getURI();
        if (!uri.endsWith("/remove") && !uri.endsWith("/remove/")) {
            command.send404();
            this.textCommandService.sendResponse(command);
            return;
        }
        if (uri.contains("/sessions")) {
            this.handleForceCloseCPSession(command);
        } else {
            this.handleForceDestroyCPGroup(command);
        }
    }

    private void handleForceCloseCPSession(final HttpPostCommand command) {
        String uri = command.getURI();
        String prefix = "/hazelcast/rest/cp-subsystem/groups/";
        String suffix = "/sessions/";
        int i = uri.indexOf(suffix);
        String groupName = uri.substring(prefix.length(), i).trim();
        long sessionId = Long.parseLong(uri.substring(i + suffix.length(), uri.indexOf(47, i + suffix.length())));
        this.getCpSubsystem().getCPSessionManagementService().forceCloseSession(groupName, sessionId).andThen(new ExecutionCallback<Boolean>(){

            @Override
            public void onResponse(Boolean response) {
                if (response.booleanValue()) {
                    command.send200();
                } else {
                    command.send400();
                }
                HttpPostCommandProcessor.this.textCommandService.sendResponse(command);
            }

            @Override
            public void onFailure(Throwable t) {
                HttpPostCommandProcessor.this.logger.warning("Error while closing CP session", t);
                command.send500();
                HttpPostCommandProcessor.this.textCommandService.sendResponse(command);
            }
        });
    }

    private void handleForceDestroyCPGroup(final HttpPostCommand command) {
        String prefix;
        String uri = command.getURI();
        final String groupName = uri.substring((prefix = "/hazelcast/rest/cp-subsystem/groups/").length(), uri.indexOf(47, prefix.length())).trim();
        if ("METADATA".equals(groupName)) {
            command.send400();
            this.textCommandService.sendResponse(command);
            return;
        }
        this.getCpSubsystem().getCPSubsystemManagementService().forceDestroyCPGroup(groupName).andThen(new ExecutionCallback<Void>(){

            @Override
            public void onResponse(Void response) {
                command.send200();
                HttpPostCommandProcessor.this.textCommandService.sendResponse(command);
            }

            @Override
            public void onFailure(Throwable t) {
                HttpPostCommandProcessor.this.logger.warning("Error while destroying CP group " + groupName, t);
                if (ExceptionUtil.peel(t) instanceof IllegalArgumentException) {
                    command.send400();
                } else {
                    command.send500();
                }
                HttpPostCommandProcessor.this.textCommandService.sendResponse(command);
            }
        });
    }

    private void handleResetAndInitCPSubsystem(final HttpPostCommand command) throws UnsupportedEncodingException {
        if (this.checkCredentials(command)) {
            this.getCpSubsystem().getCPSubsystemManagementService().restart().andThen(new ExecutionCallback<Void>(){

                @Override
                public void onResponse(Void response) {
                    command.send200();
                    HttpPostCommandProcessor.this.textCommandService.sendResponse(command);
                }

                @Override
                public void onFailure(Throwable t) {
                    HttpPostCommandProcessor.this.logger.warning("Error while resetting CP subsystem", t);
                    command.send500();
                    HttpPostCommandProcessor.this.textCommandService.sendResponse(command);
                }
            });
        } else {
            command.send403();
            this.textCommandService.sendResponse(command);
        }
    }

    private CPSubsystemManagementService getCpSubsystemManagementService() {
        return this.getCpSubsystem().getCPSubsystemManagementService();
    }

    private CPSubsystem getCpSubsystem() {
        return this.textCommandService.getNode().getNodeEngine().getHazelcastInstance().getCPSubsystem();
    }

    protected static String exceptionResponse(Throwable throwable) {
        return HttpPostCommandProcessor.response(ResponseType.FAIL, "message", throwable.getMessage());
    }

    protected static String response(ResponseType type, Object ... attributes) {
        StringBuilder builder = new StringBuilder("{");
        builder.append("\"status\":\"").append((Object)type).append("\"");
        if (attributes.length > 0) {
            int i = 0;
            while (i < attributes.length) {
                Object value;
                String key = attributes[i++].toString();
                if ((value = attributes[i++]) == null) continue;
                builder.append(String.format(",\"%s\":%s", key, JsonUtil.toJson(value)));
            }
        }
        return builder.append("}").toString();
    }

    private static String[] decodeParams(HttpPostCommand command, int paramCount) throws UnsupportedEncodingException {
        byte[] data = command.getData();
        String[] encoded = StringUtil.bytesToString(data).split("&");
        String[] decoded = new String[encoded.length];
        for (int i = 0; i < paramCount; ++i) {
            decoded[i] = URLDecoder.decode(encoded[i], "UTF-8");
        }
        return decoded;
    }

    private boolean checkCredentials(HttpPostCommand command) throws UnsupportedEncodingException {
        byte[] data = command.getData();
        if (data == null) {
            return false;
        }
        String[] strList = StringUtil.bytesToString(data).split("&", -1);
        return this.authenticate(command, strList[0], strList.length > 1 ? strList[1] : null);
    }

    protected boolean authenticate(HttpPostCommand command, String groupName, String pass) throws UnsupportedEncodingException {
        String decodedName = URLDecoder.decode(groupName, "UTF-8");
        SecurityContext securityContext = this.textCommandService.getNode().getNodeExtension().getSecurityContext();
        if (securityContext == null) {
            GroupConfig groupConfig = this.textCommandService.getNode().getConfig().getGroupConfig();
            if (pass != null && !pass.isEmpty()) {
                this.logger.fine("Password was provided but the Hazelcast Security is disabled.");
            }
            return groupConfig.getName().equals(decodedName);
        }
        if (pass == null) {
            this.logger.fine("Empty password is not allowed when the Hazelcast Security is enabled.");
            return false;
        }
        String decodedPass = URLDecoder.decode(pass, "UTF-8");
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(groupName, decodedPass);
        try {
            LoginContext lc = securityContext.createMemberLoginContext(credentials);
            lc.login();
        }
        catch (LoginException e) {
            return false;
        }
        return true;
    }

    protected void sendResponse(HttpPostCommand command, String value) {
        command.setResponse(HttpCommand.CONTENT_TYPE_JSON, StringUtil.stringToBytes(value));
        this.textCommandService.sendResponse(command);
    }

    @Override
    public void handleRejection(HttpPostCommand command) {
        this.handle(command);
    }

    private void handleSetLicense(HttpPostCommand command) {
        String res;
        int retryCount = 100;
        byte[] data = command.getData();
        try {
            String[] strList = StringUtil.bytesToString(data).split("&");
            if (this.authenticate(command, strList[0], strList.length > 1 ? strList[1] : null)) {
                final String licenseKey = strList.length > 2 ? URLDecoder.decode(strList[2], "UTF-8") : null;
                InvocationUtil.invokeOnStableClusterSerial(this.textCommandService.getNode().nodeEngine, (Supplier<? extends Operation>)new Supplier<Operation>(){

                    @Override
                    public Operation get() {
                        return new SetLicenseOperation(licenseKey);
                    }
                }, 100).get();
                res = this.responseOnSetLicenseSuccess();
            } else {
                res = HttpPostCommandProcessor.response(ResponseType.FORBIDDEN, new Object[0]);
            }
        }
        catch (Throwable throwable) {
            this.logger.warning("Error occurred while updating the license", throwable);
            res = HttpPostCommandProcessor.exceptionResponse(throwable);
        }
        command.setResponse(HttpCommand.CONTENT_TYPE_JSON, StringUtil.stringToBytes(res));
    }

    protected String responseOnSetLicenseSuccess() {
        return HttpPostCommandProcessor.response(ResponseType.SUCCESS, new Object[0]);
    }

    protected static enum ResponseType {
        SUCCESS,
        FAIL,
        FORBIDDEN;


        public String toString() {
            return super.toString().toLowerCase(StringUtil.LOCALE_INTERNAL);
        }
    }
}

