/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.general;

import java.io.Serializable;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.general.SeriesDataset;

public abstract class AbstractSeriesDataset
extends AbstractDataset
implements SeriesDataset,
SeriesChangeListener,
Serializable {
    private static final long serialVersionUID = -6074996219705033171L;

    protected AbstractSeriesDataset() {
    }

    public abstract int getSeriesCount();

    public abstract Comparable getSeriesKey(int var1);

    public int indexOf(Comparable seriesKey) {
        int seriesCount = this.getSeriesCount();
        for (int s = 0; s < seriesCount; ++s) {
            if (!this.getSeriesKey(s).equals(seriesKey)) continue;
            return s;
        }
        return -1;
    }

    public void seriesChanged(SeriesChangeEvent event) {
        this.fireDatasetChanged();
    }
}

