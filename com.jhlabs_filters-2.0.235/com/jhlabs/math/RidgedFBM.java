/*
 * Decompiled with CFR 0.152.
 */
package com.jhlabs.math;

import com.jhlabs.math.Function2D;
import com.jhlabs.math.Noise;

public class RidgedFBM
implements Function2D {
    public float evaluate(float x, float y) {
        return 1.0f - Math.abs(Noise.noise2(x, y));
    }
}

