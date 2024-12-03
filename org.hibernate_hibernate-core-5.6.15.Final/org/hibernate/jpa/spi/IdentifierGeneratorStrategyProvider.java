/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.spi;

import java.util.Map;

public interface IdentifierGeneratorStrategyProvider {
    public Map<String, Class<?>> getStrategies();
}

