/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.util.persistence.hibernate.batch;

import java.io.Serializable;
import java.util.Collection;

public interface BatchFinder {
    public <E extends Serializable> Collection<E> find(long var1, Collection<String> var3, Class<E> var4);
}

