/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.interceptor;

import java.util.Collection;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;

public interface TransactionAttribute
extends TransactionDefinition {
    @Nullable
    public String getQualifier();

    public Collection<String> getLabels();

    public boolean rollbackOn(Throwable var1);
}

