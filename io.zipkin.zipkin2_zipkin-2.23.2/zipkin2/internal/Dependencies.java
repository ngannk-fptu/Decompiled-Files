/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import zipkin2.DependencyLink;
import zipkin2.internal.ReadBuffer;
import zipkin2.internal.ThriftCodec;
import zipkin2.internal.ThriftField;
import zipkin2.internal.WriteBuffer;

public final class Dependencies {
    static final ThriftField START_TS = new ThriftField(10, 1);
    static final ThriftField END_TS = new ThriftField(10, 2);
    static final ThriftField LINKS = new ThriftField(15, 3);
    static final DependencyLinkAdapter DEPENDENCY_LINK_ADAPTER = new DependencyLinkAdapter();
    final long startTs;
    final long endTs;
    final List<DependencyLink> links;

    public List<DependencyLink> links() {
        return this.links;
    }

    public static Dependencies fromThrift(ByteBuffer bytes) {
        long startTs = 0L;
        long endTs = 0L;
        List<DependencyLink> links = Collections.emptyList();
        ReadBuffer buffer = ReadBuffer.wrapUnsafe(bytes);
        block0: while (true) {
            ThriftField thriftField = ThriftField.read(buffer);
            if (thriftField.type == 0) break;
            if (thriftField.isEqualTo(START_TS)) {
                startTs = buffer.readLong();
                continue;
            }
            if (thriftField.isEqualTo(END_TS)) {
                endTs = buffer.readLong();
                continue;
            }
            if (thriftField.isEqualTo(LINKS)) {
                int length = ThriftCodec.readListLength(buffer);
                if (length == 0) continue;
                links = new ArrayList<DependencyLink>(length);
                int i = 0;
                while (true) {
                    if (i >= length) continue block0;
                    links.add(DependencyLinkAdapter.read(buffer));
                    ++i;
                }
            }
            ThriftCodec.skip(buffer, thriftField.type);
        }
        return Dependencies.create(startTs, endTs, links);
    }

    public ByteBuffer toThrift() {
        byte[] result = new byte[this.sizeInBytes()];
        this.write(WriteBuffer.wrap(result));
        return ByteBuffer.wrap(result);
    }

    int sizeInBytes() {
        int sizeInBytes = 0;
        sizeInBytes += 11;
        sizeInBytes += 11;
        sizeInBytes += 3 + ThriftCodec.listSizeInBytes(DEPENDENCY_LINK_ADAPTER, this.links);
        return ++sizeInBytes;
    }

    void write(WriteBuffer buffer) {
        START_TS.write(buffer);
        ThriftCodec.writeLong(buffer, this.startTs);
        END_TS.write(buffer);
        ThriftCodec.writeLong(buffer, this.endTs);
        LINKS.write(buffer);
        ThriftCodec.writeList(DEPENDENCY_LINK_ADAPTER, this.links, buffer);
        buffer.writeByte(0);
    }

    public static Dependencies create(long startTs, long endTs, List<DependencyLink> links) {
        return new Dependencies(startTs, endTs, links);
    }

    Dependencies(long startTs, long endTs, List<DependencyLink> links) {
        this.startTs = startTs;
        this.endTs = endTs;
        if (links == null) {
            throw new NullPointerException("links == null");
        }
        this.links = links;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Dependencies)) {
            return false;
        }
        Dependencies that = (Dependencies)o;
        return this.startTs == that.startTs && this.endTs == that.endTs && this.links.equals(that.links);
    }

    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= (int)((long)h ^ (this.startTs >>> 32 ^ this.startTs));
        h *= 1000003;
        h ^= (int)((long)h ^ (this.endTs >>> 32 ^ this.endTs));
        h *= 1000003;
        return h ^= this.links.hashCode();
    }

    static final class DependencyLinkAdapter
    implements WriteBuffer.Writer<DependencyLink> {
        static final ThriftField PARENT = new ThriftField(11, 1);
        static final ThriftField CHILD = new ThriftField(11, 2);
        static final ThriftField CALL_COUNT = new ThriftField(10, 4);
        static final ThriftField ERROR_COUNT = new ThriftField(10, 5);

        DependencyLinkAdapter() {
        }

        static DependencyLink read(ReadBuffer buffer) {
            DependencyLink.Builder result = DependencyLink.newBuilder();
            while (true) {
                ThriftField thriftField = ThriftField.read(buffer);
                if (thriftField.type == 0) break;
                if (thriftField.isEqualTo(PARENT)) {
                    result.parent(buffer.readUtf8(buffer.readInt()));
                    continue;
                }
                if (thriftField.isEqualTo(CHILD)) {
                    result.child(buffer.readUtf8(buffer.readInt()));
                    continue;
                }
                if (thriftField.isEqualTo(CALL_COUNT)) {
                    result.callCount(buffer.readLong());
                    continue;
                }
                if (thriftField.isEqualTo(ERROR_COUNT)) {
                    result.errorCount(buffer.readLong());
                    continue;
                }
                ThriftCodec.skip(buffer, thriftField.type);
            }
            return result.build();
        }

        @Override
        public int sizeInBytes(DependencyLink value) {
            int sizeInBytes = 0;
            sizeInBytes += 7 + WriteBuffer.utf8SizeInBytes(value.parent());
            sizeInBytes += 7 + WriteBuffer.utf8SizeInBytes(value.child());
            sizeInBytes += 11;
            if (value.errorCount() > 0L) {
                sizeInBytes += 11;
            }
            return ++sizeInBytes;
        }

        @Override
        public void write(DependencyLink value, WriteBuffer buffer) {
            PARENT.write(buffer);
            ThriftCodec.writeLengthPrefixed(buffer, value.parent());
            CHILD.write(buffer);
            ThriftCodec.writeLengthPrefixed(buffer, value.child());
            CALL_COUNT.write(buffer);
            ThriftCodec.writeLong(buffer, value.callCount());
            if (value.errorCount() > 0L) {
                ERROR_COUNT.write(buffer);
                ThriftCodec.writeLong(buffer, value.errorCount());
            }
            buffer.writeByte(0);
        }
    }
}

