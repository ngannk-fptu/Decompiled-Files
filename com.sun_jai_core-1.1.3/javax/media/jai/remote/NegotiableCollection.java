/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import javax.media.jai.remote.JaiI18N;
import javax.media.jai.remote.Negotiable;

public class NegotiableCollection
implements Negotiable {
    private Vector elements;
    private Class elementClass;

    public NegotiableCollection(Collection collection) {
        Object obj;
        if (collection == null) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableCollection0"));
        }
        this.elements = new Vector();
        Iterator i = collection.iterator();
        if (i.hasNext()) {
            obj = i.next();
            this.elements.add(obj);
            this.elementClass = obj.getClass();
        }
        while (i.hasNext()) {
            obj = i.next();
            if (obj.getClass() != this.elementClass) {
                throw new IllegalArgumentException(JaiI18N.getString("NegotiableCollection1"));
            }
            this.elements.add(obj);
        }
    }

    public NegotiableCollection(Object[] objects) {
        if (objects == null) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableCollection0"));
        }
        int length = objects.length;
        if (length != 0) {
            this.elementClass = objects[0].getClass();
        }
        this.elements = new Vector(length);
        for (int i = 0; i < length; ++i) {
            if (objects[i].getClass() != this.elementClass) {
                throw new IllegalArgumentException(JaiI18N.getString("NegotiableCollection1"));
            }
            this.elements.add(objects[i]);
        }
    }

    public Collection getCollection() {
        if (this.elements.isEmpty()) {
            return null;
        }
        return this.elements;
    }

    public Negotiable negotiate(Negotiable other) {
        if (other == null) {
            return null;
        }
        if (!(other instanceof NegotiableCollection) || other.getNegotiatedValueClass() != this.elementClass) {
            return null;
        }
        Vector result = new Vector();
        Collection otherCollection = ((NegotiableCollection)other).getCollection();
        if (otherCollection == null) {
            return null;
        }
        Iterator i = this.elements.iterator();
        while (i.hasNext()) {
            Object obj = i.next();
            if (!otherCollection.contains(obj) || result.contains(obj)) continue;
            result.add(obj);
        }
        if (result.isEmpty()) {
            return null;
        }
        return new NegotiableCollection(result);
    }

    public Object getNegotiatedValue() {
        if (this.elements != null && this.elements.size() > 0) {
            return this.elements.elementAt(0);
        }
        return null;
    }

    public Class getNegotiatedValueClass() {
        return this.elementClass;
    }
}

