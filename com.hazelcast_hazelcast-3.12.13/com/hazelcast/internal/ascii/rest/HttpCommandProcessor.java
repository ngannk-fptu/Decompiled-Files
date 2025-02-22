/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.rest;

import com.hazelcast.internal.ascii.AbstractTextCommandProcessor;
import com.hazelcast.internal.ascii.TextCommandService;

public abstract class HttpCommandProcessor<T>
extends AbstractTextCommandProcessor<T> {
    public static final String URI_MAPS = "/hazelcast/rest/maps/";
    public static final String URI_QUEUES = "/hazelcast/rest/queues/";
    public static final String URI_MANCENTER_BASE_URL = "/hazelcast/rest/mancenter";
    public static final String URI_MANCENTER_CHANGE_URL = "/hazelcast/rest/mancenter/changeurl";
    public static final String URI_UPDATE_PERMISSIONS = "/hazelcast/rest/mancenter/security/permissions";
    public static final String URI_HEALTH_URL = "/hazelcast/health";
    public static final String URI_HEALTH_READY = "/hazelcast/health/ready";
    public static final String URI_CLUSTER = "/hazelcast/rest/cluster";
    public static final String URI_CLUSTER_MANAGEMENT_BASE_URL = "/hazelcast/rest/management/cluster";
    public static final String URI_CLUSTER_STATE_URL = "/hazelcast/rest/management/cluster/state";
    public static final String URI_CHANGE_CLUSTER_STATE_URL = "/hazelcast/rest/management/cluster/changeState";
    public static final String URI_CLUSTER_VERSION_URL = "/hazelcast/rest/management/cluster/version";
    public static final String URI_SHUTDOWN_CLUSTER_URL = "/hazelcast/rest/management/cluster/clusterShutdown";
    public static final String URI_SHUTDOWN_NODE_CLUSTER_URL = "/hazelcast/rest/management/cluster/memberShutdown";
    public static final String URI_CLUSTER_NODES_URL = "/hazelcast/rest/management/cluster/nodes";
    public static final String URI_FORCESTART_CLUSTER_URL = "/hazelcast/rest/management/cluster/forceStart";
    public static final String URI_PARTIALSTART_CLUSTER_URL = "/hazelcast/rest/management/cluster/partialStart";
    public static final String URI_HOT_RESTART_BACKUP_CLUSTER_URL = "/hazelcast/rest/management/cluster/hotBackup";
    public static final String URI_HOT_RESTART_BACKUP_INTERRUPT_CLUSTER_URL = "/hazelcast/rest/management/cluster/hotBackupInterrupt";
    public static final String URI_WAN_SYNC_MAP = "/hazelcast/rest/mancenter/wan/sync/map";
    public static final String URI_WAN_SYNC_ALL_MAPS = "/hazelcast/rest/mancenter/wan/sync/allmaps";
    public static final String URI_MANCENTER_WAN_CLEAR_QUEUES = "/hazelcast/rest/mancenter/wan/clearWanQueues";
    public static final String URI_ADD_WAN_CONFIG = "/hazelcast/rest/mancenter/wan/addWanConfig";
    public static final String URI_WAN_PAUSE_PUBLISHER = "/hazelcast/rest/mancenter/wan/pausePublisher";
    public static final String URI_WAN_STOP_PUBLISHER = "/hazelcast/rest/mancenter/wan/stopPublisher";
    public static final String URI_WAN_RESUME_PUBLISHER = "/hazelcast/rest/mancenter/wan/resumePublisher";
    public static final String URI_WAN_CONSISTENCY_CHECK_MAP = "/hazelcast/rest/mancenter/wan/consistencyCheck/map";
    public static final String LEGACY_URI_WAN_SYNC_MAP = "/hazelcast/rest/wan/sync/map";
    public static final String LEGACY_URI_WAN_SYNC_ALL_MAPS = "/hazelcast/rest/wan/sync/allmaps";
    public static final String LEGACY_URI_MANCENTER_WAN_CLEAR_QUEUES = "/hazelcast/rest/mancenter/clearWanQueues";
    public static final String LEGACY_URI_ADD_WAN_CONFIG = "/hazelcast/rest/wan/addWanConfig";
    public static final String URI_LICENSE_INFO = "/hazelcast/rest/license";
    public static final String URI_CP_SUBSYSTEM_BASE_URL = "/hazelcast/rest/cp-subsystem";
    public static final String URI_RESTART_CP_SUBSYSTEM_URL = "/hazelcast/rest/cp-subsystem/restart";
    public static final String URI_CP_GROUPS_URL = "/hazelcast/rest/cp-subsystem/groups";
    public static final String URI_CP_SESSIONS_SUFFIX = "/sessions";
    public static final String URI_REMOVE_SUFFIX = "/remove";
    public static final String URI_CP_MEMBERS_URL = "/hazelcast/rest/cp-subsystem/members";
    public static final String URI_LOCAL_CP_MEMBER_URL = "/hazelcast/rest/cp-subsystem/members/local";

    protected HttpCommandProcessor(TextCommandService textCommandService) {
        super(textCommandService);
    }
}

