/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework.namespace;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.resource.Namespace;

@ProviderType
public abstract class AbstractWiringNamespace
extends Namespace {
    public static final String CAPABILITY_MANDATORY_DIRECTIVE = "mandatory";
    public static final String CAPABILITY_BUNDLE_VERSION_ATTRIBUTE = "bundle-version";

    AbstractWiringNamespace() {
    }
}

