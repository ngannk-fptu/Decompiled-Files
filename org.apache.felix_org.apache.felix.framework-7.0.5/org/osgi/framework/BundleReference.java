/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;

@ProviderType
public interface BundleReference {
    public Bundle getBundle();
}

