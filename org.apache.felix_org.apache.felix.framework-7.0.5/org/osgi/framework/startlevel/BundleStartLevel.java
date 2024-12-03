/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework.startlevel;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.BundleReference;

@ProviderType
public interface BundleStartLevel
extends BundleReference {
    public int getStartLevel();

    public void setStartLevel(int var1);

    public boolean isPersistentlyStarted();

    public boolean isActivationPolicyUsed();
}

