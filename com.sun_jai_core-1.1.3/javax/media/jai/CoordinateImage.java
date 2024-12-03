/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;

public class CoordinateImage {
    public PlanarImage image;
    public Object coordinate;

    public CoordinateImage(PlanarImage pi, Object c) {
        if (pi == null || c == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.image = pi;
        this.coordinate = c;
    }
}

