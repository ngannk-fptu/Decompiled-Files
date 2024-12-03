/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.collection.AbstractSerializableCollectionDecorator;

public class TransformedCollection
extends AbstractSerializableCollectionDecorator {
    private static final long serialVersionUID = 8692300188161871514L;
    protected final Transformer transformer;

    public static Collection decorate(Collection coll, Transformer transformer) {
        return new TransformedCollection(coll, transformer);
    }

    protected TransformedCollection(Collection coll, Transformer transformer) {
        super(coll);
        if (transformer == null) {
            throw new IllegalArgumentException("Transformer must not be null");
        }
        this.transformer = transformer;
    }

    protected Object transform(Object object) {
        return this.transformer.transform(object);
    }

    protected Collection transform(Collection coll) {
        ArrayList<Object> list = new ArrayList<Object>(coll.size());
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            list.add(this.transform(it.next()));
        }
        return list;
    }

    public boolean add(Object object) {
        object = this.transform(object);
        return this.getCollection().add(object);
    }

    public boolean addAll(Collection coll) {
        coll = this.transform(coll);
        return this.getCollection().addAll(coll);
    }
}

