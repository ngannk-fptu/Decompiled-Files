/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.general;

import java.util.EventListener;
import org.jfree.data.general.DatasetChangeEvent;

public interface DatasetChangeListener
extends EventListener {
    public void datasetChanged(DatasetChangeEvent var1);
}

