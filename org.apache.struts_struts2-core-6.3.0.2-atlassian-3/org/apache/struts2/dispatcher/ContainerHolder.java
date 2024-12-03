/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.inject.Container;

class ContainerHolder {
    private static final ThreadLocal<Container> instance = new ThreadLocal();

    ContainerHolder() {
    }

    public static void store(Container newInstance) {
        instance.set(newInstance);
    }

    public static Container get() {
        return instance.get();
    }

    public static void clear() {
        instance.remove();
    }
}

