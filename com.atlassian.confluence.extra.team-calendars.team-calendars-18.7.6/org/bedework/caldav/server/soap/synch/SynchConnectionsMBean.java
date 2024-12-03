/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server.soap.synch;

import org.bedework.caldav.server.soap.synch.SynchConnection;
import org.bedework.util.jmx.ConfBaseMBean;
import org.bedework.util.jmx.MBeanInfo;

public interface SynchConnectionsMBean
extends ConfBaseMBean {
    public static final String configName = "SynchConnections";
    public static final String serviceName = "org.bedework.caldav:service=SynchConnections";

    @MBeanInfo(value="Put/update a connection")
    public void setConnection(SynchConnection var1);

    @MBeanInfo(value="Get a connection")
    public SynchConnection getConnection(String var1);

    @MBeanInfo(value="get a connection by id")
    public SynchConnection getConnectionById(String var1);

    @MBeanInfo(value="List of connections")
    public String[] activeConnectionInfo();
}

