/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.logging;

import org.jboss.logging.AbstractMdcLoggerProvider;
import org.jboss.logging.JDKLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.LoggerProvider;

final class JDKLoggerProvider
extends AbstractMdcLoggerProvider
implements LoggerProvider {
    JDKLoggerProvider() {
    }

    @Override
    public Logger getLogger(String name) {
        return new JDKLogger(name);
    }
}

