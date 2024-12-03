/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.util.Collection;
import java.util.Iterator;
import javax.media.jai.CollectionImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.SequentialImage;

public class ImageSequence
extends CollectionImage {
    protected ImageSequence() {
    }

    public ImageSequence(Collection images) {
        super(images);
    }

    public PlanarImage getImage(float ts) {
        Iterator iter = this.iterator();
        while (iter.hasNext()) {
            SequentialImage si = (SequentialImage)iter.next();
            if (si.timeStamp != ts) continue;
            return si.image;
        }
        return null;
    }

    public PlanarImage getImage(Object cp) {
        if (cp != null) {
            Iterator iter = this.iterator();
            while (iter.hasNext()) {
                SequentialImage si = (SequentialImage)iter.next();
                if (!si.cameraPosition.equals(cp)) continue;
                return si.image;
            }
        }
        return null;
    }

    public float getTimeStamp(PlanarImage pi) {
        if (pi != null) {
            Iterator iter = this.iterator();
            while (iter.hasNext()) {
                SequentialImage si = (SequentialImage)iter.next();
                if (!si.image.equals(pi)) continue;
                return si.timeStamp;
            }
        }
        return -3.4028235E38f;
    }

    public Object getCameraPosition(PlanarImage pi) {
        if (pi != null) {
            Iterator iter = this.iterator();
            while (iter.hasNext()) {
                SequentialImage si = (SequentialImage)iter.next();
                if (!si.image.equals(pi)) continue;
                return si.cameraPosition;
            }
        }
        return null;
    }

    public boolean add(Object o) {
        if (o != null && o instanceof SequentialImage) {
            return super.add(o);
        }
        return false;
    }

    public boolean remove(PlanarImage pi) {
        if (pi != null) {
            Iterator iter = this.iterator();
            while (iter.hasNext()) {
                SequentialImage si = (SequentialImage)iter.next();
                if (!si.image.equals(pi)) continue;
                return super.remove(si);
            }
        }
        return false;
    }

    public boolean remove(float ts) {
        Iterator iter = this.iterator();
        while (iter.hasNext()) {
            SequentialImage si = (SequentialImage)iter.next();
            if (si.timeStamp != ts) continue;
            return super.remove(si);
        }
        return false;
    }

    public boolean remove(Object cp) {
        if (cp != null) {
            Iterator iter = this.iterator();
            while (iter.hasNext()) {
                SequentialImage si = (SequentialImage)iter.next();
                if (!si.cameraPosition.equals(cp)) continue;
                return super.remove(si);
            }
        }
        return false;
    }
}

