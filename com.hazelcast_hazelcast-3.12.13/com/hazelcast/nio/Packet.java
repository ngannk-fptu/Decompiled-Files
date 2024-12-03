/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.internal.networking.OutboundFrame;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.annotation.PrivateApi;

@PrivateApi
public final class Packet
extends HeapData
implements OutboundFrame {
    public static final byte VERSION = 4;
    public static final int FLAG_URGENT = 16;
    private static final int FLAG_TYPE0 = 1;
    private static final int FLAG_TYPE1 = 4;
    private static final int FLAG_TYPE2 = 32;
    public static final int FLAG_OP_RESPONSE = 2;
    public static final int FLAG_OP_CONTROL = 64;
    public static final int FLAG_JET_FLOW_CONTROL = 2;
    private char flags;
    private int partitionId;
    private transient Connection conn;

    public Packet() {
    }

    public Packet(byte[] payload) {
        this(payload, -1);
    }

    public Packet(byte[] payload, int partitionId) {
        super(payload);
        this.partitionId = partitionId;
    }

    public Connection getConn() {
        return this.conn;
    }

    public Packet setConn(Connection conn) {
        this.conn = conn;
        return this;
    }

    public Type getPacketType() {
        return Type.fromFlags(this.flags);
    }

    public Packet setPacketType(Type type) {
        int nonTypeFlags = this.flags & 0xFFFFFFDA;
        this.resetFlagsTo(type.headerEncoding | nonTypeFlags);
        return this;
    }

    public Packet raiseFlags(int flagsToRaise) {
        this.flags = (char)(this.flags | flagsToRaise);
        return this;
    }

    public Packet resetFlagsTo(int flagsToSet) {
        this.flags = (char)flagsToSet;
        return this;
    }

    public boolean isFlagRaised(int flagsToCheck) {
        return Packet.isFlagRaised(this.flags, flagsToCheck);
    }

    private static boolean isFlagRaised(char flags, int flagsToCheck) {
        return (flags & flagsToCheck) != 0;
    }

    public char getFlags() {
        return this.flags;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public boolean isUrgent() {
        return this.isFlagRaised(16);
    }

    @Override
    public int getFrameLength() {
        return (this.payload != null ? this.totalSize() : 0) + 11;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Packet)) {
            return false;
        }
        Packet packet = (Packet)o;
        if (!super.equals(packet)) {
            return false;
        }
        if (this.flags != packet.flags) {
            return false;
        }
        return this.partitionId == packet.partitionId;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.flags;
        result = 31 * result + this.partitionId;
        return result;
    }

    @Override
    public String toString() {
        Type type = this.getPacketType();
        return "Packet{partitionId=" + this.partitionId + ", frameLength=" + this.getFrameLength() + ", conn=" + this.conn + ", rawFlags=" + Integer.toBinaryString(this.flags) + ", isUrgent=" + this.isUrgent() + ", packetType=" + type.name() + ", typeSpecificFlags=" + type.describeFlags(this.flags) + '}';
    }

    public static class Type
    extends Enum<Type> {
        public static final /* enum */ Type NULL = new Type();
        public static final /* enum */ Type OPERATION = new Type(){

            @Override
            public String describeFlags(char flags) {
                return "[isResponse=" + Packet.isFlagRaised(flags, 2) + ", isOpControl=" + Packet.isFlagRaised(flags, 64) + ']';
            }
        };
        public static final /* enum */ Type EVENT = new Type();
        public static final /* enum */ Type JET = new Type(){

            @Override
            public String describeFlags(char flags) {
                return "[isFlowControl=" + Packet.isFlagRaised(flags, 2) + ']';
            }
        };
        public static final /* enum */ Type BIND = new Type();
        public static final /* enum */ Type EXTENDED_BIND = new Type();
        public static final /* enum */ Type UNDEFINED6 = new Type();
        public static final /* enum */ Type UNDEFINED7 = new Type();
        final char headerEncoding = (char)this.encodeOrdinal();
        private static final Type[] VALUES;
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String name) {
            return Enum.valueOf(Type.class, name);
        }

        public static Type fromFlags(int flags) {
            return VALUES[Type.headerDecode(flags)];
        }

        public String describeFlags(char flags) {
            return "<NONE>";
        }

        private int encodeOrdinal() {
            int ordinal = this.ordinal();
            assert (ordinal < 8) : "Ordinal out of range for member " + this.name() + ": " + ordinal;
            return ordinal & 1 | (ordinal & 2) << 1 | (ordinal & 4) << 3;
        }

        private static int headerDecode(int flags) {
            return flags & 1 | (flags & 4) >> 1 | (flags & 0x20) >> 3;
        }

        static {
            $VALUES = new Type[]{NULL, OPERATION, EVENT, JET, BIND, EXTENDED_BIND, UNDEFINED6, UNDEFINED7};
            VALUES = Type.values();
        }
    }
}

