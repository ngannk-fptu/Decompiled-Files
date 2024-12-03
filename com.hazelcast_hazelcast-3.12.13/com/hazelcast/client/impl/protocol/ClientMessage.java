/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol;

import com.hazelcast.client.impl.protocol.exception.MaxMessageSizeExceeded;
import com.hazelcast.client.impl.protocol.util.BufferBuilder;
import com.hazelcast.client.impl.protocol.util.ClientProtocolBuffer;
import com.hazelcast.client.impl.protocol.util.MessageFlyweight;
import com.hazelcast.client.impl.protocol.util.SafeBuffer;
import com.hazelcast.client.impl.protocol.util.UnsafeBuffer;
import com.hazelcast.internal.networking.OutboundFrame;
import com.hazelcast.nio.Bits;
import com.hazelcast.nio.Connection;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ClientMessage
extends MessageFlyweight
implements OutboundFrame {
    public static final short VERSION = 1;
    public static final short BEGIN_FLAG = 128;
    public static final short END_FLAG = 64;
    public static final short BEGIN_AND_END_FLAGS = 192;
    public static final short LISTENER_EVENT_FLAG = 1;
    public static final int HEADER_SIZE;
    private static final String PROP_HAZELCAST_PROTOCOL_UNSAFE = "hazelcast.protocol.unsafe.enabled";
    private static final boolean USE_UNSAFE;
    private static final int FRAME_LENGTH_FIELD_OFFSET = 0;
    private static final int VERSION_FIELD_OFFSET = 4;
    private static final int FLAGS_FIELD_OFFSET = 5;
    private static final int TYPE_FIELD_OFFSET = 6;
    private static final int CORRELATION_ID_FIELD_OFFSET = 8;
    private static final int PARTITION_ID_FIELD_OFFSET = 16;
    private static final int DATA_OFFSET_FIELD_OFFSET = 20;
    private transient int writeOffset;
    private transient boolean isRetryable;
    private transient boolean acquiresResource;
    private transient String operationName;
    private Connection connection;

    protected ClientMessage() {
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    protected void wrapForEncode(ClientProtocolBuffer buffer, int offset) {
        this.ensureHeaderSize(offset, buffer.capacity());
        super.wrap(buffer.byteArray(), offset, USE_UNSAFE);
        this.setDataOffset(HEADER_SIZE);
        this.setFrameLength(HEADER_SIZE);
        this.index(this.getDataOffset());
        this.setPartitionId(-1);
    }

    private void ensureHeaderSize(int offset, int length) {
        if (length - offset < HEADER_SIZE) {
            throw new IndexOutOfBoundsException("ClientMessage buffer must contain at least " + HEADER_SIZE + " bytes! length: " + length + ", offset: " + offset);
        }
    }

    protected void wrapForDecode(ClientProtocolBuffer buffer, int offset) {
        this.ensureHeaderSize(offset, buffer.capacity());
        super.wrap(buffer.byteArray(), offset, USE_UNSAFE);
        this.index(this.getDataOffset());
    }

    public short getVersion() {
        return this.uint8Get(4);
    }

    public ClientMessage setVersion(short version) {
        this.uint8Put(4, version);
        return this;
    }

    public boolean isFlagSet(short flag) {
        int i = this.getFlags() & flag;
        return i == flag;
    }

    public short getFlags() {
        return this.uint8Get(5);
    }

    public ClientMessage addFlag(short flags) {
        this.uint8Put(5, (short)(this.getFlags() | flags));
        return this;
    }

    public int getMessageType() {
        return this.uint16Get(6);
    }

    public ClientMessage setMessageType(int type) {
        this.uint16Put(6, type);
        return this;
    }

    @Override
    public int getFrameLength() {
        return this.int32Get(0);
    }

    public ClientMessage setFrameLength(int length) {
        this.int32Set(0, length);
        return this;
    }

    public long getCorrelationId() {
        return this.int64Get(8);
    }

    public ClientMessage setCorrelationId(long correlationId) {
        this.int64Set(8, correlationId);
        return this;
    }

    public int getPartitionId() {
        return this.int32Get(16);
    }

    public ClientMessage setPartitionId(int partitionId) {
        this.int32Set(16, partitionId);
        return this;
    }

    public int getDataOffset() {
        return this.uint16Get(20);
    }

    public ClientMessage setDataOffset(int dataOffset) {
        this.uint16Put(20, dataOffset);
        return this;
    }

    public ClientMessage updateFrameLength() {
        this.setFrameLength(this.index());
        return this;
    }

    public boolean writeTo(ByteBuffer dst) {
        boolean done;
        int bytesWrite;
        int bytesNeeded;
        byte[] byteArray = this.buffer.byteArray();
        int size = this.getFrameLength();
        int bytesWritable = dst.remaining();
        if (bytesWritable >= (bytesNeeded = size - this.writeOffset)) {
            bytesWrite = bytesNeeded;
            done = true;
        } else {
            bytesWrite = bytesWritable;
            done = false;
        }
        dst.put(byteArray, this.writeOffset, bytesWrite);
        this.writeOffset += bytesWrite;
        if (done) {
            this.writeOffset = 0;
        }
        return done;
    }

    public boolean readFrom(ByteBuffer src) {
        int frameLength = 0;
        if (this.buffer == null) {
            int remaining = src.remaining();
            if (remaining < 4) {
                return false;
            }
            frameLength = Bits.readIntL(src);
            src.position(src.position() - 4);
            if (frameLength < HEADER_SIZE) {
                throw new IllegalArgumentException("Client message frame length cannot be smaller than header size.");
            }
            this.wrap(new byte[frameLength], 0, USE_UNSAFE);
        }
        frameLength = frameLength > 0 ? frameLength : this.getFrameLength();
        this.accumulate(src, frameLength - this.index());
        return this.isComplete();
    }

    private int accumulate(ByteBuffer src, int length) {
        int readLength;
        int remaining = src.remaining();
        int n = readLength = remaining < length ? remaining : length;
        if (readLength > 0) {
            this.buffer.putBytes(this.index(), src, readLength);
            this.index(this.index() + readLength);
            return readLength;
        }
        return 0;
    }

    public boolean isComplete() {
        return this.index() >= HEADER_SIZE && this.index() == this.getFrameLength();
    }

    @Override
    public boolean isUrgent() {
        return false;
    }

    public boolean isRetryable() {
        return this.isRetryable;
    }

    public boolean acquiresResource() {
        return this.acquiresResource;
    }

    public void setAcquiresResource(boolean acquiresResource) {
        this.acquiresResource = acquiresResource;
    }

    public void setRetryable(boolean isRetryable) {
        this.isRetryable = isRetryable;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getOperationName() {
        return this.operationName;
    }

    public String toString() {
        int len = this.index();
        StringBuilder sb = new StringBuilder("ClientMessage{");
        sb.append("connection=").append(this.connection);
        sb.append(", length=").append(len);
        if (len >= HEADER_SIZE) {
            sb.append(", correlationId=").append(this.getCorrelationId());
            sb.append(", operation=").append(this.operationName);
            sb.append(", messageType=").append(Integer.toHexString(this.getMessageType()));
            sb.append(", partitionId=").append(this.getPartitionId());
            sb.append(", isComplete=").append(this.isComplete());
            sb.append(", isRetryable=").append(this.isRetryable());
            sb.append(", isEvent=").append(this.isFlagSet((short)1));
            sb.append(", writeOffset=").append(this.writeOffset);
        }
        sb.append('}');
        return sb.toString();
    }

    public static ClientMessage create() {
        return new ClientMessage();
    }

    public static ClientMessage createForEncode(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new MaxMessageSizeExceeded();
        }
        if (USE_UNSAFE) {
            return ClientMessage.createForEncode(new UnsafeBuffer(new byte[initialCapacity]), 0);
        }
        return ClientMessage.createForEncode(new SafeBuffer(new byte[initialCapacity]), 0);
    }

    public static ClientMessage createForEncode(ClientProtocolBuffer buffer, int offset) {
        ClientMessage clientMessage = new ClientMessage();
        clientMessage.wrapForEncode(buffer, offset);
        return clientMessage;
    }

    public static ClientMessage createForDecode(ClientProtocolBuffer buffer, int offset) {
        ClientMessage clientMessage = new ClientMessage();
        clientMessage.wrapForDecode(buffer, offset);
        return clientMessage;
    }

    public ClientMessage copy() {
        byte[] oldBinary = this.buffer().byteArray();
        byte[] bytes = Arrays.copyOf(oldBinary, oldBinary.length);
        ClientMessage newMessage = ClientMessage.createForDecode(BufferBuilder.createBuffer(bytes), 0);
        newMessage.isRetryable = this.isRetryable;
        newMessage.acquiresResource = this.acquiresResource;
        newMessage.operationName = this.operationName;
        return newMessage;
    }

    public int hashCode() {
        return ByteBuffer.wrap(this.buffer().byteArray(), 0, this.getFrameLength()).hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClientMessage that = (ClientMessage)o;
        byte[] thisBytes = this.buffer().byteArray();
        byte[] thatBytes = that.buffer().byteArray();
        if (this.getFrameLength() != that.getFrameLength()) {
            return false;
        }
        for (int i = 0; i < this.getFrameLength(); ++i) {
            if (thisBytes[i] == thatBytes[i]) continue;
            return false;
        }
        return true;
    }

    static {
        USE_UNSAFE = Boolean.getBoolean(PROP_HAZELCAST_PROTOCOL_UNSAFE);
        HEADER_SIZE = 22;
    }
}

