/*
 * Decompiled with CFR 0.152.
 */
package com.jhlabs.image;

import com.jhlabs.image.Colormap;
import com.jhlabs.image.ImageMath;
import com.jhlabs.image.Spectrum;
import java.io.Serializable;

public class SpectrumColormap
implements Colormap,
Serializable {
    public int getColor(float v) {
        return Spectrum.wavelengthToRGB(380.0f + 400.0f * ImageMath.clamp(v, 0.0f, 1.0f));
    }
}

