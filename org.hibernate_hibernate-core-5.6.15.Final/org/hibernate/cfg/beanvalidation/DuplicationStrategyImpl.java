/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg.beanvalidation;

import org.hibernate.cfg.beanvalidation.BeanValidationEventListener;
import org.hibernate.event.service.spi.DuplicationStrategy;

public class DuplicationStrategyImpl
implements DuplicationStrategy {
    public static final DuplicationStrategyImpl INSTANCE = new DuplicationStrategyImpl();

    @Override
    public boolean areMatch(Object listener, Object original) {
        return listener.getClass().equals(original.getClass()) && BeanValidationEventListener.class.equals(listener.getClass());
    }

    @Override
    public DuplicationStrategy.Action getAction() {
        return DuplicationStrategy.Action.KEEP_ORIGINAL;
    }
}

