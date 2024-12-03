/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query;

import org.hibernate.HibernateException;

public class ParameterRecognitionException
extends HibernateException {
    public ParameterRecognitionException(String message) {
        super(message);
    }

    public ParameterRecognitionException(String message, Throwable cause) {
        super(message, cause);
    }
}

