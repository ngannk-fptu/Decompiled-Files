/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.upm.osgi;

import com.atlassian.upm.osgi.Bundle;
import javax.annotation.Nullable;

public interface Service {
    public Bundle getBundle();

    public Iterable<Bundle> getUsingBundles();

    public Iterable<String> getObjectClasses();

    @Nullable
    public String getDescription();

    public long getId();

    public Iterable<String> getPid();

    public int getRanking();

    @Nullable
    public String getVendor();
}

