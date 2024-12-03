/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.cluster.safety;

import com.atlassian.annotations.Internal;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Internal
public interface ClusterSafetyManager {
    public void verify(long var1);
}

