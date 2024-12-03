/*
 * Decompiled with CFR 0.152.
 */
package com.jhlabs.image;

import com.jhlabs.image.Colormap;
import java.io.Serializable;

public class GrayscaleColormap
implements Colormap,
Serializable {
    static final long serialVersionUID = -6015170137060961021L;

    public int getColor(float v) {
        int n = (int)(v * 255.0f);
        if (n < 0) {
            n = 0;
        } else if (n > 255) {
            n = 255;
        }
        return 0xFF000000 | n << 16 | n << 8 | n;
    }
}

