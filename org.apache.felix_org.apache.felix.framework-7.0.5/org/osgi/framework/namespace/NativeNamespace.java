/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework.namespace;

import org.osgi.resource.Namespace;

public final class NativeNamespace
extends Namespace {
    public static final String NATIVE_NAMESPACE = "osgi.native";
    public static final String CAPABILITY_OSNAME_ATTRIBUTE = "osgi.native.osname";
    public static final String CAPABILITY_OSVERSION_ATTRIBUTE = "osgi.native.osversion";
    public static final String CAPABILITY_PROCESSOR_ATTRIBUTE = "osgi.native.processor";
    public static final String CAPABILITY_LANGUAGE_ATTRIBUTE = "osgi.native.language";

    private NativeNamespace() {
    }
}

