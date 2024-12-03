/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util;

public class SecurityManagerEx
extends SecurityManager {
    private static final Class[] EMPTY_CLASSES = new Class[0];

    public Class[] getClassContext() {
        Class[] result = super.getClassContext();
        return result != null ? result : EMPTY_CLASSES;
    }
}

