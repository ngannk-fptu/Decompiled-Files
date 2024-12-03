/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.debug;

import com.mchange.lang.ThrowableUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ThreadNameStackTraceRecorder {
    static final String NL = System.getProperty("line.separator", "\r\n");
    Set set = new HashSet();
    String dumpHeader;
    String stackTraceHeader;

    public ThreadNameStackTraceRecorder(String string) {
        this(string, "Debug Stack Trace.");
    }

    public ThreadNameStackTraceRecorder(String string, String string2) {
        this.dumpHeader = string;
        this.stackTraceHeader = string2;
    }

    public synchronized Object record() {
        Record record = new Record(this.stackTraceHeader);
        this.set.add(record);
        return record;
    }

    public synchronized void remove(Object object) {
        this.set.remove(object);
    }

    public synchronized int size() {
        return this.set.size();
    }

    public synchronized String getDump() {
        return this.getDump(null);
    }

    public synchronized String getDump(String string) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss.SSSS");
        StringBuffer stringBuffer = new StringBuffer(2047);
        stringBuffer.append(NL);
        stringBuffer.append("----------------------------------------------------");
        stringBuffer.append(NL);
        stringBuffer.append(this.dumpHeader);
        stringBuffer.append(NL);
        if (string != null) {
            stringBuffer.append(string);
            stringBuffer.append(NL);
        }
        boolean bl = true;
        Iterator iterator = this.set.iterator();
        while (iterator.hasNext()) {
            if (bl) {
                bl = false;
            } else {
                stringBuffer.append("---");
                stringBuffer.append(NL);
            }
            Record record = (Record)iterator.next();
            stringBuffer.append(simpleDateFormat.format(new Date(record.time)));
            stringBuffer.append(" --> Thread Name: ");
            stringBuffer.append(record.threadName);
            stringBuffer.append(NL);
            stringBuffer.append("Stack Trace: ");
            stringBuffer.append(ThrowableUtils.extractStackTrace(record.stackTrace));
        }
        stringBuffer.append("----------------------------------------------------");
        stringBuffer.append(NL);
        return stringBuffer.toString();
    }

    private static final class Record
    implements Comparable {
        long time = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();
        Throwable stackTrace;

        Record(String string) {
            this.stackTrace = new Exception(string);
        }

        public int compareTo(Object object) {
            int n;
            Record record = (Record)object;
            if (this.time > record.time) {
                return 1;
            }
            if (this.time < record.time) {
                return -1;
            }
            int n2 = System.identityHashCode(this);
            if (n2 > (n = System.identityHashCode(record))) {
                return 1;
            }
            if (n2 < n) {
                return -1;
            }
            return 0;
        }
    }
}

