/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.awt.Shape;
import org.xhtmlrenderer.layout.BoxRange;
import org.xhtmlrenderer.render.BlockBox;

public class BoxRangeData {
    private BlockBox _box;
    private BoxRange _range;
    private Shape _clip;

    public BoxRangeData() {
    }

    public BoxRangeData(BlockBox box, BoxRange range) {
        this._box = box;
        this._range = range;
    }

    public BlockBox getBox() {
        return this._box;
    }

    public void setBox(BlockBox box) {
        this._box = box;
    }

    public BoxRange getRange() {
        return this._range;
    }

    public void setRange(BoxRange range) {
        this._range = range;
    }

    public Shape getClip() {
        return this._clip;
    }

    public void setClip(Shape clip) {
        this._clip = clip;
    }

    public String toString() {
        return "[range= " + this._range + ", box=" + this._box + ", clip=" + this._clip + "]";
    }
}

