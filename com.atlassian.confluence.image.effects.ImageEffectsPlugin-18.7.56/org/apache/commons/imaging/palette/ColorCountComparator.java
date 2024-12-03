/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.palette;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.imaging.palette.ColorComponent;
import org.apache.commons.imaging.palette.ColorCount;

public class ColorCountComparator
implements Comparator<ColorCount>,
Serializable {
    private static final long serialVersionUID = 1L;
    private ColorComponent colorComponent;

    public ColorCountComparator(ColorComponent colorComponent) {
        this.colorComponent = colorComponent;
    }

    @Override
    public int compare(ColorCount c1, ColorCount c2) {
        switch (this.colorComponent) {
            case ALPHA: {
                return c1.alpha - c2.alpha;
            }
            case RED: {
                return c1.red - c2.red;
            }
            case GREEN: {
                return c1.green - c2.green;
            }
            case BLUE: {
                return c1.blue - c2.blue;
            }
        }
        return 0;
    }
}

