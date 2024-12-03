/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot;

import java.util.Collections;
import org.jfree.chart.plot.AbstractPieLabelDistributor;
import org.jfree.chart.plot.PieLabelRecord;

public class PieLabelDistributor
extends AbstractPieLabelDistributor {
    private double minGap = 4.0;

    public PieLabelDistributor(int labelCount) {
    }

    public void distributeLabels(double minY, double height) {
        this.sort();
        if (this.isOverlap()) {
            this.adjustDownwards(minY, height);
        }
        if (this.isOverlap()) {
            this.adjustUpwards(minY, height);
        }
        if (this.isOverlap()) {
            this.spreadEvenly(minY, height);
        }
    }

    private boolean isOverlap() {
        double y = 0.0;
        for (int i = 0; i < this.labels.size(); ++i) {
            PieLabelRecord plr = this.getPieLabelRecord(i);
            if (y > plr.getLowerY()) {
                return true;
            }
            y = plr.getUpperY();
        }
        return false;
    }

    protected void adjustInwards() {
        int lower = 0;
        for (int upper = this.labels.size() - 1; upper > lower; ++lower, --upper) {
            double adjust;
            if (lower < upper - 1) {
                PieLabelRecord r0 = this.getPieLabelRecord(lower);
                PieLabelRecord r1 = this.getPieLabelRecord(lower + 1);
                if (r1.getLowerY() < r0.getUpperY()) {
                    adjust = r0.getUpperY() - r1.getLowerY() + this.minGap;
                    r1.setAllocatedY(r1.getAllocatedY() + adjust);
                }
            }
            PieLabelRecord r2 = this.getPieLabelRecord(upper - 1);
            PieLabelRecord r3 = this.getPieLabelRecord(upper);
            if (!(r2.getUpperY() > r3.getLowerY())) continue;
            adjust = r2.getUpperY() - r3.getLowerY() + this.minGap;
            r3.setAllocatedY(r3.getAllocatedY() + adjust);
        }
    }

    protected void adjustDownwards(double minY, double height) {
        for (int i = 0; i < this.labels.size() - 1; ++i) {
            PieLabelRecord record0 = this.getPieLabelRecord(i);
            PieLabelRecord record1 = this.getPieLabelRecord(i + 1);
            if (!(record1.getLowerY() < record0.getUpperY())) continue;
            record1.setAllocatedY(Math.min(minY + height - record1.getLabelHeight() / 2.0, record0.getUpperY() + this.minGap + record1.getLabelHeight() / 2.0));
        }
    }

    protected void adjustUpwards(double minY, double height) {
        for (int i = this.labels.size() - 1; i > 0; --i) {
            PieLabelRecord record0 = this.getPieLabelRecord(i);
            PieLabelRecord record1 = this.getPieLabelRecord(i - 1);
            if (!(record1.getUpperY() > record0.getLowerY())) continue;
            record1.setAllocatedY(Math.max(minY + record1.getLabelHeight() / 2.0, record0.getLowerY() - this.minGap - record1.getLabelHeight() / 2.0));
        }
    }

    protected void spreadEvenly(double minY, double height) {
        double y = minY;
        double sumOfLabelHeights = 0.0;
        for (int i = 0; i < this.labels.size(); ++i) {
            sumOfLabelHeights += this.getPieLabelRecord(i).getLabelHeight();
        }
        double gap = height - sumOfLabelHeights;
        if (this.labels.size() > 1) {
            gap /= (double)(this.labels.size() - 1);
        }
        for (int i = 0; i < this.labels.size(); ++i) {
            PieLabelRecord record = this.getPieLabelRecord(i);
            record.setAllocatedY(y += record.getLabelHeight() / 2.0);
            y = y + record.getLabelHeight() / 2.0 + gap;
        }
    }

    public void sort() {
        Collections.sort(this.labels);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < this.labels.size(); ++i) {
            result.append(this.getPieLabelRecord(i).toString()).append("\n");
        }
        return result.toString();
    }
}

