/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.collection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import org.apache.commons.collections.collection.AbstractCollectionDecorator;

public abstract class AbstractSerializableCollectionDecorator
extends AbstractCollectionDecorator
implements Serializable {
    private static final long serialVersionUID = 6249888059822088500L;

    protected AbstractSerializableCollectionDecorator(Collection coll) {
        super(coll);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.collection);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.collection = (Collection)in.readObject();
    }
}

