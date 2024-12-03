/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.pages.collab.impl.tracking;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionProgress;
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionSearchType;
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionType;

@Internal
public interface SynchronyEvictionProgressTracking {
    public SynchronyEvictionProgress startEviction(SynchronyEvictionType var1, Integer var2, Integer var3);

    default public SynchronyEvictionProgress startEviction(SynchronyEvictionType type, Integer thresholdHours) {
        return this.startEviction(type, thresholdHours, null);
    }

    public void finishEviction(SynchronyEvictionProgress var1, int var2, int var3);

    public void failEviction(SynchronyEvictionProgress var1);

    public void startSearch(SynchronyEvictionProgress var1, SynchronyEvictionSearchType var2, Integer var3);

    public void finishSearch(SynchronyEvictionProgress var1, Integer var2);

    public void failSearch(SynchronyEvictionProgress var1);

    public void startRemovalUnderLock(SynchronyEvictionProgress var1, Integer var2);

    public void finishRemovalUnderLock(SynchronyEvictionProgress var1, Integer var2);

    public void failRemovalUnderLock(SynchronyEvictionProgress var1);
}

