/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarDir;

public enum BarDirection {
    BAR(STBarDir.BAR),
    COL(STBarDir.COL);

    final STBarDir.Enum underlying;
    private static final HashMap<STBarDir.Enum, BarDirection> reverse;

    private BarDirection(STBarDir.Enum direction) {
        this.underlying = direction;
    }

    static BarDirection valueOf(STBarDir.Enum direction) {
        return reverse.get(direction);
    }

    static {
        reverse = new HashMap();
        for (BarDirection value : BarDirection.values()) {
            reverse.put(value.underlying, value);
        }
    }
}

