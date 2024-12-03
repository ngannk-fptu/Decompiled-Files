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

public class MinFunction
implements Function {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$macro$table$MinFunction == null ? (class$org$radeox$macro$table$MinFunction = MinFunction.class$("org.radeox.macro.table.MinFunction")) : class$org$radeox$macro$table$MinFunction));
    static /* synthetic */ Class class$org$radeox$macro$table$MinFunction;

    public String getName() {
        return "MIN";
    }

    public void execute(Table table, int posx, int posy, int startX, int startY, int endX, int endY) {
        float min = Float.MAX_VALUE;
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
                if (!(min > value)) continue;
                min = value;
            }
        }
        if (floating) {
            table.setXY(posx, posy, "" + min);
        } else {
            table.setXY(posx, posy, "" + (int)min);
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

