/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.plot.PieLabelRecord;

public abstract class AbstractPieLabelDistributor
implements Serializable {
    protected List labels = new ArrayList();

    public PieLabelRecord getPieLabelRecord(int index) {
        return (PieLabelRecord)this.labels.get(index);
    }

    public void addPieLabelRecord(PieLabelRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Null 'record' argument.");
        }
        this.labels.add(record);
    }

    public int getItemCount() {
        return this.labels.size();
    }

    public void clear() {
        this.labels.clear();
    }

    public abstract void distributeLabels(double var1, double var3);
}

