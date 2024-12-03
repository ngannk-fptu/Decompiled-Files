/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;

public class SequentialImage {
    public PlanarImage image;
    public float timeStamp;
    public Object cameraPosition;

    public SequentialImage(PlanarImage pi, float ts, Object cp) {
        if (pi == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.image = pi;
        this.timeStamp = ts;
        this.cameraPosition = cp;
    }
}

