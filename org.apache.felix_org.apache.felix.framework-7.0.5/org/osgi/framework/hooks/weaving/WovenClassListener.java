/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.framework.hooks.weaving;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.hooks.weaving.WovenClass;

@ConsumerType
public interface WovenClassListener {
    public void modified(WovenClass var1);
}

