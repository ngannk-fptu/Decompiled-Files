/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.util.persistence.hibernate.batch;

public interface HibernateOperation<S> {
    public void performOperation(Object var1, S var2);
}

