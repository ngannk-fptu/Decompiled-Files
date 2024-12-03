/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework;

import java.util.Dictionary;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;

@ProviderType
public interface ServiceReference<S>
extends Comparable<Object>,
BundleReference {
    public Object getProperty(String var1);

    public String[] getPropertyKeys();

    @Override
    public Bundle getBundle();

    public Bundle[] getUsingBundles();

    public boolean isAssignableTo(Bundle var1, String var2);

    @Override
    public int compareTo(Object var1);

    public Dictionary<String, Object> getProperties();

    public <A> A adapt(Class<A> var1);
}

