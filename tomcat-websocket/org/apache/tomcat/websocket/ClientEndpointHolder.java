/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.DeploymentException
 *  javax.websocket.Endpoint
 *  org.apache.tomcat.InstanceManager
 */
package org.apache.tomcat.websocket;

import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import org.apache.tomcat.InstanceManager;

public interface ClientEndpointHolder {
    public String getClassName();

    public Endpoint getInstance(InstanceManager var1) throws DeploymentException;
}

