/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.query.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.impl.AbstractIndex;
import com.hazelcast.query.impl.ComparableIdentifiedDataSerializable;
import com.hazelcast.query.impl.Comparables;
import com.hazelcast.query.impl.predicates.PredicateDataSerializerHook;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.Arrays;

public final class CompositeValue
implements Comparable<CompositeValue>,
IdentifiedDataSerializable {
    public static final ComparableIdentifiedDataSerializable NEGATIVE_INFINITY = new NegativeInfinity();
    public static final ComparableIdentifiedDataSerializable POSITIVE_INFINITY = new PositiveInfinity();
    private static final int BYTE_MASK = 255;
    private Comparable[] components;

    public CompositeValue() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP2"})
    public CompositeValue(Comparable[] components) {
        this.components = components;
    }

    public CompositeValue(int width, Comparable prefix, Comparable filler) {
        assert (width > 0);
        Object[] components = new Comparable[width];
        components[0] = prefix;
        Arrays.fill(components, 1, components.length, filler);
        this.components = components;
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public Comparable[] getComponents() {
        return this.components;
    }

    public int hashCode() {
        return Arrays.hashCode(this.components);
    }

    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CompositeValue that = (CompositeValue)o;
        return Arrays.equals(this.components, that.components);
    }

    public String toString() {
        return Arrays.toString(this.components);
    }

    @Override
    @SuppressFBWarnings(value={"EQ_COMPARETO_USE_OBJECT_EQUALS"})
    public int compareTo(CompositeValue that) {
        assert (this.components.length == that.components.length);
        for (int i = 0; i < this.components.length; ++i) {
            int order = CompositeValue.compareComponent(this.components[i], that.components[i]);
            if (order == 0) continue;
            return order;
        }
        return 0;
    }

    @Override
    public int getFactoryId() {
        return PredicateDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 18;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeByte(this.components.length);
        for (Comparable component : this.components) {
            out.writeObject(component);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int length = in.readByte() & 0xFF;
        this.components = new Comparable[length];
        for (int i = 0; i < length; ++i) {
            this.components[i] = (Comparable)in.readObject();
        }
    }

    private static int compareComponent(Comparable lhs, Comparable rhs) {
        if (rhs == AbstractIndex.NULL || rhs == NEGATIVE_INFINITY || rhs == POSITIVE_INFINITY) {
            return -Integer.signum(rhs.compareTo(lhs));
        }
        return Comparables.compare(lhs, rhs);
    }

    private static final class PositiveInfinity
    implements ComparableIdentifiedDataSerializable {
        private PositiveInfinity() {
        }

        @Override
        public int getFactoryId() {
            return PredicateDataSerializerHook.F_ID;
        }

        @Override
        public int getId() {
            return 20;
        }

        @Override
        public void writeData(ObjectDataOutput out) {
        }

        @Override
        public void readData(ObjectDataInput in) {
        }

        @SuppressFBWarnings(value={"EQ_COMPARETO_USE_OBJECT_EQUALS"})
        public int compareTo(Object o) {
            return o == this ? 0 : 1;
        }

        public String toString() {
            return "+INF";
        }
    }

    private static final class NegativeInfinity
    implements ComparableIdentifiedDataSerializable {
        private NegativeInfinity() {
        }

        @Override
        public int getFactoryId() {
            return PredicateDataSerializerHook.F_ID;
        }

        @Override
        public int getId() {
            return 19;
        }

        @Override
        public void writeData(ObjectDataOutput out) {
        }

        @Override
        public void readData(ObjectDataInput in) {
        }

        @SuppressFBWarnings(value={"EQ_COMPARETO_USE_OBJECT_EQUALS"})
        public int compareTo(Object o) {
            return o == this ? 0 : -1;
        }

        public String toString() {
            return "-INF";
        }
    }
}

