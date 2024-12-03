/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.auditing;

import java.util.Optional;
import org.springframework.data.auditing.AuditableBeanWrapper;

public interface AuditableBeanWrapperFactory {
    public <T> Optional<AuditableBeanWrapper<T>> getBeanWrapperFor(T var1);
}

