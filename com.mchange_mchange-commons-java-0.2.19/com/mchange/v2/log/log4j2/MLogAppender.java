/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.Layout
 *  org.apache.logging.log4j.core.LogEvent
 *  org.apache.logging.log4j.core.appender.AbstractAppender
 *  org.apache.logging.log4j.message.Message
 */
package com.mchange.v2.log.log4j2;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import java.io.Serializable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.message.Message;

public class MLogAppender
extends AbstractAppender {
    protected MLogAppender(String string, Filter filter, Layout<? extends Serializable> layout, boolean bl) {
        super(string, filter, layout, bl);
        this.start();
    }

    public MLogAppender(String string, Filter filter) {
        this(string, filter, null, false);
    }

    public MLogAppender(String string) {
        this(string, null);
    }

    private MLevel levelToMLevel(Level level) {
        if (level == Level.OFF) {
            return MLevel.OFF;
        }
        if (level == Level.FATAL) {
            return MLevel.SEVERE;
        }
        if (level == Level.ERROR) {
            return MLevel.SEVERE;
        }
        if (level == Level.WARN) {
            return MLevel.WARNING;
        }
        if (level == Level.INFO) {
            return MLevel.INFO;
        }
        if (level == Level.DEBUG) {
            return MLevel.DEBUG;
        }
        if (level == Level.TRACE) {
            return MLevel.TRACE;
        }
        if (level == Level.ALL) {
            return MLevel.ALL;
        }
        throw new IllegalArgumentException("Unknown log4j2 Level: " + level);
    }

    public final void append(LogEvent logEvent) {
        MLog.getLogger(this.getName()).log(this.levelToMLevel(logEvent.getLevel()), this.messageToString(logEvent.getMessage()), logEvent.getThrown());
    }

    public String messageToString(Message message) {
        return message.getFormattedMessage();
    }
}

