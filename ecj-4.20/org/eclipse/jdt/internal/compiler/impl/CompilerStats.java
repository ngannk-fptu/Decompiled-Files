/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.impl;

public class CompilerStats
implements Comparable {
    public long startTime;
    public long endTime;
    public long overallTime;
    public long lineCount;
    public long parseTime;
    public long resolveTime;
    public long analyzeTime;
    public long generateTime;

    public long elapsedTime() {
        return this.overallTime;
    }

    public int compareTo(Object o) {
        long time2;
        CompilerStats otherStats = (CompilerStats)o;
        long time1 = this.elapsedTime();
        return time1 < (time2 = otherStats.elapsedTime()) ? -1 : (time1 == time2 ? 0 : 1);
    }
}

