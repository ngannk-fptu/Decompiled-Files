/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.ringbuffer.impl.client;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.ringbuffer.ReadResultSet;
import com.hazelcast.ringbuffer.impl.client.RingbufferPortableHook;
import com.hazelcast.spi.serialization.SerializationService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PortableReadResultSet<E>
implements Portable,
ReadResultSet<E> {
    private transient long nextSeq;
    private transient long[] seqs;
    private List<Data> items;
    private int readCount;
    private SerializationService serializationService;

    public PortableReadResultSet() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP2"})
    public PortableReadResultSet(int readCount, List<Data> items, long[] seqs, long nextSeq) {
        this.readCount = readCount;
        this.items = items;
        this.seqs = seqs;
        this.nextSeq = nextSeq;
    }

    public List<Data> getDataItems() {
        return this.items;
    }

    public void setSerializationService(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public Iterator<E> iterator() {
        ArrayList result = new ArrayList(this.items.size());
        for (Data data : this.items) {
            result.add(this.serializationService.toObject(data));
        }
        return Collections.unmodifiableList(result).iterator();
    }

    @Override
    public int readCount() {
        return this.readCount;
    }

    @Override
    public E get(int index) {
        Data data = this.items.get(index);
        return (E)this.serializationService.toObject(data);
    }

    @Override
    public long getSequence(int index) {
        return this.seqs[index];
    }

    @Override
    public int size() {
        return this.items.size();
    }

    @Override
    public long getNextSequenceToReadFrom() {
        return this.nextSeq;
    }

    @Override
    public int getFactoryId() {
        return RingbufferPortableHook.F_ID;
    }

    @Override
    public int getClassId() {
        return 10;
    }

    @Override
    public void writePortable(PortableWriter writer) throws IOException {
        writer.writeInt("readCount", this.readCount);
        writer.writeInt("count", this.items.size());
        ObjectDataOutput rawDataOutput = writer.getRawDataOutput();
        for (Data item : this.items) {
            rawDataOutput.writeData(item);
        }
    }

    @Override
    public void readPortable(PortableReader reader) throws IOException {
        this.readCount = reader.readInt("readCount");
        int size = reader.readInt("count");
        this.items = new ArrayList<Data>(size);
        ObjectDataInput rawDataInput = reader.getRawDataInput();
        for (int k = 0; k < size; ++k) {
            Data item = rawDataInput.readData();
            this.items.add(item);
        }
    }
}

