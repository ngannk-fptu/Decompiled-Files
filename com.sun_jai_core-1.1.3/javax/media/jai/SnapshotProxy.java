/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.image.Raster;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.Snapshot;

final class SnapshotProxy
extends PlanarImage {
    Snapshot parent;

    SnapshotProxy(Snapshot parent) {
        super(new ImageLayout(parent), null, null);
        this.parent = parent;
    }

    public Raster getTile(int tileX, int tileY) {
        return this.parent.getTile(tileX, tileY);
    }

    public void dispose() {
        this.parent.dispose();
    }
}

