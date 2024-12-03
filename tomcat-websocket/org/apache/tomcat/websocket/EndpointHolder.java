/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.DeploymentException
 *  javax.websocket.Endpoint
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket;

import javax.naming.NamingException;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.ClientEndpointHolder;

public class EndpointHolder
implements ClientEndpointHolder {
    private static final StringManager sm = StringManager.getManager(EndpointHolder.class);
    private final Endpoint endpoint;

    public EndpointHolder(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String getClassName() {
        return this.endpoint.getClass().getName();
    }

    @Override
    public Endpoint getInstance(InstanceManager instanceManager) throws DeploymentException {
        if (instanceManager != null) {
            try {
                instanceManager.newInstance((Object)this.endpoint);
            }
            catch (ReflectiveOperationException | NamingException e) {
                throw new DeploymentException(sm.getString("clientEndpointHolder.instanceRegistrationFailed"), (Throwable)e);
            }
        }
        return this.endpoint;
    }
}

