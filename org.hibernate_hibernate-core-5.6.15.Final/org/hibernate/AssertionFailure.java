/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public class AssertionFailure
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(AssertionFailure.class);

    public AssertionFailure(String message) {
        super(message);
        LOG.failed(this);
    }

    public AssertionFailure(String message, Throwable cause) {
        super(message, cause);
        LOG.failed(cause);
    }
}

