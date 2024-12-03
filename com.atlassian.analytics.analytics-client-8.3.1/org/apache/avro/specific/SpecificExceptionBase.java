/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.specific;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificRecord;

public abstract class SpecificExceptionBase
extends AvroRemoteException
implements SpecificRecord,
Externalizable {
    public SpecificExceptionBase() {
    }

    public SpecificExceptionBase(Throwable value) {
        super(value);
    }

    public SpecificExceptionBase(Object value) {
        super(value);
    }

    public SpecificExceptionBase(Object value, Throwable cause) {
        super(value, cause);
    }

    @Override
    public abstract Schema getSchema();

    @Override
    public abstract Object get(int var1);

    @Override
    public abstract void put(int var1, Object var2);

    public boolean equals(Object that) {
        if (that == this) {
            return true;
        }
        if (!(that instanceof SpecificExceptionBase)) {
            return false;
        }
        if (this.getClass() != that.getClass()) {
            return false;
        }
        return this.getSpecificData().compare(this, that, this.getSchema()) == 0;
    }

    public int hashCode() {
        return SpecificData.get().hashCode(this, this.getSchema());
    }

    @Override
    public abstract void writeExternal(ObjectOutput var1) throws IOException;

    @Override
    public abstract void readExternal(ObjectInput var1) throws IOException;

    public SpecificData getSpecificData() {
        return SpecificData.get();
    }
}

