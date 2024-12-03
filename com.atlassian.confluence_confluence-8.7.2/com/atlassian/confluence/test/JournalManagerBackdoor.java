/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.confluence.test;

import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
public interface JournalManagerBackdoor {
    public long getIgnoreWithinMillis();

    public void setIgnoreWithinMillis(long var1);
}

