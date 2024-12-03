/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.awt.geom.Path2D;
import org.apache.poi.sl.draw.geom.ClosePathCommandIf;
import org.apache.poi.sl.draw.geom.Context;

public class ClosePathCommand
implements ClosePathCommandIf {
    @Override
    public void execute(Path2D.Double path, Context ctx) {
        path.closePath();
    }

    public int hashCode() {
        return 790622;
    }

    public boolean equals(Object obj) {
        return obj instanceof ClosePathCommand;
    }
}

