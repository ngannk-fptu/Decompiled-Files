/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.audit;

import java.util.Map;
import javax.annotation.Nonnull;

public interface Auditor {
    public void audit(@Nonnull String var1);

    public void audit(@Nonnull String var1, @Nonnull Map<String, String> var2);
}

