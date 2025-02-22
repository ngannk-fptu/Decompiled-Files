/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.Predicate;
import java.io.IOException;
import java.util.Map;

@BinaryInterface
public class InstanceOfPredicate
implements Predicate,
IdentifiedDataSerializable {
    private static final long serialVersionUID = 1L;
    private Class klass;

    public InstanceOfPredicate(Class klass) {
        this.klass = klass;
    }

    public InstanceOfPredicate() {
    }

    public boolean apply(Map.Entry mapEntry) {
        Object value = mapEntry.getValue();
        if (value == null) {
            return false;
        }
        return this.klass.isAssignableFrom(value.getClass());
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.klass.getName());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        String klassName = in.readUTF();
        try {
            this.klass = ClassLoaderUtil.loadClass(in.getClassLoader(), klassName);
        }
        catch (ClassNotFoundException e) {
            throw new HazelcastSerializationException("Failed to load class: " + this.klass, e);
        }
    }

    public String toString() {
        return " instanceOf (" + this.klass.getName() + ")";
    }

    @Override
    public int getFactoryId() {
        return -32;
    }

    @Override
    public int getId() {
        return 8;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof InstanceOfPredicate)) {
            return false;
        }
        InstanceOfPredicate that = (InstanceOfPredicate)o;
        return this.klass != null ? this.klass.equals(that.klass) : that.klass == null;
    }

    public int hashCode() {
        return this.klass != null ? this.klass.hashCode() : 0;
    }
}

