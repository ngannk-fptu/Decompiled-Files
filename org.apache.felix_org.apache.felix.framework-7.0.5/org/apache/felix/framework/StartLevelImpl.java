/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import org.apache.felix.framework.Felix;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.service.startlevel.StartLevel;

public class StartLevelImpl
implements StartLevel {
    private final Felix m_felix;

    StartLevelImpl(Felix felix) {
        this.m_felix = felix;
    }

    @Override
    public int getStartLevel() {
        return this.m_felix.adapt(FrameworkStartLevel.class).getStartLevel();
    }

    @Override
    public void setStartLevel(int startlevel) {
        this.m_felix.adapt(FrameworkStartLevel.class).setStartLevel(startlevel, new FrameworkListener[0]);
    }

    @Override
    public int getBundleStartLevel(Bundle bundle) {
        return bundle.adapt(BundleStartLevel.class).getStartLevel();
    }

    @Override
    public void setBundleStartLevel(Bundle bundle, int startlevel) {
        bundle.adapt(BundleStartLevel.class).setStartLevel(startlevel);
    }

    @Override
    public int getInitialBundleStartLevel() {
        return this.m_felix.adapt(FrameworkStartLevel.class).getInitialBundleStartLevel();
    }

    @Override
    public void setInitialBundleStartLevel(int startlevel) {
        this.m_felix.adapt(FrameworkStartLevel.class).setInitialBundleStartLevel(startlevel);
    }

    @Override
    public boolean isBundlePersistentlyStarted(Bundle bundle) {
        return bundle.adapt(BundleStartLevel.class).isPersistentlyStarted();
    }

    @Override
    public boolean isBundleActivationPolicyUsed(Bundle bundle) {
        return bundle.adapt(BundleStartLevel.class).isActivationPolicyUsed();
    }
}

