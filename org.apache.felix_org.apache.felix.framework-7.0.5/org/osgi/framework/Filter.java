/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework;

import java.util.Dictionary;
import java.util.Map;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.ServiceReference;

@ProviderType
public interface Filter {
    public boolean match(ServiceReference<?> var1);

    public boolean match(Dictionary<String, ?> var1);

    public String toString();

    public boolean equals(Object var1);

    public int hashCode();

    public boolean matchCase(Dictionary<String, ?> var1);

    public boolean matches(Map<String, ?> var1);
}

