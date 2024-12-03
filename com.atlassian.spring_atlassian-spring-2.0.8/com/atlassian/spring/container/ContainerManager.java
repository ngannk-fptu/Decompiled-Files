/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.spring.container;

import com.atlassian.spring.container.ComponentNotFoundException;
import com.atlassian.spring.container.ComponentTypeMismatchException;
import com.atlassian.spring.container.ContainerContext;

public class ContainerManager {
    private static ContainerManager instance = new ContainerManager();
    private ContainerContext containerContext = null;
    private static boolean containerSetup = false;

    public static ContainerManager getInstance() {
        return instance;
    }

    private ContainerManager() {
    }

    public static Object getComponent(String key) throws ComponentNotFoundException {
        return ContainerManager.getInstance().getContainerContext().getComponent(key);
    }

    public static <T> T getComponent(String key, Class<T> aClass) throws ComponentNotFoundException, ComponentTypeMismatchException {
        Object o = ContainerManager.getInstance().getContainerContext().getComponent(key);
        if (aClass.isAssignableFrom(o.getClass())) {
            return aClass.cast(o);
        }
        throw new ComponentTypeMismatchException("Component '" + key + "' of type '" + o.getClass() + "' cannot be assigned to requested type '" + aClass + "'");
    }

    public static void autowireComponent(Object component) {
        ContainerManager.getInstance().getContainerContext().autowireComponent(component);
    }

    public ContainerContext getContainerContext() {
        return this.containerContext;
    }

    public void setContainerContext(ContainerContext containerContext) {
        this.containerContext = containerContext;
    }

    public static void resetInstance() {
        instance = new ContainerManager();
        containerSetup = false;
    }

    public static boolean isContainerSetup() {
        return ContainerManager.getInstance().containerContext != null && ContainerManager.getInstance().containerContext.isSetup();
    }
}

