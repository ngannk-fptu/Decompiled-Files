/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.operations.registry;

import com.atlassian.instrumentation.operations.OpSnapshot;
import java.text.DecimalFormat;

public class OpFormatter {
    public static final String DEFAULT_FORMAT_STR = "#0.00#";
    private final OpSnapshot snapshot;

    public OpFormatter(OpSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public OpSnapshot getSnapshot() {
        return this.snapshot;
    }

    public String getInvocationCount() {
        return String.valueOf(this.snapshot.getInvocationCount());
    }

    public String getMillisecondsTaken() {
        return String.valueOf(this.snapshot.getMillisecondsTaken());
    }

    public String getMillisecondsPerInvocation() {
        return this.getMillisecondsPerInvocation(DEFAULT_FORMAT_STR);
    }

    public String getMillisecondsPerInvocation(String decimalFormatStr) {
        return this.timePerInvocationImpl(decimalFormatStr, this.snapshot.getMillisecondsTaken(), this.snapshot.getInvocationCount());
    }

    public String getSecondsTaken() {
        return this.getSecondsTaken(DEFAULT_FORMAT_STR);
    }

    public String getSecondsTaken(String decimalFormatStr) {
        double secondsTaken = (double)this.snapshot.getMillisecondsTaken() / 1000.0;
        return this.timePerInvocationImpl(decimalFormatStr, secondsTaken, 1.0);
    }

    public String getSecondsPerInvocation() {
        return this.getSecondsPerInvocation(DEFAULT_FORMAT_STR);
    }

    public String getSecondsPerInvocation(String decimalFormatStr) {
        double secondsTaken = (double)this.snapshot.getMillisecondsTaken() / 1000.0;
        return this.timePerInvocationImpl(decimalFormatStr, secondsTaken, this.snapshot.getInvocationCount());
    }

    private String timePerInvocationImpl(String decimalFormatStr, double timeTaken, double invoCount) {
        DecimalFormat df = new DecimalFormat(decimalFormatStr);
        return df.format(this.timePerInvocationImpl(timeTaken, invoCount));
    }

    private double timePerInvocationImpl(double timeTaken, double invoCount) {
        return timeTaken == 0.0 ? 0.0 : timeTaken / invoCount;
    }
}

