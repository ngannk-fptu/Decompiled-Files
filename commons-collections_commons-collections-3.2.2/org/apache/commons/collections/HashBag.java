/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.Collection;
import java.util.HashMap;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.DefaultMapBag;

public class HashBag
extends DefaultMapBag
implements Bag {
    public HashBag() {
        super(new HashMap());
    }

    public HashBag(Collection coll) {
        this();
        this.addAll(coll);
    }
}

