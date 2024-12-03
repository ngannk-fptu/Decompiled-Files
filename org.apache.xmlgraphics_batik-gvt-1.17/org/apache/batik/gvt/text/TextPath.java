/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.geom.PathLength
 */
package org.apache.batik.gvt.text;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import org.apache.batik.ext.awt.geom.PathLength;

public class TextPath {
    private PathLength pathLength;
    private float startOffset;

    public TextPath(GeneralPath path) {
        this.pathLength = new PathLength((Shape)path);
        this.startOffset = 0.0f;
    }

    public void setStartOffset(float startOffset) {
        this.startOffset = startOffset;
    }

    public float getStartOffset() {
        return this.startOffset;
    }

    public float lengthOfPath() {
        return this.pathLength.lengthOfPath();
    }

    public float angleAtLength(float length) {
        return this.pathLength.angleAtLength(length);
    }

    public Point2D pointAtLength(float length) {
        return this.pathLength.pointAtLength(length);
    }
}

