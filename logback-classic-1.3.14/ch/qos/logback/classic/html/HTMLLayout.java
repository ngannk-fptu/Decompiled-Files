/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.CoreConstants
 *  ch.qos.logback.core.helpers.Transform
 *  ch.qos.logback.core.html.HTMLLayoutBase
 *  ch.qos.logback.core.html.IThrowableRenderer
 *  ch.qos.logback.core.pattern.Converter
 */
package ch.qos.logback.classic.html;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.html.DefaultCssBuilder;
import ch.qos.logback.classic.html.DefaultThrowableRenderer;
import ch.qos.logback.classic.pattern.MDCConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.helpers.Transform;
import ch.qos.logback.core.html.HTMLLayoutBase;
import ch.qos.logback.core.html.IThrowableRenderer;
import ch.qos.logback.core.pattern.Converter;
import java.util.Map;

public class HTMLLayout
extends HTMLLayoutBase<ILoggingEvent> {
    static final String DEFAULT_CONVERSION_PATTERN = "%date%thread%level%logger%mdc%msg";
    IThrowableRenderer<ILoggingEvent> throwableRenderer;

    public HTMLLayout() {
        this.pattern = DEFAULT_CONVERSION_PATTERN;
        this.throwableRenderer = new DefaultThrowableRenderer();
        this.cssBuilder = new DefaultCssBuilder();
    }

    public void start() {
        int errorCount = 0;
        if (this.throwableRenderer == null) {
            this.addError("ThrowableRender cannot be null.");
            ++errorCount;
        }
        if (errorCount == 0) {
            super.start();
        }
    }

    protected Map<String, String> getDefaultConverterMap() {
        return PatternLayout.DEFAULT_CONVERTER_MAP;
    }

    public String doLayout(ILoggingEvent event) {
        StringBuilder buf = new StringBuilder();
        this.startNewTableIfLimitReached(buf);
        boolean odd = true;
        if ((this.counter++ & 1L) == 0L) {
            odd = false;
        }
        String level = event.getLevel().toString().toLowerCase();
        buf.append(CoreConstants.LINE_SEPARATOR);
        buf.append("<tr class=\"");
        buf.append(level);
        if (odd) {
            buf.append(" odd\">");
        } else {
            buf.append(" even\">");
        }
        buf.append(CoreConstants.LINE_SEPARATOR);
        for (Converter c = this.head; c != null; c = c.getNext()) {
            this.appendEventToBuffer(buf, (Converter<ILoggingEvent>)c, event);
        }
        buf.append("</tr>");
        buf.append(CoreConstants.LINE_SEPARATOR);
        if (event.getThrowableProxy() != null) {
            this.throwableRenderer.render(buf, (Object)event);
        }
        return buf.toString();
    }

    private void appendEventToBuffer(StringBuilder buf, Converter<ILoggingEvent> c, ILoggingEvent event) {
        buf.append("<td class=\"");
        buf.append(this.computeConverterName(c));
        buf.append("\">");
        buf.append(Transform.escapeTags((String)c.convert((Object)event)));
        buf.append("</td>");
        buf.append(CoreConstants.LINE_SEPARATOR);
    }

    public IThrowableRenderer<ILoggingEvent> getThrowableRenderer() {
        return this.throwableRenderer;
    }

    public void setThrowableRenderer(IThrowableRenderer<ILoggingEvent> throwableRenderer) {
        this.throwableRenderer = throwableRenderer;
    }

    protected String computeConverterName(Converter<ILoggingEvent> c) {
        if (c instanceof MDCConverter) {
            MDCConverter mc = (MDCConverter)c;
            String key = mc.getFirstOption();
            if (key != null) {
                return key;
            }
            return "MDC";
        }
        return super.computeConverterName(c);
    }
}

