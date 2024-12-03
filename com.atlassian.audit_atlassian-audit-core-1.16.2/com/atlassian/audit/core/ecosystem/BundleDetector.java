/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.osgi.framework.Bundle
 */
package com.atlassian.audit.core.ecosystem;

import javax.annotation.Nonnull;
import org.osgi.framework.Bundle;

@FunctionalInterface
public interface BundleDetector {
    public boolean isInternal(@Nonnull Bundle var1);
}

