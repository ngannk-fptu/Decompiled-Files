/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.collections.SortedBag;
import org.apache.commons.collections.bag.AbstractMapBag;

public class TreeBag
extends AbstractMapBag
implements SortedBag,
Serializable {
    private static final long serialVersionUID = -7740146511091606676L;

    public TreeBag() {
        super(new TreeMap());
    }

    public TreeBag(Comparator comparator) {
        super(new TreeMap(comparator));
    }

    public TreeBag(Collection coll) {
        this();
        this.addAll(coll);
    }

    public Object first() {
        return ((SortedMap)this.getMap()).firstKey();
    }

    public Object last() {
        return ((SortedMap)this.getMap()).lastKey();
    }

    public Comparator comparator() {
        return ((SortedMap)this.getMap()).comparator();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.comparator());
        super.doWriteObject(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        Comparator comp = (Comparator)in.readObject();
        super.doReadObject(new TreeMap(comp), in);
    }
}

