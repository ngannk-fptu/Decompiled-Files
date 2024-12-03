/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry.selector.spi;

import org.hibernate.HibernateException;

public class StrategySelectionException
extends HibernateException {
    public StrategySelectionException(String message) {
        super(message);
    }

    public StrategySelectionException(String message, Throwable cause) {
        super(message, cause);
    }
}

