/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.boolex.EvaluationException
 *  ch.qos.logback.core.boolex.EventEvaluatorBase
 */
package ch.qos.logback.classic.boolex;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

public class OnErrorEvaluator
extends EventEvaluatorBase<ILoggingEvent> {
    public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {
        return event.getLevel().levelInt >= 40000;
    }
}

