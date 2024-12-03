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

public class EndpointClassHolder
implements ClientEndpointHolder {
    private static final StringManager sm = StringManager.getManager(EndpointClassHolder.class);
    private final Class<? extends Endpoint> clazz;

    public EndpointClassHolder(Class<? extends Endpoint> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getClassName() {
        return this.clazz.getName();
    }

    @Override
    public Endpoint getInstance(InstanceManager instanceManager) throws DeploymentException {
        try {
            if (instanceManager == null) {
                return this.clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            return (Endpoint)instanceManager.newInstance(this.clazz);
        }
        catch (ReflectiveOperationException | NamingException e) {
            throw new DeploymentException(sm.getString("clientEndpointHolder.instanceCreationFailed"), (Throwable)e);
        }
    }
}

