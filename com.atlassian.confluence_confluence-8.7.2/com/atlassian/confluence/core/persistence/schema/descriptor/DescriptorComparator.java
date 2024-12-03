/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.confluence.core.persistence.schema.descriptor;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public interface DescriptorComparator<T> {
    public boolean matches(T var1);
}

