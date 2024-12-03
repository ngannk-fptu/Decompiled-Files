/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

public interface InterfaceExtension {
    public String getInterface();

    public String getStaticHandler();

    public MethodSignature[] getMethods();

    public static interface MethodSignature {
        public String getName();

        public String getReturnType();

        public String[] getParameterTypes();

        public String[] getParameterNames();

        public String[] getExceptionTypes();
    }
}

