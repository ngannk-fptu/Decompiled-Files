/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.logging;

import com.hazelcast.core.Member;
import java.util.EventObject;
import java.util.logging.LogRecord;

public class LogEvent
extends EventObject {
    private final LogRecord logRecord;
    private final Member member;

    public LogEvent(LogRecord logRecord, Member member) {
        super(member);
        this.logRecord = logRecord;
        this.member = member;
    }

    public Member getMember() {
        return this.member;
    }

    public LogRecord getLogRecord() {
        return this.logRecord;
    }
}

