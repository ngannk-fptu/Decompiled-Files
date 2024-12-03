/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.FileUtils;

public class TimeComparison
extends EnumeratedAttribute {
    private static final String[] VALUES = new String[]{"before", "after", "equal"};
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    public static final TimeComparison BEFORE = new TimeComparison("before");
    public static final TimeComparison AFTER = new TimeComparison("after");
    public static final TimeComparison EQUAL = new TimeComparison("equal");

    public TimeComparison() {
    }

    public TimeComparison(String value) {
        this.setValue(value);
    }

    @Override
    public String[] getValues() {
        return VALUES;
    }

    public boolean evaluate(long t1, long t2) {
        return this.evaluate(t1, t2, FILE_UTILS.getFileTimestampGranularity());
    }

    public boolean evaluate(long t1, long t2, long g) {
        int cmp = this.getIndex();
        if (cmp == -1) {
            throw new BuildException("TimeComparison value not set.");
        }
        if (cmp == 0) {
            return t1 - g < t2;
        }
        if (cmp == 1) {
            return t1 + g > t2;
        }
        return Math.abs(t1 - t2) <= g;
    }

    public static int compare(long t1, long t2) {
        return TimeComparison.compare(t1, t2, FILE_UTILS.getFileTimestampGranularity());
    }

    public static int compare(long t1, long t2, long g) {
        long diff = t1 - t2;
        long abs = Math.abs(diff);
        return abs > Math.abs(g) ? (int)(diff / abs) : 0;
    }
}

