/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.palette;

import org.apache.commons.imaging.palette.ColorComponent;
import org.apache.commons.imaging.palette.ColorGroup;

class ColorGroupCut {
    public final ColorGroup less;
    public final ColorGroup more;
    public final ColorComponent mode;
    public final int limit;

    ColorGroupCut(ColorGroup less, ColorGroup more, ColorComponent mode, int limit) {
        this.less = less;
        this.more = more;
        this.mode = mode;
        this.limit = limit;
    }

    public ColorGroup getColorGroup(int argb) {
        int value = this.mode.argbComponent(argb);
        if (value <= this.limit) {
            return this.less;
        }
        return this.more;
    }
}

