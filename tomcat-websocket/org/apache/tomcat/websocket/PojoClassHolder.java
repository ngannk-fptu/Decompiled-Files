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

public class PojoClassHolder
implements ClientEndpointHolder {
    private static final StringManager sm = StringManager.getManager(PojoClassHolder.class);
    private final Class<?> pojoClazz;
    private final ClientEndpointConfig clientEndpointConfig;

    public PojoClassHolder(Class<?> pojoClazz, ClientEndpointConfig clientEndpointConfig) {
        this.pojoClazz = pojoClazz;
        this.clientEndpointConfig = clientEndpointConfig;
    }

    @Override
    public String getClassName() {
        return this.pojoClazz.getName();
    }

    @Override
    public Endpoint getInstance(InstanceManager instanceManager) throws DeploymentException {
        try {
            Object pojo = instanceManager == null ? this.pojoClazz.getConstructor(new Class[0]).newInstance(new Object[0]) : instanceManager.newInstance(this.pojoClazz);
            return new PojoEndpointClient(pojo, this.clientEndpointConfig.getDecoders(), instanceManager);
        }
        catch (ReflectiveOperationException | SecurityException | NamingException e) {
            throw new DeploymentException(sm.getString("clientEndpointHolder.instanceCreationFailed"), (Throwable)e);
        }
    }
}

