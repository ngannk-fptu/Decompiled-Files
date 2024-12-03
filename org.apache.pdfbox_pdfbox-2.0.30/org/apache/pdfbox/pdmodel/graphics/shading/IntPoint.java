/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Point;
import java.awt.geom.Point2D;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class IntPoint
extends Point {
    private static final Log LOG = LogFactory.getLog(IntPoint.class);

    IntPoint(int x, int y) {
        super(x, y);
    }

    @Override
    public int hashCode() {
        return 89 * (623 + this.x) + this.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            if (obj instanceof Point2D) {
                LOG.error((Object)"IntPoint should not be used together with its base class");
            }
            return false;
        }
        IntPoint other = (IntPoint)obj;
        return this.x == other.x && this.y == other.y;
    }
}

