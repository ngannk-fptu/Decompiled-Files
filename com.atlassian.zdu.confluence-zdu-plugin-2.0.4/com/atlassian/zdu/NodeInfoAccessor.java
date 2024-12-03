/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.zdu;

import com.atlassian.zdu.internal.api.ClusterManagerAdapter;
import com.atlassian.zdu.persistence.ZduNodeRepository;
import com.atlassian.zdu.rest.dto.NodeInfoDTO;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class NodeInfoAccessor {
    private final ClusterManagerAdapter clusterManagerAdapter;
    private final ZduNodeRepository persistenceService;

    public NodeInfoAccessor(@Nonnull ClusterManagerAdapter clusterManagerAdapter, @Nonnull ZduNodeRepository persistenceService) {
        this.clusterManagerAdapter = Objects.requireNonNull(clusterManagerAdapter);
        this.persistenceService = Objects.requireNonNull(persistenceService);
    }

    public List<NodeInfoDTO> getNodes() {
        List<NodeInfoDTO> previousNodes = this.persistenceService.get();
        List<NodeInfoDTO> currentNodes = this.clusterManagerAdapter.getNodes().stream().map(n -> NodeInfoDTO.builder(n).build()).collect(Collectors.toList());
        return this.addOfflineNodes(currentNodes, previousNodes);
    }

    public void snapshotNodes() {
        List<NodeInfoDTO> currentNodes = this.clusterManagerAdapter.getNodes().stream().map(n -> NodeInfoDTO.builder(n).build()).collect(Collectors.toList());
        this.persistenceService.cleanAll();
        this.persistenceService.put(currentNodes);
    }

    private List<NodeInfoDTO> addOfflineNodes(List<NodeInfoDTO> currentNodes, List<NodeInfoDTO> previousNodes) {
        return Stream.concat(currentNodes.stream(), previousNodes.stream()).distinct().collect(Collectors.toList());
    }
}

