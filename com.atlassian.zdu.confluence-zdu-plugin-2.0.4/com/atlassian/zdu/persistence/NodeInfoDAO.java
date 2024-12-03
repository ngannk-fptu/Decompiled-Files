/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Accessor
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.Table
 */
package com.atlassian.zdu.persistence;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;

@Table(value="ZDU_CLUSTER_NODES")
@Preload
public interface NodeInfoDAO
extends Entity {
    public static final String NODE_ID = "NODE_ID";
    public static final String NAME = "NAME";
    public static final String IP_ADDRESS = "IP_ADDRESS";
    public static final String PORT_NUMBER = "PORT_NUMBER";

    @NotNull
    @Accessor(value="NODE_ID")
    public String getNodeId();

    @NotNull
    @Accessor(value="NAME")
    public String getName();

    @NotNull
    @Accessor(value="IP_ADDRESS")
    public String getIpAddress();

    @NotNull
    @Accessor(value="PORT_NUMBER")
    public int getPortNumber();
}

