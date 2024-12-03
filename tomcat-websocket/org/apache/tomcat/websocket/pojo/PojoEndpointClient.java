/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.Decoder
 *  javax.websocket.DeploymentException
 *  javax.websocket.EndpointConfig
 *  javax.websocket.Session
 *  org.apache.tomcat.InstanceManager
 */
package org.apache.tomcat.websocket.pojo;

import java.util.Collections;
import java.util.List;
import javax.websocket.Decoder;
import javax.websocket.DeploymentException;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.websocket.pojo.PojoEndpointBase;
import org.apache.tomcat.websocket.pojo.PojoMethodMapping;

public class PojoEndpointClient
extends PojoEndpointBase {
    @Deprecated
    public PojoEndpointClient(Object pojo, List<Class<? extends Decoder>> decoders) throws DeploymentException {
        super(Collections.emptyMap());
        this.setPojo(pojo);
        this.setMethodMapping(new PojoMethodMapping(pojo.getClass(), decoders, null));
    }

    public PojoEndpointClient(Object pojo, List<Class<? extends Decoder>> decoders, InstanceManager instanceManager) throws DeploymentException {
        super(Collections.emptyMap());
        this.setPojo(pojo);
        this.setMethodMapping(new PojoMethodMapping(pojo.getClass(), decoders, null, instanceManager));
    }

    public void onOpen(Session session, EndpointConfig config) {
        this.doOnOpen(session, config);
    }
}

