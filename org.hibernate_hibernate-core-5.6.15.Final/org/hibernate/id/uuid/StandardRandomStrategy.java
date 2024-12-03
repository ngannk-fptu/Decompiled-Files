/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.uuid;

import java.util.UUID;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerationStrategy;

public class StandardRandomStrategy
implements UUIDGenerationStrategy {
    public static final StandardRandomStrategy INSTANCE = new StandardRandomStrategy();

    @Override
    public int getGeneratedVersion() {
        return 4;
    }

    @Override
    public UUID generateUUID(SharedSessionContractImplementor session) {
        return UUID.randomUUID();
    }
}

