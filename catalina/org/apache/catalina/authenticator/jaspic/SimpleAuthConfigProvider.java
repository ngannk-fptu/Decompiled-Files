/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.security.auth.message.AuthException
 *  javax.security.auth.message.config.AuthConfigFactory
 *  javax.security.auth.message.config.AuthConfigProvider
 *  javax.security.auth.message.config.ClientAuthConfig
 *  javax.security.auth.message.config.ServerAuthConfig
 */
package org.apache.catalina.authenticator.jaspic;

import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.ClientAuthConfig;
import javax.security.auth.message.config.ServerAuthConfig;
import org.apache.catalina.authenticator.jaspic.SimpleServerAuthConfig;

public class SimpleAuthConfigProvider
implements AuthConfigProvider {
    private final Map<String, String> properties;
    private volatile ServerAuthConfig serverAuthConfig;

    public SimpleAuthConfigProvider(Map<String, String> properties, AuthConfigFactory factory) {
        this.properties = properties;
        if (factory != null) {
            factory.registerConfigProvider((AuthConfigProvider)this, null, null, "Automatic registration");
        }
    }

    public ClientAuthConfig getClientAuthConfig(String layer, String appContext, CallbackHandler handler) throws AuthException {
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ServerAuthConfig getServerAuthConfig(String layer, String appContext, CallbackHandler handler) throws AuthException {
        ServerAuthConfig serverAuthConfig = this.serverAuthConfig;
        if (serverAuthConfig == null) {
            SimpleAuthConfigProvider simpleAuthConfigProvider = this;
            synchronized (simpleAuthConfigProvider) {
                if (this.serverAuthConfig == null) {
                    this.serverAuthConfig = this.createServerAuthConfig(layer, appContext, handler, this.properties);
                }
                serverAuthConfig = this.serverAuthConfig;
            }
        }
        return serverAuthConfig;
    }

    protected ServerAuthConfig createServerAuthConfig(String layer, String appContext, CallbackHandler handler, Map<String, String> properties) {
        return new SimpleServerAuthConfig(layer, appContext, handler, properties);
    }

    public void refresh() {
        ServerAuthConfig serverAuthConfig = this.serverAuthConfig;
        if (serverAuthConfig != null) {
            serverAuthConfig.refresh();
        }
    }
}

