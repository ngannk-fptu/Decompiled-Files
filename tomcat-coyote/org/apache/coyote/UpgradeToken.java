/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpUpgradeHandler
 *  org.apache.tomcat.ContextBind
 *  org.apache.tomcat.InstanceManager
 */
package org.apache.coyote;

import javax.servlet.http.HttpUpgradeHandler;
import org.apache.tomcat.ContextBind;
import org.apache.tomcat.InstanceManager;

public final class UpgradeToken {
    private final ContextBind contextBind;
    private final HttpUpgradeHandler httpUpgradeHandler;
    private final InstanceManager instanceManager;
    private final String protocol;

    public UpgradeToken(HttpUpgradeHandler httpUpgradeHandler, ContextBind contextBind, InstanceManager instanceManager, String protocol) {
        this.contextBind = contextBind;
        this.httpUpgradeHandler = httpUpgradeHandler;
        this.instanceManager = instanceManager;
        this.protocol = protocol;
    }

    public ContextBind getContextBind() {
        return this.contextBind;
    }

    public HttpUpgradeHandler getHttpUpgradeHandler() {
        return this.httpUpgradeHandler;
    }

    public InstanceManager getInstanceManager() {
        return this.instanceManager;
    }

    public String getProtocol() {
        return this.protocol;
    }
}

