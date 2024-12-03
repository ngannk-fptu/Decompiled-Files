/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout.breaker;

public class BreakPoint
implements Comparable<BreakPoint> {
    private int position;
    private String hyphen;

    public BreakPoint(int position) {
        this.position = position;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String toString() {
        return "BreakPoint [position=" + this.position + "]";
    }

    @Override
    public int compareTo(BreakPoint o) {
        return Integer.compare(this.position, o.position);
    }

    public void setHyphen(String hyphen) {
        this.hyphen = hyphen;
    }

    public String getHyphen() {
        if (this.hyphen == null) {
            return "";
        }
        return this.hyphen;
    }

    public static BreakPoint getDonePoint() {
        return new BreakPoint(-1);
    }
}

