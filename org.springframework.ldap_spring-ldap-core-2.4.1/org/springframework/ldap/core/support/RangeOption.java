/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RangeOption
implements Comparable<RangeOption> {
    public static final int TERMINAL_END_OF_RANGE = -1;
    public static final int TERMINAL_MISSING = -2;
    private int initial = 0;
    private int terminal = -1;
    private static final Pattern RANGE_PATTERN = Pattern.compile("^Range=([0-9]+)(-([0-9]+|\\*))?$", 2);

    public RangeOption(int initial) {
        this(initial, -1);
    }

    public RangeOption(int initial, int terminal) {
        if (terminal < 0 && terminal != -1 && terminal != -2) {
            throw new IllegalArgumentException("Illegal range-terminal: " + terminal);
        }
        if (initial < 0) {
            throw new IllegalArgumentException("Illegal range-initial: " + initial);
        }
        if (terminal >= 0 && terminal < initial) {
            throw new IllegalArgumentException("range-terminal cannot be smaller than range-initial: " + initial + "-" + terminal);
        }
        this.initial = initial;
        this.terminal = terminal;
    }

    public boolean isTerminalEndOfRange() {
        return this.terminal == -1;
    }

    public boolean isTerminalMissing() {
        return this.terminal == -2;
    }

    public int getInitial() {
        return this.initial;
    }

    public int getTerminal() {
        return this.terminal;
    }

    public boolean isFullRange() {
        return this.getInitial() == 0 && this.getTerminal() == -1;
    }

    public String toString() {
        StringBuilder rangeBuilder = new StringBuilder();
        this.appendTo(rangeBuilder);
        return rangeBuilder.toString();
    }

    public void appendTo(StringBuilder rangeBuilder) {
        rangeBuilder.append("Range=").append(this.initial);
        if (!this.isTerminalMissing()) {
            rangeBuilder.append('-');
            if (this.isTerminalEndOfRange()) {
                rangeBuilder.append('*');
            } else {
                rangeBuilder.append(this.terminal);
            }
        }
    }

    public static RangeOption parse(String option) {
        Matcher rangeMatcher = RANGE_PATTERN.matcher(option);
        rangeMatcher.find();
        if (!rangeMatcher.matches()) {
            return null;
        }
        String initialStr = rangeMatcher.group(1);
        int initial = Integer.parseInt(initialStr);
        int terminal = -2;
        if (rangeMatcher.group(2) != null) {
            String terminalStr = rangeMatcher.group(3);
            terminal = "*".equals(terminalStr) ? -1 : Integer.parseInt(terminalStr);
        }
        return new RangeOption(initial, terminal);
    }

    @Override
    public int compareTo(RangeOption that) {
        if (this.getInitial() != that.getInitial()) {
            throw new IllegalStateException("Ranges cannot be compared, range-initial not the same: " + this.toString() + " vs " + that.toString());
        }
        if (this.getTerminal() == that.getTerminal()) {
            return 0;
        }
        if (that.getTerminal() == -2) {
            throw new IllegalStateException("Don't know how to deal with missing range-terminal: " + that.toString());
        }
        if (this.getTerminal() == -2) {
            throw new IllegalStateException("Don't know how to deal with missing range-terminal: " + this.toString());
        }
        if (this.getTerminal() == -1) {
            return 1;
        }
        if (that.getTerminal() == -1) {
            return -1;
        }
        return this.getTerminal() > that.getTerminal() ? 1 : -1;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RangeOption that = (RangeOption)o;
        if (this.initial != that.initial) {
            return false;
        }
        return this.terminal == that.terminal;
    }

    public int hashCode() {
        int result = this.initial;
        result = 31 * result + this.terminal;
        return result;
    }

    public RangeOption nextRange(int pageSize) {
        if (this.getTerminal() < 0) {
            throw new IllegalStateException("Cannot generate next range, range-terminal: " + this.getTerminal());
        }
        if (pageSize < 0 && pageSize != -1) {
            throw new IllegalArgumentException("Invalid page size: " + pageSize);
        }
        int initial = this.getTerminal() + 1;
        int terminal = pageSize == -1 ? -1 : this.getTerminal() + pageSize;
        return new RangeOption(initial, terminal);
    }
}

