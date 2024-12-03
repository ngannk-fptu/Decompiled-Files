/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.Logger;

public class LazyInitializationException
extends HibernateException {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)LazyInitializationException.class.getName());

    public LazyInitializationException(String message) {
        super(message);
        LOG.trace(message, (Throwable)((Object)this));
    }
}

