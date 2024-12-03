/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.map.AbstractInputCheckedMapDecorator;

public class PredicatedMap
extends AbstractInputCheckedMapDecorator
implements Serializable {
    private static final long serialVersionUID = 7412622456128415156L;
    protected final Predicate keyPredicate;
    protected final Predicate valuePredicate;

    public static Map decorate(Map map, Predicate keyPredicate, Predicate valuePredicate) {
        return new PredicatedMap(map, keyPredicate, valuePredicate);
    }

    protected PredicatedMap(Map map, Predicate keyPredicate, Predicate valuePredicate) {
        super(map);
        this.keyPredicate = keyPredicate;
        this.valuePredicate = valuePredicate;
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            this.validate(key, value);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.map = (Map)in.readObject();
    }

    protected void validate(Object key, Object value) {
        if (this.keyPredicate != null && !this.keyPredicate.evaluate(key)) {
            throw new IllegalArgumentException("Cannot add key - Predicate rejected it");
        }
        if (this.valuePredicate != null && !this.valuePredicate.evaluate(value)) {
            throw new IllegalArgumentException("Cannot add value - Predicate rejected it");
        }
    }

    protected Object checkSetValue(Object value) {
        if (!this.valuePredicate.evaluate(value)) {
            throw new IllegalArgumentException("Cannot set value - Predicate rejected it");
        }
        return value;
    }

    protected boolean isSetValueChecking() {
        return this.valuePredicate != null;
    }

    public Object put(Object key, Object value) {
        this.validate(key, value);
        return this.map.put(key, value);
    }

    public void putAll(Map mapToCopy) {
        Iterator it = mapToCopy.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            this.validate(key, value);
        }
        this.map.putAll(mapToCopy);
    }
}

