/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import org.xhtmlrenderer.util.Configuration;

public class XRSimpleLogFormatter
extends Formatter {
    private static final String MSG_FMT = Configuration.valueFor("xr.simple-log-format", "{1}:\n  {5}\n").trim() + "\n";
    private static final String EXMSG_FMT = Configuration.valueFor("xr.simple-log-format-throwable", "{1}:\n  {5}\n{8}").trim() + "\n";
    private final MessageFormat mformat = new MessageFormat(MSG_FMT);
    private final MessageFormat exmformat = new MessageFormat(EXMSG_FMT);

    @Override
    public String format(LogRecord record) {
        Throwable th = record.getThrown();
        String thName = "";
        String thMessage = "";
        String trace = null;
        if (th != null) {
            StringWriter sw = new StringWriter();
            th.printStackTrace(new PrintWriter(sw));
            trace = sw.toString();
            thName = th.getClass().getName();
            thMessage = th.getMessage();
        }
        String[] args = new String[]{String.valueOf(record.getMillis()), record.getLoggerName(), record.getLevel().toString(), record.getSourceClassName(), record.getSourceMethodName(), record.getMessage(), thName, thMessage, trace};
        return th == null ? this.mformat.format(args) : this.exmformat.format(args);
    }

    @Override
    public String formatMessage(LogRecord record) {
        return super.formatMessage(record);
    }

    @Override
    public String getHead(Handler h) {
        return super.getHead(h);
    }

    @Override
    public String getTail(Handler h) {
        return super.getTail(h);
    }
}

