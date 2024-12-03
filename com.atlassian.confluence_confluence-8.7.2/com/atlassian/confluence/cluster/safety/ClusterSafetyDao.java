/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.cluster.safety;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ClusterSafetyDao {
    @Transactional(propagation=Propagation.REQUIRES_NEW, readOnly=true)
    public Integer getSafetyNumber();

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void setSafetyNumber(int var1);
}

