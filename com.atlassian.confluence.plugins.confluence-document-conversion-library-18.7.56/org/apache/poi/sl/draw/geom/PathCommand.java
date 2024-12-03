/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.awt.geom.Path2D;
import org.apache.poi.sl.draw.geom.Context;

public interface PathCommand {
    public void execute(Path2D.Double var1, Context var2);
}

