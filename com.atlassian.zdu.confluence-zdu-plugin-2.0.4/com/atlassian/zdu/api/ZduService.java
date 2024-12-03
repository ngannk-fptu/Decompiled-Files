/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.zdu.api;

import com.atlassian.zdu.exception.InvalidStateTransitionException;
import com.atlassian.zdu.rest.dto.Cluster;
import com.atlassian.zdu.rest.dto.ClusterState;
import com.atlassian.zdu.rest.dto.ClusterStateResponse;
import com.atlassian.zdu.rest.dto.NodeInfoDTO;
import java.util.List;
import java.util.Optional;

public interface ZduService {
    public List<NodeInfoDTO> getNodes();

    public Optional<NodeInfoDTO> getNode(String var1);

    public ClusterState getState();

    public ClusterStateResponse getClusterStateResponse();

    public Cluster getCluster();

    public Cluster retryFinalization() throws InvalidStateTransitionException;

    public Cluster startZdu() throws InvalidStateTransitionException;

    public Cluster cancelZdu() throws InvalidStateTransitionException;

    public Cluster finalizeZdu() throws InvalidStateTransitionException;
}

