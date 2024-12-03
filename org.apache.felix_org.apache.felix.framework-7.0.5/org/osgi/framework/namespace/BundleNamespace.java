/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework.namespace;

import org.osgi.framework.namespace.AbstractWiringNamespace;

public final class BundleNamespace
extends AbstractWiringNamespace {
    public static final String BUNDLE_NAMESPACE = "osgi.wiring.bundle";
    public static final String CAPABILITY_SINGLETON_DIRECTIVE = "singleton";
    public static final String CAPABILITY_FRAGMENT_ATTACHMENT_DIRECTIVE = "fragment-attachment";
    public static final String REQUIREMENT_EXTENSION_DIRECTIVE = "extension";
    public static final String REQUIREMENT_VISIBILITY_DIRECTIVE = "visibility";
    public static final String VISIBILITY_PRIVATE = "private";
    public static final String VISIBILITY_REEXPORT = "reexport";

    private BundleNamespace() {
    }
}

