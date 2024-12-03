/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework.launch;

import java.util.Map;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.launch.Framework;

@ProviderType
public interface FrameworkFactory {
    public Framework newFramework(Map<String, String> var1);
}

