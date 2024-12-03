/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl.operations;

import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.ringbuffer.impl.ArrayRingbuffer;
import com.hazelcast.ringbuffer.impl.Ringbuffer;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.RingbufferService;
import com.hazelcast.ringbuffer.impl.operations.AbstractRingBufferOperation;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class MergeBackupOperation
extends AbstractRingBufferOperation
implements BackupOperation {
    private Ringbuffer<Object> ringbuffer;

    public MergeBackupOperation() {
    }

    MergeBackupOperation(String name, Ringbuffer<Object> ringbuffer) {
        super(name);
        this.ringbuffer = ringbuffer;
    }

    @Override
    public void run() throws Exception {
        RingbufferService service = (RingbufferService)this.getService();
        if (this.ringbuffer == null) {
            service.destroyDistributedObject(this.name);
        } else {
            RingbufferContainer existingContainer = this.getRingBufferContainer();
            existingContainer.setHeadSequence(this.ringbuffer.headSequence());
            existingContainer.setTailSequence(this.ringbuffer.tailSequence());
            for (long seq = this.ringbuffer.headSequence(); seq <= this.ringbuffer.tailSequence(); ++seq) {
                existingContainer.set(seq, this.ringbuffer.read(seq));
            }
        }
    }

    @Override
    public int getId() {
        return 12;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.ringbuffer != null ? (int)this.ringbuffer.getCapacity() : 0);
        if (this.ringbuffer != null) {
            out.writeLong(this.ringbuffer.tailSequence());
            out.writeLong(this.ringbuffer.headSequence());
            for (Object t : this.ringbuffer) {
                IOUtil.writeObject(out, t);
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int capacity = in.readInt();
        if (capacity > 0) {
            long tailSequence = in.readLong();
            long headSequence = in.readLong();
            this.ringbuffer = new ArrayRingbuffer<Object>(capacity);
            this.ringbuffer.setTailSequence(tailSequence);
            this.ringbuffer.setHeadSequence(headSequence);
            for (long seq = headSequence; seq <= tailSequence; ++seq) {
                this.ringbuffer.set(seq, IOUtil.readObject(in));
            }
        }
    }
}

