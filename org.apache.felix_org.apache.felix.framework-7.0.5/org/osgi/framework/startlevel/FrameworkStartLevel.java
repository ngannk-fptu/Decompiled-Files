/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework.startlevel;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.BundleReference;
import org.osgi.framework.FrameworkListener;

@ProviderType
public interface FrameworkStartLevel
extends BundleReference {
    public int getStartLevel();

    public void setStartLevel(int var1, FrameworkListener ... var2);

    public int getInitialBundleStartLevel();

    public void setInitialBundleStartLevel(int var1);
}

