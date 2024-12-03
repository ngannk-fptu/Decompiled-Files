/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.io.Serializable;
import org.hibernate.SessionFactory;

public interface SessionFactoryObserver
extends Serializable {
    default public void sessionFactoryCreated(SessionFactory factory) {
    }

    default public void sessionFactoryClosing(SessionFactory factory) {
    }

    default public void sessionFactoryClosed(SessionFactory factory) {
    }
}

