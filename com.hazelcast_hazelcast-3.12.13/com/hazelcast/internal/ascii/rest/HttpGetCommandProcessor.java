/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.rest;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.cp.CPGroup;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.CPMember;
import com.hazelcast.cp.CPSubsystem;
import com.hazelcast.cp.CPSubsystemManagementService;
import com.hazelcast.cp.session.CPSession;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeState;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.ascii.rest.HttpCommand;
import com.hazelcast.internal.ascii.rest.HttpCommandProcessor;
import com.hazelcast.internal.ascii.rest.HttpGetCommand;
import com.hazelcast.internal.ascii.rest.RestValue;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.AggregateEndpointManager;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.nio.NetworkingService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.StringUtil;
import java.util.Collection;

public class HttpGetCommandProcessor
extends HttpCommandProcessor<HttpGetCommand> {
    public static final String QUEUE_SIZE_COMMAND = "size";
    private static final String HEALTH_PATH_PARAM_NODE_STATE = "/node-state";
    private static final String HEALTH_PATH_PARAM_CLUSTER_STATE = "/cluster-state";
    private static final String HEALTH_PATH_PARAM_CLUSTER_SAFE = "/cluster-safe";
    private static final String HEALTH_PATH_PARAM_MIGRATION_QUEUE_SIZE = "/migration-queue-size";
    private static final String HEALTH_PATH_PARAM_CLUSTER_SIZE = "/cluster-size";

    public HttpGetCommandProcessor(TextCommandService textCommandService) {
        super(textCommandService);
    }

    @Override
    public void handle(HttpGetCommand command) {
        boolean sendResponse = true;
        try {
            String uri = command.getURI();
            if (uri.startsWith("/hazelcast/rest/maps/")) {
                this.handleMap(command, uri);
            } else if (uri.startsWith("/hazelcast/rest/queues/")) {
                this.handleQueue(command, uri);
            } else if (uri.startsWith("/hazelcast/rest/cluster")) {
                this.handleCluster(command);
            } else if (uri.startsWith("/hazelcast/health/ready")) {
                this.handleHealthReady(command);
            } else if (uri.startsWith("/hazelcast/health")) {
                this.handleHealthcheck(command, uri);
            } else if (uri.startsWith("/hazelcast/rest/management/cluster/version")) {
                this.handleGetClusterVersion(command);
            } else if (uri.startsWith("/hazelcast/rest/license")) {
                this.handleLicense(command);
            } else if (uri.startsWith("/hazelcast/rest/cp-subsystem/groups")) {
                this.handleCPGroupRequest(command);
                sendResponse = false;
            } else if (uri.startsWith("/hazelcast/rest/cp-subsystem/members/local")) {
                this.handleGetLocalCPMember(command);
            } else if (uri.startsWith("/hazelcast/rest/cp-subsystem/members")) {
                this.handleGetCPMembers(command);
                sendResponse = false;
            } else {
                command.send404();
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

    private void handleHealthReady(HttpGetCommand command) {
        Node node = this.textCommandService.getNode();
        if (node.isRunning() && node.getNodeExtension().isStartCompleted()) {
            command.setResponse(HttpCommand.RES_200_WITH_NO_CONTENT);
        } else {
            command.setResponse(HttpCommand.RES_503);
        }
    }

    private void handleHealthcheck(HttpGetCommand command, String uri) {
        Node node = this.textCommandService.getNode();
        NodeState nodeState = node.getState();
        ClusterServiceImpl clusterService = node.getClusterService();
        ClusterState clusterState = clusterService.getClusterState();
        int clusterSize = clusterService.getMembers().size();
        InternalPartitionService partitionService = node.getPartitionService();
        boolean memberStateSafe = partitionService.isMemberStateSafe();
        boolean clusterSafe = memberStateSafe && !partitionService.hasOnGoingMigration();
        long migrationQueueSize = partitionService.getMigrationQueueSize();
        String healthParameter = uri.substring("/hazelcast/health".length());
        if (healthParameter.equals(HEALTH_PATH_PARAM_NODE_STATE)) {
            if (NodeState.SHUT_DOWN.equals((Object)nodeState)) {
                command.setResponse(HttpCommand.RES_503);
            } else {
                command.setResponse(null, StringUtil.stringToBytes(nodeState.toString()));
            }
        } else if (healthParameter.equals(HEALTH_PATH_PARAM_CLUSTER_STATE)) {
            command.setResponse(null, StringUtil.stringToBytes(clusterState.toString()));
        } else if (healthParameter.equals(HEALTH_PATH_PARAM_CLUSTER_SAFE)) {
            if (clusterSafe) {
                command.send200();
            } else {
                command.setResponse(HttpCommand.RES_503);
            }
        } else if (healthParameter.equals(HEALTH_PATH_PARAM_MIGRATION_QUEUE_SIZE)) {
            command.setResponse(null, StringUtil.stringToBytes(Long.toString(migrationQueueSize)));
        } else if (healthParameter.equals(HEALTH_PATH_PARAM_CLUSTER_SIZE)) {
            command.setResponse(null, StringUtil.stringToBytes(Integer.toString(clusterSize)));
        } else if (healthParameter.isEmpty()) {
            StringBuilder res = new StringBuilder();
            res.append("Hazelcast::NodeState=").append((Object)nodeState).append("\n");
            res.append("Hazelcast::ClusterState=").append((Object)clusterState).append("\n");
            res.append("Hazelcast::ClusterSafe=").append(HttpGetCommandProcessor.booleanToString(clusterSafe)).append("\n");
            res.append("Hazelcast::MigrationQueueSize=").append(migrationQueueSize).append("\n");
            res.append("Hazelcast::ClusterSize=").append(clusterSize).append("\n");
            command.setResponse(TextCommandConstants.MIME_TEXT_PLAIN, StringUtil.stringToBytes(res.toString()));
        } else {
            command.send400();
        }
    }

    private static String booleanToString(boolean b) {
        return Boolean.toString(b).toUpperCase(StringUtil.LOCALE_INTERNAL);
    }

    private void handleGetClusterVersion(HttpGetCommand command) {
        String res = "{\"status\":\"${STATUS}\",\"version\":\"${VERSION}\"}";
        Node node = this.textCommandService.getNode();
        ClusterServiceImpl clusterService = node.getClusterService();
        res = res.replace("${STATUS}", "success");
        res = res.replace("${VERSION}", clusterService.getClusterVersion().toString());
        command.setResponse(HttpCommand.CONTENT_TYPE_JSON, StringUtil.stringToBytes(res));
    }

    private void handleCPGroupRequest(HttpGetCommand command) {
        String uri = command.getURI();
        if (uri.contains("/sessions")) {
            this.handleGetCPSessions(command);
        } else if (uri.endsWith("/hazelcast/rest/cp-subsystem/groups") || uri.endsWith("/hazelcast/rest/cp-subsystem/groups/")) {
            this.handleGetCPGroupIds(command);
        } else {
            this.handleGetCPGroupByName(command);
        }
    }

    private void handleGetCPGroupIds(final HttpGetCommand command) {
        ICompletableFuture<Collection<CPGroupId>> f = this.getCpSubsystemManagementService().getCPGroupIds();
        f.andThen(new ExecutionCallback<Collection<CPGroupId>>(){

            @Override
            public void onResponse(Collection<CPGroupId> groupIds) {
                JsonArray arr = new JsonArray();
                for (CPGroupId groupId : groupIds) {
                    arr.add(HttpGetCommandProcessor.this.toJson(groupId));
                }
                command.setResponse(HttpCommand.CONTENT_TYPE_JSON, StringUtil.stringToBytes(arr.toString()));
                HttpGetCommandProcessor.this.textCommandService.sendResponse(command);
            }

            @Override
            public void onFailure(Throwable t) {
                command.send500();
                HttpGetCommandProcessor.this.textCommandService.sendResponse(command);
            }
        });
    }

    private void handleGetCPSessions(final HttpGetCommand command) {
        String uri = command.getURI();
        String prefix = "/hazelcast/rest/cp-subsystem/groups/";
        int i = uri.indexOf("/sessions");
        String groupName = uri.substring(prefix.length(), i).trim();
        this.getCpSubsystem().getCPSessionManagementService().getAllSessions(groupName).andThen(new ExecutionCallback<Collection<CPSession>>(){

            @Override
            public void onResponse(Collection<CPSession> sessions) {
                JsonArray sessionsArr = new JsonArray();
                for (CPSession session : sessions) {
                    sessionsArr.add(HttpGetCommandProcessor.this.toJson(session));
                }
                command.setResponse(HttpCommand.CONTENT_TYPE_JSON, StringUtil.stringToBytes(sessionsArr.toString()));
                HttpGetCommandProcessor.this.textCommandService.sendResponse(command);
            }

            @Override
            public void onFailure(Throwable t) {
                if (ExceptionUtil.peel(t) instanceof IllegalArgumentException) {
                    command.send404();
                } else {
                    command.send500();
                }
                HttpGetCommandProcessor.this.textCommandService.sendResponse(command);
            }
        });
    }

    private void handleGetCPGroupByName(final HttpGetCommand command) {
        String prefix = "/hazelcast/rest/cp-subsystem/groups/";
        String groupName = command.getURI().substring(prefix.length()).trim();
        ICompletableFuture<CPGroup> f = this.getCpSubsystemManagementService().getCPGroup(groupName);
        f.andThen(new ExecutionCallback<CPGroup>(){

            @Override
            public void onResponse(CPGroup group) {
                if (group != null) {
                    JsonObject json = new JsonObject();
                    json.add("id", HttpGetCommandProcessor.this.toJson(group.id())).add("status", group.status().name());
                    JsonArray membersArr = new JsonArray();
                    for (CPMember member : group.members()) {
                        membersArr.add(HttpGetCommandProcessor.this.toJson(member));
                    }
                    json.add("members", membersArr);
                    command.setResponse(HttpCommand.CONTENT_TYPE_JSON, StringUtil.stringToBytes(json.toString()));
                } else {
                    command.send404();
                }
                HttpGetCommandProcessor.this.textCommandService.sendResponse(command);
            }

            @Override
            public void onFailure(Throwable t) {
                command.send500();
                HttpGetCommandProcessor.this.textCommandService.sendResponse(command);
            }
        });
    }

    private void handleGetCPMembers(final HttpGetCommand command) {
        ICompletableFuture<Collection<CPMember>> f = this.getCpSubsystemManagementService().getCPMembers();
        f.andThen(new ExecutionCallback<Collection<CPMember>>(){

            @Override
            public void onResponse(Collection<CPMember> cpMembers) {
                JsonArray arr = new JsonArray();
                for (CPMember cpMember : cpMembers) {
                    arr.add(HttpGetCommandProcessor.this.toJson(cpMember));
                }
                command.setResponse(HttpCommand.CONTENT_TYPE_JSON, StringUtil.stringToBytes(arr.toString()));
                HttpGetCommandProcessor.this.textCommandService.sendResponse(command);
            }

            @Override
            public void onFailure(Throwable t) {
                command.send500();
                HttpGetCommandProcessor.this.textCommandService.sendResponse(command);
            }
        });
    }

    private void handleGetLocalCPMember(HttpGetCommand command) {
        CPMember localCPMember = this.getCpSubsystem().getLocalCPMember();
        if (localCPMember != null) {
            command.setResponse(HttpCommand.CONTENT_TYPE_JSON, StringUtil.stringToBytes(this.toJson(localCPMember).toString()));
        } else {
            command.send404();
        }
    }

    private CPSubsystemManagementService getCpSubsystemManagementService() {
        return this.getCpSubsystem().getCPSubsystemManagementService();
    }

    private CPSubsystem getCpSubsystem() {
        return this.textCommandService.getNode().getNodeEngine().getHazelcastInstance().getCPSubsystem();
    }

    private JsonObject toJson(CPGroupId groupId) {
        return new JsonObject().add("name", groupId.name()).add("id", groupId.id());
    }

    private JsonObject toJson(CPMember cpMember) {
        Address address = cpMember.getAddress();
        return new JsonObject().add("uuid", cpMember.getUuid()).add("address", "[" + address.getHost() + "]:" + address.getPort());
    }

    private JsonObject toJson(CPSession cpSession) {
        Address address = cpSession.endpoint();
        return new JsonObject().add("id", cpSession.id()).add("creationTime", cpSession.creationTime()).add("expirationTime", cpSession.expirationTime()).add("version", cpSession.version()).add("endpoint", "[" + address.getHost() + "]:" + address.getPort()).add("endpointType", cpSession.endpointType().name()).add("endpointName", cpSession.endpointName());
    }

    private void handleCluster(HttpGetCommand command) {
        Node node = this.textCommandService.getNode();
        StringBuilder res = new StringBuilder(node.getClusterService().getMemberListString());
        res.append("\n");
        NetworkingService ns = node.getNetworkingService();
        EndpointManager cem = ns.getEndpointManager(EndpointQualifier.CLIENT);
        AggregateEndpointManager aem = ns.getAggregateEndpointManager();
        res.append("ConnectionCount: ").append(cem == null ? "0" : Integer.valueOf(cem.getActiveConnections().size()));
        res.append("\n");
        res.append("AllConnectionCount: ").append(aem.getActiveConnections().size());
        res.append("\n");
        command.setResponse(null, StringUtil.stringToBytes(res.toString()));
    }

    private void handleQueue(HttpGetCommand command, String uri) {
        String secondStr;
        int indexEnd = uri.indexOf(47, "/hazelcast/rest/queues/".length());
        String queueName = uri.substring("/hazelcast/rest/queues/".length(), indexEnd);
        String string = secondStr = uri.length() > indexEnd + 1 ? uri.substring(indexEnd + 1) : null;
        if (QUEUE_SIZE_COMMAND.equalsIgnoreCase(secondStr)) {
            int size = this.textCommandService.size(queueName);
            this.prepareResponse(command, Integer.toString(size));
        } else {
            int seconds = secondStr == null ? 0 : Integer.parseInt(secondStr);
            Object value = this.textCommandService.poll(queueName, seconds);
            this.prepareResponse(command, value);
        }
    }

    private void handleMap(HttpGetCommand command, String uri) {
        int indexEnd = uri.indexOf(47, "/hazelcast/rest/maps/".length());
        String mapName = uri.substring("/hazelcast/rest/maps/".length(), indexEnd);
        String key = uri.substring(indexEnd + 1);
        Object value = this.textCommandService.get(mapName, key);
        this.prepareResponse(command, value);
    }

    @Override
    public void handleRejection(HttpGetCommand command) {
        this.handle(command);
    }

    private void prepareResponse(HttpGetCommand command, Object value) {
        if (value == null) {
            command.send204();
        } else if (value instanceof byte[]) {
            command.setResponse(HttpCommand.CONTENT_TYPE_BINARY, (byte[])value);
        } else if (value instanceof RestValue) {
            RestValue restValue = (RestValue)value;
            command.setResponse(restValue.getContentType(), restValue.getValue());
        } else if (value instanceof String) {
            command.setResponse(HttpCommand.CONTENT_TYPE_PLAIN_TEXT, StringUtil.stringToBytes((String)value));
        } else {
            command.setResponse(HttpCommand.CONTENT_TYPE_BINARY, this.textCommandService.toByteArray(value));
        }
    }

    protected void handleLicense(HttpGetCommand command) {
        command.send404();
    }
}

