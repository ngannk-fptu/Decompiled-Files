/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework.hooks.service;

import java.util.Collection;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.BundleContext;

@ConsumerType
public interface ListenerHook {
    public void added(Collection<ListenerInfo> var1);

    public void removed(Collection<ListenerInfo> var1);

    @ProviderType
    public static interface ListenerInfo {
        public BundleContext getBundleContext();

        public String getFilter();

        public boolean isRemoved();

        public boolean equals(Object var1);

        public int hashCode();
    }
}

