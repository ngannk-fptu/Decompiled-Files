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

public class AvgFunction
implements Function {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$macro$table$AvgFunction == null ? (class$org$radeox$macro$table$AvgFunction = AvgFunction.class$("org.radeox.macro.table.AvgFunction")) : class$org$radeox$macro$table$AvgFunction));
    static /* synthetic */ Class class$org$radeox$macro$table$AvgFunction;

    public String getName() {
        return "AVG";
    }

    public void execute(Table table, int posx, int posy, int startX, int startY, int endX, int endY) {
        float sum = 0.0f;
        int count = 0;
        for (int x = startX; x <= endX; ++x) {
            for (int y = startY; y <= endY; ++y) {
                try {
                    sum += (float)Integer.parseInt((String)table.getXY(x, y));
                    ++count;
                    continue;
                }
                catch (Exception e) {
                    try {
                        sum += Float.parseFloat((String)table.getXY(x, y));
                        ++count;
                        continue;
                    }
                    catch (NumberFormatException e1) {
                        log.debug((Object)("SumFunction: unable to parse " + table.getXY(x, y)));
                    }
                }
            }
        }
        table.setXY(posx, posy, "" + sum / (float)count);
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

