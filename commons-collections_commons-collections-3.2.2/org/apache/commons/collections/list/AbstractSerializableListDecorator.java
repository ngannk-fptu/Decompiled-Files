/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections.list.AbstractListDecorator;

public abstract class AbstractSerializableListDecorator
extends AbstractListDecorator
implements Serializable {
    private static final long serialVersionUID = 2684959196747496299L;

    protected AbstractSerializableListDecorator(List list) {
        super(list);
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

