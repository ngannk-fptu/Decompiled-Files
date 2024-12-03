/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import org.apache.catalina.Engine;
import org.apache.catalina.Executor;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.Server;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.mapper.Mapper;

public interface Service
extends Lifecycle {
    public Engine getContainer();

    public void setContainer(Engine var1);

    public String getName();

    public void setName(String var1);

    public Server getServer();

    public void setServer(Server var1);

    public ClassLoader getParentClassLoader();

    public void setParentClassLoader(ClassLoader var1);

    public String getDomain();

    public void addConnector(Connector var1);

    public Connector[] findConnectors();

    public void removeConnector(Connector var1);

    public void addExecutor(Executor var1);

    public Executor[] findExecutors();

    public Executor getExecutor(String var1);

    public void removeExecutor(Executor var1);

    public Mapper getMapper();
}

