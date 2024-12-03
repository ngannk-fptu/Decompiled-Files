/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework.namespace;

import org.osgi.framework.namespace.AbstractWiringNamespace;

public final class PackageNamespace
extends AbstractWiringNamespace {
    public static final String PACKAGE_NAMESPACE = "osgi.wiring.package";
    public static final String CAPABILITY_INCLUDE_DIRECTIVE = "include";
    public static final String CAPABILITY_EXCLUDE_DIRECTIVE = "exclude";
    public static final String CAPABILITY_VERSION_ATTRIBUTE = "version";
    public static final String CAPABILITY_BUNDLE_SYMBOLICNAME_ATTRIBUTE = "bundle-symbolic-name";
    public static final String RESOLUTION_DYNAMIC = "dynamic";

    private PackageNamespace() {
    }
}

