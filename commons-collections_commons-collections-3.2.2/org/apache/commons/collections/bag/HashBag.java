/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.AbstractMapBag;

public class HashBag
extends AbstractMapBag
implements Bag,
Serializable {
    private static final long serialVersionUID = -6561115435802554013L;

    public HashBag() {
        super(new HashMap());
    }

    public HashBag(Collection coll) {
        this();
        this.addAll(coll);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        super.doWriteObject(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        super.doReadObject(new HashMap(), in);
    }
}

