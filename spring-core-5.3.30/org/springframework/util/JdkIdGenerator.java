/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.util;

import java.util.UUID;
import org.springframework.util.IdGenerator;

public class JdkIdGenerator
implements IdGenerator {
    @Override
    public UUID generateId() {
        return UUID.randomUUID();
    }
}

