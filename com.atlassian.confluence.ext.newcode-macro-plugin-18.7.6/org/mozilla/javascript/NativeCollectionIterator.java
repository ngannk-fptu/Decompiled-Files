/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Iterator;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ES6Iterator;
import org.mozilla.javascript.Hashtable;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class NativeCollectionIterator
extends ES6Iterator {
    private static final long serialVersionUID = 7094840979404373443L;
    private String className;
    private Type type;
    private transient Iterator<Hashtable.Entry> iterator = Collections.emptyIterator();

    static void init(ScriptableObject scope, String tag, boolean sealed) {
        ES6Iterator.init(scope, sealed, new NativeCollectionIterator(tag), tag);
    }

    public NativeCollectionIterator(String tag) {
        this.className = tag;
        this.iterator = Collections.emptyIterator();
        this.type = Type.BOTH;
    }

    public NativeCollectionIterator(Scriptable scope, String className, Type type, Iterator<Hashtable.Entry> iterator) {
        super(scope, className);
        this.className = className;
        this.iterator = iterator;
        this.type = type;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    protected boolean isDone(Context cx, Scriptable scope) {
        return !this.iterator.hasNext();
    }

    @Override
    protected Object nextValue(Context cx, Scriptable scope) {
        Hashtable.Entry e = this.iterator.next();
        switch (this.type) {
            case KEYS: {
                return e.key;
            }
            case VALUES: {
                return e.value;
            }
            case BOTH: {
                return cx.newArray(scope, new Object[]{e.key, e.value});
            }
        }
        throw new AssertionError();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.className = (String)stream.readObject();
        this.type = (Type)((Object)stream.readObject());
        this.iterator = Collections.emptyIterator();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(this.className);
        stream.writeObject((Object)this.type);
    }

    static enum Type {
        KEYS,
        VALUES,
        BOTH;

    }
}

