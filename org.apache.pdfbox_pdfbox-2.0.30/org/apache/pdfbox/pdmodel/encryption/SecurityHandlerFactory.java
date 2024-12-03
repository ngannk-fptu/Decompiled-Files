/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.encryption;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.pdmodel.encryption.ProtectionPolicy;
import org.apache.pdfbox.pdmodel.encryption.PublicKeyProtectionPolicy;
import org.apache.pdfbox.pdmodel.encryption.PublicKeySecurityHandler;
import org.apache.pdfbox.pdmodel.encryption.SecurityHandler;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.encryption.StandardSecurityHandler;

public final class SecurityHandlerFactory {
    public static final SecurityHandlerFactory INSTANCE = new SecurityHandlerFactory();
    private final Map<String, Class<? extends SecurityHandler>> nameToHandler = new HashMap<String, Class<? extends SecurityHandler>>();
    private final Map<Class<? extends ProtectionPolicy>, Class<? extends SecurityHandler>> policyToHandler = new HashMap<Class<? extends ProtectionPolicy>, Class<? extends SecurityHandler>>();

    private SecurityHandlerFactory() {
        this.registerHandler("Standard", StandardSecurityHandler.class, StandardProtectionPolicy.class);
        this.registerHandler("Adobe.PubSec", PublicKeySecurityHandler.class, PublicKeyProtectionPolicy.class);
    }

    public void registerHandler(String name, Class<? extends SecurityHandler> securityHandler, Class<? extends ProtectionPolicy> protectionPolicy) {
        if (this.nameToHandler.containsKey(name)) {
            throw new IllegalStateException("The security handler name is already registered");
        }
        this.nameToHandler.put(name, securityHandler);
        this.policyToHandler.put(protectionPolicy, securityHandler);
    }

    public SecurityHandler newSecurityHandlerForPolicy(ProtectionPolicy policy) {
        Class<? extends SecurityHandler> handlerClass = this.policyToHandler.get(policy.getClass());
        if (handlerClass == null) {
            return null;
        }
        Class[] argsClasses = new Class[]{policy.getClass()};
        Object[] args = new Object[]{policy};
        return this.newSecurityHandler(handlerClass, argsClasses, args);
    }

    public SecurityHandler newSecurityHandlerForFilter(String name) {
        Class<? extends SecurityHandler> handlerClass = this.nameToHandler.get(name);
        if (handlerClass == null) {
            return null;
        }
        Class[] argsClasses = new Class[]{};
        Object[] args = new Object[]{};
        return this.newSecurityHandler(handlerClass, argsClasses, args);
    }

    private SecurityHandler newSecurityHandler(Class<? extends SecurityHandler> handlerClass, Class<?>[] argsClasses, Object[] args) {
        try {
            Constructor<? extends SecurityHandler> ctor = handlerClass.getDeclaredConstructor(argsClasses);
            return ctor.newInstance(args);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}

