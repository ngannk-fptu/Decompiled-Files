/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework.namespace;

import org.osgi.framework.namespace.AbstractWiringNamespace;

public final class HostNamespace
extends AbstractWiringNamespace {
    public static final String HOST_NAMESPACE = "osgi.wiring.host";
    public static final String CAPABILITY_SINGLETON_DIRECTIVE = "singleton";
    public static final String CAPABILITY_FRAGMENT_ATTACHMENT_DIRECTIVE = "fragment-attachment";
    public static final String FRAGMENT_ATTACHMENT_ALWAYS = "always";
    public static final String FRAGMENT_ATTACHMENT_RESOLVETIME = "resolve-time";
    public static final String FRAGMENT_ATTACHMENT_NEVER = "never";
    public static final String REQUIREMENT_EXTENSION_DIRECTIVE = "extension";
    public static final String EXTENSION_FRAMEWORK = "framework";
    public static final String EXTENSION_BOOTCLASSPATH = "bootclasspath";
    public static final String REQUIREMENT_VISIBILITY_DIRECTIVE = "visibility";

    private HostNamespace() {
    }
}

