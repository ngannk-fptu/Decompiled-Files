/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.interceptor;

import java.io.Serializable;
import java.util.Collection;
import org.springframework.lang.Nullable;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.support.DelegatingTransactionDefinition;

public abstract class DelegatingTransactionAttribute
extends DelegatingTransactionDefinition
implements TransactionAttribute,
Serializable {
    private final TransactionAttribute targetAttribute;

    public DelegatingTransactionAttribute(TransactionAttribute targetAttribute) {
        super(targetAttribute);
        this.targetAttribute = targetAttribute;
    }

    @Override
    @Nullable
    public String getQualifier() {
        return this.targetAttribute.getQualifier();
    }

    @Override
    public Collection<String> getLabels() {
        return this.targetAttribute.getLabels();
    }

    @Override
    public boolean rollbackOn(Throwable ex) {
        return this.targetAttribute.rollbackOn(ex);
    }
}

