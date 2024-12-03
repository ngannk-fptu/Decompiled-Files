/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework;

import org.springframework.aop.framework.AdvisedSupport;

public interface AdvisedSupportListener {
    public void activated(AdvisedSupport var1);

    public void adviceChanged(AdvisedSupport var1);
}

