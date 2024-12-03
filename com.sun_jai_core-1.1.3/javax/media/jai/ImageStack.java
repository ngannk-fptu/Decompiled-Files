/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.util.Collection;
import java.util.Iterator;
import javax.media.jai.CollectionImage;
import javax.media.jai.CoordinateImage;
import javax.media.jai.PlanarImage;

public abstract class ImageStack
extends CollectionImage {
    protected ImageStack() {
    }

    public ImageStack(Collection images) {
        super(images);
    }

    public PlanarImage getImage(Object c) {
        if (c != null) {
            Iterator iter = this.iterator();
            while (iter.hasNext()) {
                CoordinateImage ci = (CoordinateImage)iter.next();
                if (!ci.coordinate.equals(c)) continue;
                return ci.image;
            }
        }
        return null;
    }

    public Object getCoordinate(PlanarImage pi) {
        if (pi != null) {
            Iterator iter = this.iterator();
            while (iter.hasNext()) {
                CoordinateImage ci = (CoordinateImage)iter.next();
                if (!ci.image.equals(pi)) continue;
                return ci.coordinate;
            }
        }
        return null;
    }

    public boolean add(Object o) {
        if (o != null && o instanceof CoordinateImage) {
            return super.add(o);
        }
        return false;
    }

    public boolean remove(PlanarImage pi) {
        if (pi != null) {
            Iterator iter = this.iterator();
            while (iter.hasNext()) {
                CoordinateImage ci = (CoordinateImage)iter.next();
                if (!ci.image.equals(pi)) continue;
                return super.remove(ci);
            }
        }
        return false;
    }

    public boolean remove(Object c) {
        if (c != null) {
            Iterator iter = this.iterator();
            while (iter.hasNext()) {
                CoordinateImage ci = (CoordinateImage)iter.next();
                if (!ci.coordinate.equals(c)) continue;
                return super.remove(ci);
            }
        }
        return false;
    }
}

