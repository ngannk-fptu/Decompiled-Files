/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.media.jai.AttributedImage;
import javax.media.jai.CollectionImage;
import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;

public class AttributedImageCollection
extends CollectionImage {
    protected AttributedImageCollection() {
    }

    public AttributedImageCollection(Collection images) {
        if (images == null) {
            throw new IllegalArgumentException(JaiI18N.getString("AttributedImageCollection0"));
        }
        try {
            this.imageCollection = (Collection)images.getClass().newInstance();
        }
        catch (Exception e) {
            this.imageCollection = new ArrayList(images.size());
        }
        Iterator iter = images.iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (!(o instanceof AttributedImage) || this.imageCollection.contains(o)) continue;
            this.imageCollection.add(o);
        }
    }

    public Set getAll(Object attribute) {
        if (attribute == null) {
            return (Set)this.imageCollection;
        }
        HashSet<AttributedImage> set = null;
        Iterator iter = this.iterator();
        while (iter.hasNext()) {
            AttributedImage ai = (AttributedImage)iter.next();
            if (!attribute.equals(ai.getAttribute())) continue;
            if (set == null) {
                set = new HashSet<AttributedImage>();
            }
            set.add(ai);
        }
        return set;
    }

    public Set getAll(PlanarImage image) {
        if (image == null) {
            return (Set)this.imageCollection;
        }
        HashSet<AttributedImage> set = null;
        Iterator iter = this.iterator();
        while (iter.hasNext()) {
            AttributedImage ai = (AttributedImage)iter.next();
            if (!image.equals(ai.getImage())) continue;
            if (set == null) {
                set = new HashSet<AttributedImage>();
            }
            set.add(ai);
        }
        return set;
    }

    public Set removeAll(Object attribute) {
        if (attribute == null) {
            return null;
        }
        Iterator iter = this.iterator();
        HashSet<AttributedImage> removed = null;
        while (iter.hasNext()) {
            AttributedImage ai = (AttributedImage)iter.next();
            if (!attribute.equals(ai.getAttribute())) continue;
            iter.remove();
            if (removed == null) {
                removed = new HashSet<AttributedImage>();
            }
            removed.add(ai);
        }
        return removed;
    }

    public Set removeAll(PlanarImage image) {
        if (image == null) {
            return null;
        }
        Iterator iter = this.iterator();
        HashSet<AttributedImage> removed = null;
        while (iter.hasNext()) {
            AttributedImage ai = (AttributedImage)iter.next();
            if (!image.equals(ai.getImage())) continue;
            iter.remove();
            if (removed == null) {
                removed = new HashSet<AttributedImage>();
            }
            removed.add(ai);
        }
        return removed;
    }

    public boolean add(Object o) {
        if (o == null || !(o instanceof AttributedImage)) {
            throw new IllegalArgumentException(JaiI18N.getString("AttributedImageCollection1"));
        }
        if (this.imageCollection.contains(o)) {
            return false;
        }
        return this.imageCollection.add(o);
    }

    public boolean addAll(Collection c) {
        if (c == null) {
            return false;
        }
        Iterator iter = c.iterator();
        boolean flag = false;
        while (iter.hasNext()) {
            Object o = iter.next();
            if (!(o instanceof AttributedImage) || this.imageCollection.contains(o) || !this.imageCollection.add(o)) continue;
            flag = true;
        }
        return flag;
    }

    public AttributedImage getAttributedImage(PlanarImage image) {
        if (image == null) {
            return null;
        }
        Iterator iter = this.iterator();
        while (iter.hasNext()) {
            AttributedImage ai = (AttributedImage)iter.next();
            if (!image.equals(ai.getImage())) continue;
            return ai;
        }
        return null;
    }

    public AttributedImage getAttributedImage(Object attribute) {
        if (attribute == null) {
            return null;
        }
        Iterator iter = this.iterator();
        while (iter.hasNext()) {
            AttributedImage ai = (AttributedImage)iter.next();
            if (!attribute.equals(ai.getAttribute())) continue;
            return ai;
        }
        return null;
    }
}

