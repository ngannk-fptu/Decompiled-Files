/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.ObjectDataOutput
 *  com.hazelcast.nio.serialization.StreamSerializer
 *  org.hibernate.cache.spi.entry.CacheEntry
 */
package com.hazelcast.hibernate.serialization;

import com.hazelcast.hibernate.serialization.CacheEntryImpl;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import java.io.IOException;
import java.io.Serializable;
import org.hibernate.cache.spi.entry.CacheEntry;

class Hibernate53CacheEntrySerializer
implements StreamSerializer<CacheEntry> {
    Hibernate53CacheEntrySerializer() {
    }

    public int getTypeId() {
        return -205;
    }

    public void destroy() {
    }

    public CacheEntry read(ObjectDataInput in) throws IOException {
        try {
            if (in.readBoolean()) {
                return Hibernate53CacheEntrySerializer.readReference(in);
            }
            return Hibernate53CacheEntrySerializer.readDisassembled(in);
        }
        catch (Exception e) {
            throw Hibernate53CacheEntrySerializer.rethrow(e);
        }
    }

    public void write(ObjectDataOutput out, CacheEntry object) throws IOException {
        try {
            out.writeBoolean(object.isReferenceEntry());
            if (object.isReferenceEntry()) {
                Hibernate53CacheEntrySerializer.writeReference(out, object);
            } else {
                Hibernate53CacheEntrySerializer.writeDisassembled(out, object);
            }
        }
        catch (Exception e) {
            throw Hibernate53CacheEntrySerializer.rethrow(e);
        }
    }

    private static CacheEntry readDisassembled(ObjectDataInput in) throws IOException {
        int length = in.readInt();
        Serializable[] disassembledState = new Serializable[length];
        for (int i = 0; i < length; ++i) {
            disassembledState[i] = (Serializable)in.readObject();
        }
        String subclass = in.readUTF();
        Object version = in.readObject();
        return new CacheEntryImpl(disassembledState, subclass, version);
    }

    private static CacheEntry readReference(ObjectDataInput in) throws IOException {
        return ((CacheEntryWrapper)in.readObject()).entry;
    }

    private static IOException rethrow(Exception e) throws IOException {
        if (e instanceof IOException) {
            throw (IOException)e;
        }
        throw new IOException(e);
    }

    private static void writeDisassembled(ObjectDataOutput out, CacheEntry object) throws IOException {
        Serializable[] disassembledState = object.getDisassembledState();
        out.writeInt(disassembledState.length);
        for (Serializable state : disassembledState) {
            out.writeObject((Object)state);
        }
        out.writeUTF(object.getSubclass());
        out.writeObject(object.getVersion());
    }

    private static void writeReference(ObjectDataOutput out, CacheEntry object) throws IOException {
        out.writeObject((Object)new CacheEntryWrapper(object));
    }

    private static final class CacheEntryWrapper
    implements Serializable {
        private final CacheEntry entry;

        private CacheEntryWrapper(CacheEntry entry) {
            this.entry = entry;
        }
    }
}

