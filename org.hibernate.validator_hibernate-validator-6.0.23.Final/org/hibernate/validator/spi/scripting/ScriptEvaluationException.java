/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ValidationException
 */
package org.hibernate.validator.spi.scripting;

import javax.validation.ValidationException;
import org.hibernate.validator.Incubating;

@Incubating
public class ScriptEvaluationException
extends ValidationException {
    public ScriptEvaluationException() {
    }

    public ScriptEvaluationException(String message) {
        super(message);
    }

    public ScriptEvaluationException(Throwable cause) {
        super(cause);
    }

    public ScriptEvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}

