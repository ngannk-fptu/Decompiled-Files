/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.internal;

import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.bytecode.spi.BytecodeProvider;

public final class SessionFactoryObserverForBytecodeEnhancer
implements SessionFactoryObserver {
    private final BytecodeProvider bytecodeProvider;

    public SessionFactoryObserverForBytecodeEnhancer(BytecodeProvider bytecodeProvider) {
        this.bytecodeProvider = bytecodeProvider;
    }

    @Override
    public void sessionFactoryCreated(SessionFactory factory) {
        this.bytecodeProvider.resetCaches();
    }

    @Override
    public void sessionFactoryClosing(SessionFactory factory) {
    }

    @Override
    public void sessionFactoryClosed(SessionFactory factory) {
        this.bytecodeProvider.resetCaches();
    }
}

