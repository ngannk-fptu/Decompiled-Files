/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.DeploymentException
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket.pojo;

import javax.websocket.DeploymentException;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.Util;

public class PojoPathParam {
    private static final StringManager sm = StringManager.getManager(PojoPathParam.class);
    private final Class<?> type;
    private final String name;

    public PojoPathParam(Class<?> type, String name) throws DeploymentException {
        if (name != null) {
            PojoPathParam.validateType(type);
        }
        this.type = type;
        this.name = name;
    }

    public Class<?> getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    private static void validateType(Class<?> type) throws DeploymentException {
        if (String.class == type) {
            return;
        }
        if (Util.isPrimitive(type)) {
            return;
        }
        throw new DeploymentException(sm.getString("pojoPathParam.wrongType", new Object[]{type.getName()}));
    }
}

