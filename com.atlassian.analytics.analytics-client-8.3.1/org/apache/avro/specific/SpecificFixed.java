/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.specific;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.io.BinaryData;

public abstract class SpecificFixed
implements GenericFixed,
Comparable<SpecificFixed>,
Externalizable {
    private byte[] bytes;

    public SpecificFixed() {
        this.bytes(new byte[this.getSchema().getFixedSize()]);
    }

    public SpecificFixed(byte[] bytes) {
        this.bytes(bytes);
    }

    public void bytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public byte[] bytes() {
        return this.bytes;
    }

    @Override
    public abstract Schema getSchema();

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        return o instanceof GenericFixed && Arrays.equals(this.bytes, ((GenericFixed)o).bytes());
    }

    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    public String toString() {
        return Arrays.toString(this.bytes);
    }

    @Override
    public int compareTo(SpecificFixed that) {
        return BinaryData.compareBytes(this.bytes, 0, this.bytes.length, that.bytes, 0, that.bytes.length);
    }

    @Override
    public abstract void writeExternal(ObjectOutput var1) throws IOException;

    @Override
    public abstract void readExternal(ObjectInput var1) throws IOException;
}

