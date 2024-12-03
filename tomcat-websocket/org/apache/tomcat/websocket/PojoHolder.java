/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.ClientEndpointConfig
 *  javax.websocket.DeploymentException
 *  javax.websocket.Endpoint
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket;

import javax.naming.NamingException;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.ClientEndpointHolder;
import org.apache.tomcat.websocket.pojo.PojoEndpointClient;

public class PojoHolder
implements ClientEndpointHolder {
    private static final StringManager sm = StringManager.getManager(PojoHolder.class);
    private final Object pojo;
    private final ClientEndpointConfig clientEndpointConfig;

    public PojoHolder(Object pojo, ClientEndpointConfig clientEndpointConfig) {
        this.pojo = pojo;
        this.clientEndpointConfig = clientEndpointConfig;
    }

    @Override
    public String getClassName() {
        return this.pojo.getClass().getName();
    }

    @Override
    public Endpoint getInstance(InstanceManager instanceManager) throws DeploymentException {
        if (instanceManager != null) {
            try {
                instanceManager.newInstance(this.pojo);
            }
            catch (ReflectiveOperationException | NamingException e) {
                throw new DeploymentException(sm.getString("clientEndpointHolder.instanceRegistrationFailed"), (Throwable)e);
            }
        }
        return new PojoEndpointClient(this.pojo, this.clientEndpointConfig.getDecoders(), instanceManager);
    }
}

