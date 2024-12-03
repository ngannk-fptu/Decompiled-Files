/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.framework.hooks.bundle;

import java.util.Collection;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.Bundle;

@ConsumerType
public interface CollisionHook {
    public static final int INSTALLING = 1;
    public static final int UPDATING = 2;

    public void filterCollisions(int var1, Bundle var2, Collection<Bundle> var3);
}

