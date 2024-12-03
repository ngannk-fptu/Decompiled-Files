/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.Layout
 *  ch.qos.logback.core.boolex.EventEvaluator
 *  ch.qos.logback.core.helpers.CyclicBuffer
 *  ch.qos.logback.core.net.SMTPAppenderBase
 *  org.slf4j.Marker
 */
package ch.qos.logback.classic.net;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.boolex.OnErrorEvaluator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.helpers.CyclicBuffer;
import ch.qos.logback.core.net.SMTPAppenderBase;
import java.util.List;
import java.util.concurrent.Future;
import org.slf4j.Marker;

public class SMTPAppender
extends SMTPAppenderBase<ILoggingEvent> {
    static final String DEFAULT_SUBJECT_PATTERN = "%logger{20} - %m";
    private boolean includeCallerData = false;

    public SMTPAppender() {
    }

    public void start() {
        if (this.eventEvaluator == null) {
            OnErrorEvaluator onError = new OnErrorEvaluator();
            onError.setContext(this.getContext());
            onError.setName("onError");
            onError.start();
            this.eventEvaluator = onError;
        }
        super.start();
    }

    public SMTPAppender(EventEvaluator<ILoggingEvent> eventEvaluator) {
        this.eventEvaluator = eventEvaluator;
    }

    protected void subAppend(CyclicBuffer<ILoggingEvent> cb, ILoggingEvent event) {
        if (this.includeCallerData) {
            event.getCallerData();
        }
        event.prepareForDeferredProcessing();
        cb.add((Object)event);
    }

    protected void fillBuffer(CyclicBuffer<ILoggingEvent> cb, StringBuffer sbuf) {
        int len = cb.length();
        for (int i = 0; i < len; ++i) {
            ILoggingEvent event = (ILoggingEvent)cb.get();
            sbuf.append(this.layout.doLayout((Object)event));
        }
    }

    protected boolean eventMarksEndOfLife(ILoggingEvent eventObject) {
        List<Marker> markers = eventObject.getMarkerList();
        if (markers == null || markers.isEmpty()) {
            return false;
        }
        for (Marker marker : markers) {
            if (!marker.contains(ClassicConstants.FINALIZE_SESSION_MARKER)) continue;
            return true;
        }
        return false;
    }

    protected Layout<ILoggingEvent> makeSubjectLayout(String subjectStr) {
        if (subjectStr == null) {
            subjectStr = DEFAULT_SUBJECT_PATTERN;
        }
        PatternLayout pl = new PatternLayout();
        pl.setContext(this.getContext());
        pl.setPattern(subjectStr);
        pl.setPostCompileProcessor(null);
        pl.start();
        return pl;
    }

    protected PatternLayout makeNewToPatternLayout(String toPattern) {
        PatternLayout pl = new PatternLayout();
        pl.setPattern(toPattern + "%nopex");
        return pl;
    }

    public boolean isIncludeCallerData() {
        return this.includeCallerData;
    }

    public void setIncludeCallerData(boolean includeCallerData) {
        this.includeCallerData = includeCallerData;
    }

    Future<?> getAsynchronousSendingFuture() {
        return this.asynchronousSendingFuture;
    }
}

