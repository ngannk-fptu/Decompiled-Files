/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction.support;

import org.springframework.transaction.PlatformTransactionManager;

public interface ResourceTransactionManager
extends PlatformTransactionManager {
    public Object getResourceFactory();
}

