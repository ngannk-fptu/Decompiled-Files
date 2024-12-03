/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;

public class SessionFactoryObserverChain
implements SessionFactoryObserver {
    private List<SessionFactoryObserver> observers;

    public void addObserver(SessionFactoryObserver observer) {
        if (this.observers == null) {
            this.observers = new ArrayList<SessionFactoryObserver>();
        }
        this.observers.add(observer);
    }

    @Override
    public void sessionFactoryCreated(SessionFactory factory) {
        if (this.observers == null) {
            return;
        }
        for (SessionFactoryObserver observer : this.observers) {
            observer.sessionFactoryCreated(factory);
        }
    }

    @Override
    public void sessionFactoryClosing(SessionFactory factory) {
        if (this.observers == null) {
            return;
        }
        int size = this.observers.size();
        for (int index = size - 1; index >= 0; --index) {
            this.observers.get(index).sessionFactoryClosing(factory);
        }
    }

    @Override
    public void sessionFactoryClosed(SessionFactory factory) {
        if (this.observers == null) {
            return;
        }
        int size = this.observers.size();
        for (int index = size - 1; index >= 0; --index) {
            this.observers.get(index).sessionFactoryClosed(factory);
        }
    }
}

