/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;
import org.apache.tools.ant.types.EnumeratedAttribute;

public class WaitFor
extends ConditionBase {
    public static final long ONE_MILLISECOND = 1L;
    public static final long ONE_SECOND = 1000L;
    public static final long ONE_MINUTE = 60000L;
    public static final long ONE_HOUR = 3600000L;
    public static final long ONE_DAY = 86400000L;
    public static final long ONE_WEEK = 604800000L;
    public static final long DEFAULT_MAX_WAIT_MILLIS = 180000L;
    public static final long DEFAULT_CHECK_MILLIS = 500L;
    private long maxWait = 180000L;
    private long maxWaitMultiplier = 1L;
    private long checkEvery = 500L;
    private long checkEveryMultiplier = 1L;
    private String timeoutProperty;

    public WaitFor() {
        super("waitfor");
    }

    public WaitFor(String taskName) {
        super(taskName);
    }

    public void setMaxWait(long time) {
        this.maxWait = time;
    }

    public void setMaxWaitUnit(Unit unit) {
        this.maxWaitMultiplier = unit.getMultiplier();
    }

    public void setCheckEvery(long time) {
        this.checkEvery = time;
    }

    public void setCheckEveryUnit(Unit unit) {
        this.checkEveryMultiplier = unit.getMultiplier();
    }

    public void setTimeoutProperty(String p) {
        this.timeoutProperty = p;
    }

    public void execute() throws BuildException {
        if (this.countConditions() > 1) {
            throw new BuildException("You must not nest more than one condition into %s", this.getTaskName());
        }
        if (this.countConditions() < 1) {
            throw new BuildException("You must nest a condition into %s", this.getTaskName());
        }
        Condition c = this.getConditions().nextElement();
        try {
            long maxWaitMillis = this.calculateMaxWaitMillis();
            long checkEveryMillis = this.calculateCheckEveryMillis();
            long start = System.currentTimeMillis();
            long end = start + maxWaitMillis;
            while (System.currentTimeMillis() < end) {
                if (c.eval()) {
                    this.processSuccess();
                    return;
                }
                Thread.sleep(checkEveryMillis);
            }
        }
        catch (InterruptedException e) {
            this.log("Task " + this.getTaskName() + " interrupted, treating as timed out.");
        }
        this.processTimeout();
    }

    public long calculateCheckEveryMillis() {
        return this.checkEvery * this.checkEveryMultiplier;
    }

    public long calculateMaxWaitMillis() {
        return this.maxWait * this.maxWaitMultiplier;
    }

    protected void processSuccess() {
        this.log(this.getTaskName() + ": condition was met", 3);
    }

    protected void processTimeout() {
        this.log(this.getTaskName() + ": timeout", 3);
        if (this.timeoutProperty != null) {
            this.getProject().setNewProperty(this.timeoutProperty, "true");
        }
    }

    public static class Unit
    extends EnumeratedAttribute {
        public static final String MILLISECOND = "millisecond";
        public static final String SECOND = "second";
        public static final String MINUTE = "minute";
        public static final String HOUR = "hour";
        public static final String DAY = "day";
        public static final String WEEK = "week";
        private static final String[] UNITS = new String[]{"millisecond", "second", "minute", "hour", "day", "week"};
        private Map<String, Long> timeTable = new HashMap<String, Long>();

        public Unit() {
            this.timeTable.put(MILLISECOND, 1L);
            this.timeTable.put(SECOND, 1000L);
            this.timeTable.put(MINUTE, 60000L);
            this.timeTable.put(HOUR, 3600000L);
            this.timeTable.put(DAY, 86400000L);
            this.timeTable.put(WEEK, 604800000L);
        }

        public long getMultiplier() {
            String key = this.getValue().toLowerCase(Locale.ENGLISH);
            return this.timeTable.get(key);
        }

        @Override
        public String[] getValues() {
            return UNITS;
        }
    }
}

