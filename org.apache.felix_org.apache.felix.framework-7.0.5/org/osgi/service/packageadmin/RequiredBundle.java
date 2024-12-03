/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.packageadmin;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

public interface RequiredBundle {
    public String getSymbolicName();

    public Bundle getBundle();

    public Bundle[] getRequiringBundles();

    public Version getVersion();

    public boolean isRemovalPending();
}

