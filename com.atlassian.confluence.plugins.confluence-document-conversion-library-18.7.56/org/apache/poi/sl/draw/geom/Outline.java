/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.awt.Shape;
import org.apache.poi.sl.draw.geom.PathIf;

public class Outline {
    private final Shape shape;
    private final PathIf path;

    public Outline(Shape shape, PathIf path) {
        this.shape = shape;
        this.path = path;
    }

    public PathIf getPath() {
        return this.path;
    }

    public Shape getOutline() {
        return this.shape;
    }
}

