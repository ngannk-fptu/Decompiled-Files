/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.util;

import java.util.UUID;

@FunctionalInterface
public interface IdGenerator {
    public UUID generateId();
}

