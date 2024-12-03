/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.startlevel;

import org.osgi.framework.Bundle;

public interface StartLevel {
    public int getStartLevel();

    public void setStartLevel(int var1);

    public int getBundleStartLevel(Bundle var1);

    public void setBundleStartLevel(Bundle var1, int var2);

    public int getInitialBundleStartLevel();

    public void setInitialBundleStartLevel(int var1);

    public boolean isBundlePersistentlyStarted(Bundle var1);

    public boolean isBundleActivationPolicyUsed(Bundle var1);
}

