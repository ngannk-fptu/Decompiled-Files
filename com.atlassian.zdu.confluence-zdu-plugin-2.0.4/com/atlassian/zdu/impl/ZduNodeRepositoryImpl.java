/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  net.java.ao.ActiveObjectsException
 *  net.java.ao.DBParam
 *  net.java.ao.RawEntity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.zdu.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.zdu.persistence.NodeInfoDAO;
import com.atlassian.zdu.persistence.ZduNodeRepository;
import com.atlassian.zdu.rest.dto.NodeInfoDTO;
import com.atlassian.zdu.rest.dto.NodeState;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.java.ao.ActiveObjectsException;
import net.java.ao.DBParam;
import net.java.ao.RawEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZduNodeRepositoryImpl
implements ZduNodeRepository {
    private static final Logger LOG = LoggerFactory.getLogger(ZduNodeRepositoryImpl.class);
    private final ActiveObjects ao;
    private Function<NodeInfoDAO, NodeInfoDTO> aoToClusterNodeConverter = node -> NodeInfoDTO.builder().id(node.getNodeId()).name(node.getName()).ipAddress(node.getIpAddress()).portNumber(node.getPortNumber()).state(NodeState.OFFLINE).build();

    public ZduNodeRepositoryImpl(ActiveObjects ao) {
        this.ao = Objects.requireNonNull(ao);
    }

    @Override
    public void put(List<NodeInfoDTO> nodes) {
        for (NodeInfoDTO node : nodes) {
            this.saveNode(node);
        }
    }

    private void saveNode(NodeInfoDTO node) {
        try {
            NodeInfoDAO nodeEntity = (NodeInfoDAO)this.ao.create(NodeInfoDAO.class, new DBParam[]{new DBParam("NODE_ID", (Object)node.getId()), new DBParam("NAME", (Object)node.getName()), new DBParam("IP_ADDRESS", (Object)node.getIpAddress()), new DBParam("PORT_NUMBER", (Object)node.getPortNumber())});
            nodeEntity.save();
        }
        catch (ActiveObjectsException e) {
            LOG.error("There was a problem persisting Cluster Node, {}, into the database", (Object)node.getId());
            LOG.error("Stacktrace for failure:", (Throwable)e);
        }
    }

    @Override
    public List<NodeInfoDTO> get() {
        return Arrays.stream(this.ao.find(NodeInfoDAO.class)).map(this.aoToClusterNodeConverter).collect(Collectors.toList());
    }

    @Override
    public void cleanAll() {
        NodeInfoDAO[] entities;
        for (NodeInfoDAO entity : entities = (NodeInfoDAO[])this.ao.find(NodeInfoDAO.class)) {
            this.ao.delete(new RawEntity[]{entity});
        }
    }
}

