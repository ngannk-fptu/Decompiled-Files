/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.security.auth.message.AuthException
 *  javax.security.auth.message.MessageInfo
 *  javax.security.auth.message.config.ServerAuthConfig
 *  javax.security.auth.message.config.ServerAuthContext
 *  javax.security.auth.message.module.ServerAuthModule
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.authenticator.jaspic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.config.ServerAuthConfig;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.module.ServerAuthModule;
import org.apache.catalina.authenticator.jaspic.SimpleServerAuthContext;
import org.apache.tomcat.util.res.StringManager;

public class SimpleServerAuthConfig
implements ServerAuthConfig {
    private static StringManager sm = StringManager.getManager(SimpleServerAuthConfig.class);
    private static final String SERVER_AUTH_MODULE_KEY_PREFIX = "org.apache.catalina.authenticator.jaspic.ServerAuthModule.";
    private final String layer;
    private final String appContext;
    private final CallbackHandler handler;
    private final Map<String, String> properties;
    private volatile ServerAuthContext serverAuthContext;

    public SimpleServerAuthConfig(String layer, String appContext, CallbackHandler handler, Map<String, String> properties) {
        this.layer = layer;
        this.appContext = appContext;
        this.handler = handler;
        this.properties = properties;
    }

    public String getMessageLayer() {
        return this.layer;
    }

    public String getAppContext() {
        return this.appContext;
    }

    public String getAuthContextID(MessageInfo messageInfo) {
        return messageInfo.toString();
    }

    public void refresh() {
        this.serverAuthContext = null;
    }

    public boolean isProtected() {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ServerAuthContext getAuthContext(String authContextID, Subject serviceSubject, Map properties) throws AuthException {
        ServerAuthContext serverAuthContext = this.serverAuthContext;
        if (serverAuthContext == null) {
            SimpleServerAuthConfig simpleServerAuthConfig = this;
            synchronized (simpleServerAuthConfig) {
                if (this.serverAuthContext == null) {
                    HashMap<String, String> mergedProperties = new HashMap<String, String>();
                    if (this.properties != null) {
                        mergedProperties.putAll(this.properties);
                    }
                    if (properties != null) {
                        mergedProperties.putAll(properties);
                    }
                    ArrayList<ServerAuthModule> modules = new ArrayList<ServerAuthModule>();
                    int moduleIndex = 1;
                    String key = SERVER_AUTH_MODULE_KEY_PREFIX + moduleIndex;
                    String moduleClassName = (String)mergedProperties.get(key);
                    while (moduleClassName != null) {
                        try {
                            Class<?> clazz = Class.forName(moduleClassName);
                            ServerAuthModule module = (ServerAuthModule)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                            module.initialize(null, null, this.handler, mergedProperties);
                            modules.add(module);
                        }
                        catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
                            AuthException ae = new AuthException();
                            ae.initCause((Throwable)e);
                            throw ae;
                        }
                        key = SERVER_AUTH_MODULE_KEY_PREFIX + ++moduleIndex;
                        moduleClassName = (String)mergedProperties.get(key);
                    }
                    if (modules.size() == 0) {
                        throw new AuthException(sm.getString("simpleServerAuthConfig.noModules"));
                    }
                    this.serverAuthContext = this.createServerAuthContext(modules);
                }
                serverAuthContext = this.serverAuthContext;
            }
        }
        return serverAuthContext;
    }

    protected ServerAuthContext createServerAuthContext(List<ServerAuthModule> modules) {
        return new SimpleServerAuthContext(modules);
    }
}

