/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.util.UUID;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface UUIDGenerationStrategy
extends Serializable {
    public int getGeneratedVersion();

    public UUID generateUUID(SharedSessionContractImplementor var1);
}

