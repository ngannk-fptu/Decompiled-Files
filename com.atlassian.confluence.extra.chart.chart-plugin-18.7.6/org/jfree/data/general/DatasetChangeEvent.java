/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.general;

import java.util.EventObject;
import org.jfree.data.general.Dataset;

public class DatasetChangeEvent
extends EventObject {
    private Dataset dataset;

    public DatasetChangeEvent(Object source, Dataset dataset) {
        super(source);
        this.dataset = dataset;
    }

    public Dataset getDataset() {
        return this.dataset;
    }
}

