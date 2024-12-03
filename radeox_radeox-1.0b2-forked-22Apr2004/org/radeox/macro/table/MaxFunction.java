/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.macro.table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.macro.table.Function;
import org.radeox.macro.table.Table;

public class MaxFunction
implements Function {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$macro$table$MaxFunction == null ? (class$org$radeox$macro$table$MaxFunction = MaxFunction.class$("org.radeox.macro.table.MaxFunction")) : class$org$radeox$macro$table$MaxFunction));
    static /* synthetic */ Class class$org$radeox$macro$table$MaxFunction;

    public String getName() {
        return "MAX";
    }

    public void execute(Table table, int posx, int posy, int startX, int startY, int endX, int endY) {
        float max = 0.0f;
        boolean floating = false;
        for (int x = startX; x <= endX; ++x) {
            for (int y = startY; y <= endY; ++y) {
                float value = 0.0f;
                try {
                    value += (float)Integer.parseInt((String)table.getXY(x, y));
                }
                catch (Exception e) {
                    try {
                        value += Float.parseFloat((String)table.getXY(x, y));
                        floating = true;
                    }
                    catch (NumberFormatException e1) {
                        log.debug((Object)("SumFunction: unable to parse " + table.getXY(x, y)));
                    }
                }
                if (!(max < value)) continue;
                max = value;
            }
        }
        if (floating) {
            table.setXY(posx, posy, "" + max);
        } else {
            table.setXY(posx, posy, "" + (int)max);
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

