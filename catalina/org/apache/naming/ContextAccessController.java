/*
 * Decompiled with CFR 0.152.
 */
package org.apache.naming;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContextAccessController {
    private static final Map<Object, Object> readOnlyContexts = new ConcurrentHashMap<Object, Object>();
    private static final Map<Object, Object> securityTokens = new ConcurrentHashMap<Object, Object>();

    public static void setSecurityToken(Object name, Object token) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission(ContextAccessController.class.getName() + ".setSecurityToken"));
        }
        if (!securityTokens.containsKey(name) && token != null) {
            securityTokens.put(name, token);
        }
    }

    public static void unsetSecurityToken(Object name, Object token) {
        if (ContextAccessController.checkSecurityToken(name, token)) {
            securityTokens.remove(name);
        }
    }

    public static boolean checkSecurityToken(Object name, Object token) {
        Object refToken = securityTokens.get(name);
        return refToken == null || refToken.equals(token);
    }

    public static void setWritable(Object name, Object token) {
        if (ContextAccessController.checkSecurityToken(name, token)) {
            readOnlyContexts.remove(name);
        }
    }

    public static void setReadOnly(Object name) {
        readOnlyContexts.put(name, name);
    }

    public static boolean isWritable(Object name) {
        return !readOnlyContexts.containsKey(name);
    }
}

