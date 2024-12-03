/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sisyphus;

import java.util.Date;

public class LogLine
implements Comparable<LogLine> {
    private int lineNo;
    private Date date;
    private String logLevel;

    public LogLine(int lineNo, Date date, String logLevel) {
        this.lineNo = lineNo;
        this.date = date;
        this.logLevel = logLevel;
    }

    public int getLineNo() {
        return this.lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLogLevel() {
        return this.logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public int compareTo(LogLine o) {
        return this.getLineNo() - o.getLineNo();
    }
}

