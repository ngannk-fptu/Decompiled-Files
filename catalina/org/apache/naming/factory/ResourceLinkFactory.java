/*
 * Decompiled with CFR 0.152.
 */
package org.apache.naming.factory;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.ResourceLinkRef;
import org.apache.naming.StringManager;

public class ResourceLinkFactory
implements ObjectFactory {
    protected static final StringManager sm = StringManager.getManager(ResourceLinkFactory.class);
    private static Context globalContext = null;
    private static Map<ClassLoader, Map<String, String>> globalResourceRegistrations = new ConcurrentHashMap<ClassLoader, Map<String, String>>();

    public static void setGlobalContext(Context newGlobalContext) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission(ResourceLinkFactory.class.getName() + ".setGlobalContext"));
        }
        globalContext = newGlobalContext;
    }

    public static void registerGlobalResourceAccess(Context globalContext, String localName, String globalName) {
        ResourceLinkFactory.validateGlobalContext(globalContext);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        globalResourceRegistrations.computeIfAbsent(cl, k -> new HashMap()).put(localName, globalName);
    }

    public static void deregisterGlobalResourceAccess(Context globalContext, String localName) {
        ResourceLinkFactory.validateGlobalContext(globalContext);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Map<String, String> registrations = globalResourceRegistrations.get(cl);
        if (registrations != null) {
            registrations.remove(localName);
        }
    }

    public static void deregisterGlobalResourceAccess(Context globalContext) {
        ResourceLinkFactory.validateGlobalContext(globalContext);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        globalResourceRegistrations.remove(cl);
    }

    private static void validateGlobalContext(Context globalContext) {
        if (ResourceLinkFactory.globalContext != null && ResourceLinkFactory.globalContext != globalContext) {
            throw new SecurityException(sm.getString("resourceLinkFactory.invalidGlobalContext"));
        }
    }

    private static boolean validateGlobalResourceAccess(String globalName) {
        for (ClassLoader cl = Thread.currentThread().getContextClassLoader(); cl != null; cl = cl.getParent()) {
            Map<String, String> registrations = globalResourceRegistrations.get(cl);
            if (registrations == null || !registrations.containsValue(globalName)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws NamingException {
        if (!(obj instanceof ResourceLinkRef)) {
            return null;
        }
        Reference ref = (Reference)obj;
        String globalName = null;
        RefAddr refAddr = ref.get("globalName");
        if (refAddr != null) {
            globalName = refAddr.getContent().toString();
            if (!ResourceLinkFactory.validateGlobalResourceAccess(globalName)) {
                return null;
            }
            Object result = null;
            result = globalContext.lookup(globalName);
            String expectedClassName = ref.getClassName();
            if (expectedClassName == null) {
                throw new IllegalArgumentException(sm.getString("resourceLinkFactory.nullType", name, globalName));
            }
            try {
                Class<?> expectedClazz = Class.forName(expectedClassName, true, Thread.currentThread().getContextClassLoader());
                if (!expectedClazz.isAssignableFrom(result.getClass())) {
                    throw new IllegalArgumentException(sm.getString("resourceLinkFactory.wrongType", name, globalName, expectedClassName, result.getClass().getName()));
                }
            }
            catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(sm.getString("resourceLinkFactory.unknownType", name, globalName, expectedClassName), e);
            }
            return result;
        }
        return null;
    }
}

