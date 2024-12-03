/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.domain;

import java.util.Optional;

public interface AuditorAware<T> {
    public Optional<T> getCurrentAuditor();
}

