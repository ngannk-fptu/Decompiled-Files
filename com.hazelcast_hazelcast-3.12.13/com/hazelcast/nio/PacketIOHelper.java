/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.nio.Packet;
import com.hazelcast.spi.annotation.PrivateApi;
import java.nio.ByteBuffer;

@PrivateApi
public class PacketIOHelper {
    static final int HEADER_SIZE = 11;
    private int valueOffset;
    private int size;
    private boolean headerComplete;
    private char flags;
    private int partitionId;
    private byte[] payload;

    public boolean writeTo(Packet packet, ByteBuffer dst) {
        if (!this.headerComplete) {
            if (dst.remaining() < 11) {
                return false;
            }
            dst.put((byte)4);
            dst.putChar(packet.getFlags());
            dst.putInt(packet.getPartitionId());
            this.size = packet.totalSize();
            dst.putInt(this.size);
            this.headerComplete = true;
        }
        if (this.writeValue(packet, dst)) {
            this.reset();
            return true;
        }
        return false;
    }

    private boolean writeValue(Packet packet, ByteBuffer dst) {
        if (this.size > 0) {
            boolean done;
            int bytesWrite;
            int bytesNeeded;
            int bytesWritable = dst.remaining();
            if (bytesWritable >= (bytesNeeded = this.size - this.valueOffset)) {
                bytesWrite = bytesNeeded;
                done = true;
            } else {
                bytesWrite = bytesWritable;
                done = false;
            }
            byte[] byteArray = packet.toByteArray();
            dst.put(byteArray, this.valueOffset, bytesWrite);
            this.valueOffset += bytesWrite;
            if (!done) {
                return false;
            }
        }
        return true;
    }

    public Packet readFrom(ByteBuffer src) {
        if (!this.headerComplete) {
            if (src.remaining() < 11) {
                return null;
            }
            byte version = src.get();
            if (4 != version) {
                throw new IllegalArgumentException("Packet versions are not matching! Expected -> 4, Incoming -> " + version);
            }
            this.flags = src.getChar();
            this.partitionId = src.getInt();
            this.size = src.getInt();
            this.headerComplete = true;
        }
        if (this.readValue(src)) {
            Packet packet = new Packet(this.payload, this.partitionId).resetFlagsTo(this.flags);
            this.reset();
            return packet;
        }
        return null;
    }

    private void reset() {
        this.headerComplete = false;
        this.payload = null;
        this.valueOffset = 0;
    }

    private boolean readValue(ByteBuffer src) {
        if (this.payload == null) {
            this.payload = new byte[this.size];
        }
        if (this.size > 0) {
            boolean done;
            int bytesRead;
            int bytesNeeded;
            int bytesReadable = src.remaining();
            if (bytesReadable >= (bytesNeeded = this.size - this.valueOffset)) {
                bytesRead = bytesNeeded;
                done = true;
            } else {
                bytesRead = bytesReadable;
                done = false;
            }
            src.get(this.payload, this.valueOffset, bytesRead);
            this.valueOffset += bytesRead;
            if (!done) {
                return false;
            }
        }
        return true;
    }
}

