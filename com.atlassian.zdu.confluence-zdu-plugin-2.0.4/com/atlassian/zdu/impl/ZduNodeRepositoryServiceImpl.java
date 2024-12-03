/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 */
package com.atlassian.zdu.impl;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.zdu.internal.api.NodeInfo;
import com.atlassian.zdu.internal.api.ZduNodeRepositoryService;
import com.atlassian.zdu.persistence.ZduNodeRepository;
import com.atlassian.zdu.rest.dto.NodeInfoDTO;
import java.util.List;
import java.util.stream.Collectors;

@VisibleForTesting
public class ZduNodeRepositoryServiceImpl
implements ZduNodeRepositoryService {
    private final ZduNodeRepository zduNodeRepository;

    public ZduNodeRepositoryServiceImpl(ZduNodeRepository repository) {
        this.zduNodeRepository = repository;
    }

    @Override
    public void removeAllNodeInfo() {
        this.zduNodeRepository.cleanAll();
    }

    @Override
    public void removeNodeInfo(String nodeId) {
        List<NodeInfoDTO> nodes = this.zduNodeRepository.get().stream().filter(n -> !n.getId().equals(nodeId)).collect(Collectors.toList());
        this.zduNodeRepository.cleanAll();
        this.zduNodeRepository.put(nodes);
    }

    @Override
    public void addNodeInfo(NodeInfo node) {
        List<NodeInfoDTO> nodes = this.zduNodeRepository.get();
        nodes.add(NodeInfoDTO.builder(node).build());
        this.zduNodeRepository.put(nodes);
    }
}

