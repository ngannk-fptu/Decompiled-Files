/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.Context
 *  ch.qos.logback.core.pattern.CompositeConverter
 *  ch.qos.logback.core.pattern.Converter
 *  ch.qos.logback.core.pattern.ConverterUtil
 *  ch.qos.logback.core.pattern.PostCompileProcessor
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter;
import ch.qos.logback.classic.pattern.ThrowableHandlingConverter;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.pattern.CompositeConverter;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.ConverterUtil;
import ch.qos.logback.core.pattern.PostCompileProcessor;

public class EnsureExceptionHandling
implements PostCompileProcessor<ILoggingEvent> {
    public void process(Context context, Converter<ILoggingEvent> head) {
        if (head == null) {
            throw new IllegalArgumentException("cannot process empty chain");
        }
        if (!this.chainHandlesThrowable(head)) {
            Converter tail = ConverterUtil.findTail(head);
            ThrowableProxyConverter exConverter = null;
            LoggerContext loggerContext = (LoggerContext)context;
            exConverter = loggerContext.isPackagingDataEnabled() ? new ExtendedThrowableProxyConverter() : new ThrowableProxyConverter();
            tail.setNext((Converter)exConverter);
        }
    }

    public boolean chainHandlesThrowable(Converter<ILoggingEvent> head) {
        for (Converter c = head; c != null; c = c.getNext()) {
            if (c instanceof ThrowableHandlingConverter) {
                return true;
            }
            if (!(c instanceof CompositeConverter) || !this.compositeHandlesThrowable((CompositeConverter<ILoggingEvent>)((CompositeConverter)c))) continue;
            return true;
        }
        return false;
    }

    public boolean compositeHandlesThrowable(CompositeConverter<ILoggingEvent> compositeConverter) {
        Converter childConverter;
        for (Converter c = childConverter = compositeConverter.getChildConverter(); c != null; c = c.getNext()) {
            boolean r;
            if (c instanceof ThrowableHandlingConverter) {
                return true;
            }
            if (!(c instanceof CompositeConverter) || !(r = this.compositeHandlesThrowable((CompositeConverter<ILoggingEvent>)((CompositeConverter)c)))) continue;
            return true;
        }
        return false;
    }
}

