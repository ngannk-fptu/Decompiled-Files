/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.ByteProcessor
 *  io.netty.util.IllegalReferenceCountException
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.internal.EmptyArrays
 *  io.netty.util.internal.ObjectUtil
 *  io.netty.util.internal.RecyclableArrayList
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.AbstractUnpooledSlicedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.DuplicatedByteBuf;
import io.netty.buffer.PooledDuplicatedByteBuf;
import io.netty.buffer.PooledSlicedByteBuf;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.WrappedByteBuf;
import io.netty.buffer.WrappedCompositeByteBuf;
import io.netty.util.ByteProcessor;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.RecyclableArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class CompositeByteBuf
extends AbstractReferenceCountedByteBuf
implements Iterable<ByteBuf> {
    private static final ByteBuffer EMPTY_NIO_BUFFER = Unpooled.EMPTY_BUFFER.nioBuffer();
    private static final Iterator<ByteBuf> EMPTY_ITERATOR = Collections.emptyList().iterator();
    private final ByteBufAllocator alloc;
    private final boolean direct;
    private final int maxNumComponents;
    private int componentCount;
    private Component[] components;
    private boolean freed;
    static final ByteWrapper<byte[]> BYTE_ARRAY_WRAPPER = new ByteWrapper<byte[]>(){

        @Override
        public ByteBuf wrap(byte[] bytes) {
            return Unpooled.wrappedBuffer(bytes);
        }

        @Override
        public boolean isEmpty(byte[] bytes) {
            return bytes.length == 0;
        }
    };
    static final ByteWrapper<ByteBuffer> BYTE_BUFFER_WRAPPER = new ByteWrapper<ByteBuffer>(){

        @Override
        public ByteBuf wrap(ByteBuffer bytes) {
            return Unpooled.wrappedBuffer(bytes);
        }

        @Override
        public boolean isEmpty(ByteBuffer bytes) {
            return !bytes.hasRemaining();
        }
    };
    private Component lastAccessed;

    private CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, int initSize) {
        super(Integer.MAX_VALUE);
        this.alloc = (ByteBufAllocator)ObjectUtil.checkNotNull((Object)alloc, (String)"alloc");
        if (maxNumComponents < 1) {
            throw new IllegalArgumentException("maxNumComponents: " + maxNumComponents + " (expected: >= 1)");
        }
        this.direct = direct;
        this.maxNumComponents = maxNumComponents;
        this.components = CompositeByteBuf.newCompArray(initSize, maxNumComponents);
    }

    public CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents) {
        this(alloc, direct, maxNumComponents, 0);
    }

    public CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, ByteBuf ... buffers) {
        this(alloc, direct, maxNumComponents, buffers, 0);
    }

    CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, ByteBuf[] buffers, int offset) {
        this(alloc, direct, maxNumComponents, buffers.length - offset);
        this.addComponents0(false, 0, buffers, offset);
        this.consolidateIfNeeded();
        this.setIndex0(0, this.capacity());
    }

    public CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, Iterable<ByteBuf> buffers) {
        this(alloc, direct, maxNumComponents, buffers instanceof Collection ? ((Collection)buffers).size() : 0);
        this.addComponents(false, 0, buffers);
        this.setIndex(0, this.capacity());
    }

    <T> CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, ByteWrapper<T> wrapper, T[] buffers, int offset) {
        this(alloc, direct, maxNumComponents, buffers.length - offset);
        this.addComponents0(false, 0, wrapper, buffers, offset);
        this.consolidateIfNeeded();
        this.setIndex(0, this.capacity());
    }

    private static Component[] newCompArray(int initComponents, int maxNumComponents) {
        int capacityGuess = Math.min(16, maxNumComponents);
        return new Component[Math.max(initComponents, capacityGuess)];
    }

    CompositeByteBuf(ByteBufAllocator alloc) {
        super(Integer.MAX_VALUE);
        this.alloc = alloc;
        this.direct = false;
        this.maxNumComponents = 0;
        this.components = null;
    }

    public CompositeByteBuf addComponent(ByteBuf buffer) {
        return this.addComponent(false, buffer);
    }

    public CompositeByteBuf addComponents(ByteBuf ... buffers) {
        return this.addComponents(false, buffers);
    }

    public CompositeByteBuf addComponents(Iterable<ByteBuf> buffers) {
        return this.addComponents(false, buffers);
    }

    public CompositeByteBuf addComponent(int cIndex, ByteBuf buffer) {
        return this.addComponent(false, cIndex, buffer);
    }

    public CompositeByteBuf addComponent(boolean increaseWriterIndex, ByteBuf buffer) {
        return this.addComponent(increaseWriterIndex, this.componentCount, buffer);
    }

    public CompositeByteBuf addComponents(boolean increaseWriterIndex, ByteBuf ... buffers) {
        ObjectUtil.checkNotNull((Object)buffers, (String)"buffers");
        this.addComponents0(increaseWriterIndex, this.componentCount, buffers, 0);
        this.consolidateIfNeeded();
        return this;
    }

    public CompositeByteBuf addComponents(boolean increaseWriterIndex, Iterable<ByteBuf> buffers) {
        return this.addComponents(increaseWriterIndex, this.componentCount, buffers);
    }

    public CompositeByteBuf addComponent(boolean increaseWriterIndex, int cIndex, ByteBuf buffer) {
        ObjectUtil.checkNotNull((Object)buffer, (String)"buffer");
        this.addComponent0(increaseWriterIndex, cIndex, buffer);
        this.consolidateIfNeeded();
        return this;
    }

    private static void checkForOverflow(int capacity, int readableBytes) {
        if (capacity + readableBytes < 0) {
            throw new IllegalArgumentException("Can't increase by " + readableBytes + " as capacity(" + capacity + ") would overflow " + Integer.MAX_VALUE);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int addComponent0(boolean increaseWriterIndex, int cIndex, ByteBuf buffer) {
        assert (buffer != null);
        boolean wasAdded = false;
        try {
            this.checkComponentIndex(cIndex);
            Component c = this.newComponent(CompositeByteBuf.ensureAccessible(buffer), 0);
            int readableBytes = c.length();
            CompositeByteBuf.checkForOverflow(this.capacity(), readableBytes);
            this.addComp(cIndex, c);
            wasAdded = true;
            if (readableBytes > 0 && cIndex < this.componentCount - 1) {
                this.updateComponentOffsets(cIndex);
            } else if (cIndex > 0) {
                c.reposition(this.components[cIndex - 1].endOffset);
            }
            if (increaseWriterIndex) {
                this.writerIndex += readableBytes;
            }
            int n = cIndex;
            return n;
        }
        finally {
            if (!wasAdded) {
                buffer.release();
            }
        }
    }

    private static ByteBuf ensureAccessible(ByteBuf buf) {
        if (checkAccessible && !buf.isAccessible()) {
            throw new IllegalReferenceCountException(0);
        }
        return buf;
    }

    private Component newComponent(ByteBuf buf, int offset) {
        int srcIndex = buf.readerIndex();
        int len = buf.readableBytes();
        ByteBuf unwrapped = buf;
        int unwrappedIndex = srcIndex;
        while (unwrapped instanceof WrappedByteBuf || unwrapped instanceof SwappedByteBuf) {
            unwrapped = unwrapped.unwrap();
        }
        if (unwrapped instanceof AbstractUnpooledSlicedByteBuf) {
            unwrappedIndex += ((AbstractUnpooledSlicedByteBuf)unwrapped).idx(0);
            unwrapped = unwrapped.unwrap();
        } else if (unwrapped instanceof PooledSlicedByteBuf) {
            unwrappedIndex += ((PooledSlicedByteBuf)unwrapped).adjustment;
            unwrapped = unwrapped.unwrap();
        } else if (unwrapped instanceof DuplicatedByteBuf || unwrapped instanceof PooledDuplicatedByteBuf) {
            unwrapped = unwrapped.unwrap();
        }
        ByteBuf slice = buf.capacity() == len ? buf : null;
        return new Component(buf.order(ByteOrder.BIG_ENDIAN), srcIndex, unwrapped.order(ByteOrder.BIG_ENDIAN), unwrappedIndex, offset, len, slice);
    }

    public CompositeByteBuf addComponents(int cIndex, ByteBuf ... buffers) {
        ObjectUtil.checkNotNull((Object)buffers, (String)"buffers");
        this.addComponents0(false, cIndex, buffers, 0);
        this.consolidateIfNeeded();
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CompositeByteBuf addComponents0(boolean increaseWriterIndex, int cIndex, ByteBuf[] buffers, int arrOffset) {
        ByteBuf b;
        int len = buffers.length;
        int count = len - arrOffset;
        int readableBytes = 0;
        int capacity = this.capacity();
        for (int i = arrOffset; i < buffers.length && (b = buffers[i]) != null; ++i) {
            CompositeByteBuf.checkForOverflow(capacity, readableBytes += b.readableBytes());
        }
        int ci = Integer.MAX_VALUE;
        try {
            ByteBuf b2;
            this.checkComponentIndex(cIndex);
            this.shiftComps(cIndex, count);
            int nextOffset = cIndex > 0 ? this.components[cIndex - 1].endOffset : 0;
            ci = cIndex;
            while (arrOffset < len && (b2 = buffers[arrOffset]) != null) {
                Component c;
                this.components[ci] = c = this.newComponent(CompositeByteBuf.ensureAccessible(b2), nextOffset);
                nextOffset = c.endOffset;
                ++arrOffset;
                ++ci;
            }
            CompositeByteBuf compositeByteBuf = this;
            return compositeByteBuf;
        }
        finally {
            if (ci < this.componentCount) {
                if (ci < cIndex + count) {
                    this.removeCompRange(ci, cIndex + count);
                    while (arrOffset < len) {
                        ReferenceCountUtil.safeRelease((Object)buffers[arrOffset]);
                        ++arrOffset;
                    }
                }
                this.updateComponentOffsets(ci);
            }
            if (increaseWriterIndex && ci > cIndex && ci <= this.componentCount) {
                this.writerIndex += this.components[ci - 1].endOffset - this.components[cIndex].offset;
            }
        }
    }

    private <T> int addComponents0(boolean increaseWriterIndex, int cIndex, ByteWrapper<T> wrapper, T[] buffers, int offset) {
        T b;
        this.checkComponentIndex(cIndex);
        int len = buffers.length;
        for (int i = offset; i < len && (b = buffers[i]) != null; ++i) {
            int size;
            if (wrapper.isEmpty(b) || (cIndex = this.addComponent0(increaseWriterIndex, cIndex, wrapper.wrap(b)) + 1) <= (size = this.componentCount)) continue;
            cIndex = size;
        }
        return cIndex;
    }

    public CompositeByteBuf addComponents(int cIndex, Iterable<ByteBuf> buffers) {
        return this.addComponents(false, cIndex, buffers);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CompositeByteBuf addFlattenedComponents(boolean increaseWriterIndex, ByteBuf buffer) {
        ObjectUtil.checkNotNull((Object)buffer, (String)"buffer");
        int ridx = buffer.readerIndex();
        int widx = buffer.writerIndex();
        if (ridx == widx) {
            buffer.release();
            return this;
        }
        if (!(buffer instanceof CompositeByteBuf)) {
            this.addComponent0(increaseWriterIndex, this.componentCount, buffer);
            this.consolidateIfNeeded();
            return this;
        }
        CompositeByteBuf from = buffer instanceof WrappedCompositeByteBuf ? (CompositeByteBuf)buffer.unwrap() : (CompositeByteBuf)buffer;
        from.checkIndex(ridx, widx - ridx);
        Component[] fromComponents = from.components;
        int compCountBefore = this.componentCount;
        int writerIndexBefore = this.writerIndex;
        try {
            int cidx = from.toComponentIndex0(ridx);
            int newOffset = this.capacity();
            while (true) {
                Component component = fromComponents[cidx];
                int compOffset = component.offset;
                int fromIdx = Math.max(ridx, compOffset);
                int toIdx = Math.min(widx, component.endOffset);
                int len = toIdx - fromIdx;
                if (len > 0) {
                    this.addComp(this.componentCount, new Component(component.srcBuf.retain(), component.srcIdx(fromIdx), component.buf, component.idx(fromIdx), newOffset, len, null));
                }
                if (widx == toIdx) break;
                newOffset += len;
                ++cidx;
            }
            if (increaseWriterIndex) {
                this.writerIndex = writerIndexBefore + (widx - ridx);
            }
            this.consolidateIfNeeded();
            buffer.release();
            buffer = null;
            CompositeByteBuf compositeByteBuf = this;
            return compositeByteBuf;
        }
        finally {
            if (buffer != null) {
                if (increaseWriterIndex) {
                    this.writerIndex = writerIndexBefore;
                }
                for (int cidx = this.componentCount - 1; cidx >= compCountBefore; --cidx) {
                    this.components[cidx].free();
                    this.removeComp(cidx);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CompositeByteBuf addComponents(boolean increaseIndex, int cIndex, Iterable<ByteBuf> buffers) {
        if (buffers instanceof ByteBuf) {
            return this.addComponent(increaseIndex, cIndex, (ByteBuf)((Object)buffers));
        }
        ObjectUtil.checkNotNull(buffers, (String)"buffers");
        Iterator<ByteBuf> it = buffers.iterator();
        try {
            this.checkComponentIndex(cIndex);
            while (it.hasNext()) {
                ByteBuf b = it.next();
                if (b == null) {
                    break;
                }
                cIndex = this.addComponent0(increaseIndex, cIndex, b) + 1;
                cIndex = Math.min(cIndex, this.componentCount);
            }
        }
        finally {
            while (it.hasNext()) {
                ReferenceCountUtil.safeRelease((Object)it.next());
            }
        }
        this.consolidateIfNeeded();
        return this;
    }

    private void consolidateIfNeeded() {
        int size = this.componentCount;
        if (size > this.maxNumComponents) {
            this.consolidate0(0, size);
        }
    }

    private void checkComponentIndex(int cIndex) {
        this.ensureAccessible();
        if (cIndex < 0 || cIndex > this.componentCount) {
            throw new IndexOutOfBoundsException(String.format("cIndex: %d (expected: >= 0 && <= numComponents(%d))", cIndex, this.componentCount));
        }
    }

    private void checkComponentIndex(int cIndex, int numComponents) {
        this.ensureAccessible();
        if (cIndex < 0 || cIndex + numComponents > this.componentCount) {
            throw new IndexOutOfBoundsException(String.format("cIndex: %d, numComponents: %d (expected: cIndex >= 0 && cIndex + numComponents <= totalNumComponents(%d))", cIndex, numComponents, this.componentCount));
        }
    }

    private void updateComponentOffsets(int cIndex) {
        int nextIndex;
        int size = this.componentCount;
        if (size <= cIndex) {
            return;
        }
        int n = nextIndex = cIndex > 0 ? this.components[cIndex - 1].endOffset : 0;
        while (cIndex < size) {
            Component c = this.components[cIndex];
            c.reposition(nextIndex);
            nextIndex = c.endOffset;
            ++cIndex;
        }
    }

    public CompositeByteBuf removeComponent(int cIndex) {
        this.checkComponentIndex(cIndex);
        Component comp = this.components[cIndex];
        if (this.lastAccessed == comp) {
            this.lastAccessed = null;
        }
        comp.free();
        this.removeComp(cIndex);
        if (comp.length() > 0) {
            this.updateComponentOffsets(cIndex);
        }
        return this;
    }

    public CompositeByteBuf removeComponents(int cIndex, int numComponents) {
        this.checkComponentIndex(cIndex, numComponents);
        if (numComponents == 0) {
            return this;
        }
        int endIndex = cIndex + numComponents;
        boolean needsUpdate = false;
        for (int i = cIndex; i < endIndex; ++i) {
            Component c = this.components[i];
            if (c.length() > 0) {
                needsUpdate = true;
            }
            if (this.lastAccessed == c) {
                this.lastAccessed = null;
            }
            c.free();
        }
        this.removeCompRange(cIndex, endIndex);
        if (needsUpdate) {
            this.updateComponentOffsets(cIndex);
        }
        return this;
    }

    @Override
    public Iterator<ByteBuf> iterator() {
        this.ensureAccessible();
        return this.componentCount == 0 ? EMPTY_ITERATOR : new CompositeByteBufIterator();
    }

    @Override
    protected int forEachByteAsc0(int start, int end, ByteProcessor processor) throws Exception {
        if (end <= start) {
            return -1;
        }
        int i = this.toComponentIndex0(start);
        int length = end - start;
        while (length > 0) {
            Component c = this.components[i];
            if (c.offset != c.endOffset) {
                int result;
                ByteBuf s = c.buf;
                int localStart = c.idx(start);
                int localLength = Math.min(length, c.endOffset - start);
                int n = result = s instanceof AbstractByteBuf ? ((AbstractByteBuf)s).forEachByteAsc0(localStart, localStart + localLength, processor) : s.forEachByte(localStart, localLength, processor);
                if (result != -1) {
                    return result - c.adjustment;
                }
                start += localLength;
                length -= localLength;
            }
            ++i;
        }
        return -1;
    }

    @Override
    protected int forEachByteDesc0(int rStart, int rEnd, ByteProcessor processor) throws Exception {
        if (rEnd > rStart) {
            return -1;
        }
        int i = this.toComponentIndex0(rStart);
        int length = 1 + rStart - rEnd;
        while (length > 0) {
            Component c = this.components[i];
            if (c.offset != c.endOffset) {
                int result;
                ByteBuf s = c.buf;
                int localRStart = c.idx(length + rEnd);
                int localLength = Math.min(length, localRStart);
                int localIndex = localRStart - localLength;
                int n = result = s instanceof AbstractByteBuf ? ((AbstractByteBuf)s).forEachByteDesc0(localRStart - 1, localIndex, processor) : s.forEachByteDesc(localIndex, localLength, processor);
                if (result != -1) {
                    return result - c.adjustment;
                }
                length -= localLength;
            }
            --i;
        }
        return -1;
    }

    public List<ByteBuf> decompose(int offset, int length) {
        ByteBuf slice;
        this.checkIndex(offset, length);
        if (length == 0) {
            return Collections.emptyList();
        }
        int componentId = this.toComponentIndex0(offset);
        int bytesToSlice = length;
        Component firstC = this.components[componentId];
        if ((bytesToSlice -= (slice = firstC.srcBuf.slice(firstC.srcIdx(offset), Math.min(firstC.endOffset - offset, bytesToSlice))).readableBytes()) == 0) {
            return Collections.singletonList(slice);
        }
        ArrayList<ByteBuf> sliceList = new ArrayList<ByteBuf>(this.componentCount - componentId);
        sliceList.add(slice);
        do {
            Component component = this.components[++componentId];
            slice = component.srcBuf.slice(component.srcIdx(component.offset), Math.min(component.length(), bytesToSlice));
            sliceList.add(slice);
        } while ((bytesToSlice -= slice.readableBytes()) > 0);
        return sliceList;
    }

    @Override
    public boolean isDirect() {
        int size = this.componentCount;
        if (size == 0) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (this.components[i].buf.isDirect()) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean hasArray() {
        switch (this.componentCount) {
            case 0: {
                return true;
            }
            case 1: {
                return this.components[0].buf.hasArray();
            }
        }
        return false;
    }

    @Override
    public byte[] array() {
        switch (this.componentCount) {
            case 0: {
                return EmptyArrays.EMPTY_BYTES;
            }
            case 1: {
                return this.components[0].buf.array();
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public int arrayOffset() {
        switch (this.componentCount) {
            case 0: {
                return 0;
            }
            case 1: {
                Component c = this.components[0];
                return c.idx(c.buf.arrayOffset());
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasMemoryAddress() {
        switch (this.componentCount) {
            case 0: {
                return Unpooled.EMPTY_BUFFER.hasMemoryAddress();
            }
            case 1: {
                return this.components[0].buf.hasMemoryAddress();
            }
        }
        return false;
    }

    @Override
    public long memoryAddress() {
        switch (this.componentCount) {
            case 0: {
                return Unpooled.EMPTY_BUFFER.memoryAddress();
            }
            case 1: {
                Component c = this.components[0];
                return c.buf.memoryAddress() + (long)c.adjustment;
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public int capacity() {
        int size = this.componentCount;
        return size > 0 ? this.components[size - 1].endOffset : 0;
    }

    @Override
    public CompositeByteBuf capacity(int newCapacity) {
        this.checkNewCapacity(newCapacity);
        int size = this.componentCount;
        int oldCapacity = this.capacity();
        if (newCapacity > oldCapacity) {
            int paddingLength = newCapacity - oldCapacity;
            ByteBuf padding = this.allocBuffer(paddingLength).setIndex(0, paddingLength);
            this.addComponent0(false, size, padding);
            if (this.componentCount >= this.maxNumComponents) {
                this.consolidateIfNeeded();
            }
        } else if (newCapacity < oldCapacity) {
            int i;
            this.lastAccessed = null;
            int bytesToTrim = oldCapacity - newCapacity;
            for (i = size - 1; i >= 0; --i) {
                Component c = this.components[i];
                int cLength = c.length();
                if (bytesToTrim < cLength) {
                    c.endOffset -= bytesToTrim;
                    ByteBuf slice = c.slice;
                    if (slice == null) break;
                    c.slice = slice.slice(0, c.length());
                    break;
                }
                c.free();
                bytesToTrim -= cLength;
            }
            this.removeCompRange(i + 1, size);
            if (this.readerIndex() > newCapacity) {
                this.setIndex0(newCapacity, newCapacity);
            } else if (this.writerIndex > newCapacity) {
                this.writerIndex = newCapacity;
            }
        }
        return this;
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.alloc;
    }

    @Override
    public ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }

    public int numComponents() {
        return this.componentCount;
    }

    public int maxNumComponents() {
        return this.maxNumComponents;
    }

    public int toComponentIndex(int offset) {
        this.checkIndex(offset);
        return this.toComponentIndex0(offset);
    }

    private int toComponentIndex0(int offset) {
        int size = this.componentCount;
        if (offset == 0) {
            for (int i = 0; i < size; ++i) {
                if (this.components[i].endOffset <= 0) continue;
                return i;
            }
        }
        if (size <= 2) {
            return size == 1 || offset < this.components[0].endOffset ? 0 : 1;
        }
        int low = 0;
        int high = size;
        while (low <= high) {
            int mid = low + high >>> 1;
            Component c = this.components[mid];
            if (offset >= c.endOffset) {
                low = mid + 1;
                continue;
            }
            if (offset < c.offset) {
                high = mid - 1;
                continue;
            }
            return mid;
        }
        throw new Error("should not reach here");
    }

    public int toByteIndex(int cIndex) {
        this.checkComponentIndex(cIndex);
        return this.components[cIndex].offset;
    }

    @Override
    public byte getByte(int index) {
        Component c = this.findComponent(index);
        return c.buf.getByte(c.idx(index));
    }

    @Override
    protected byte _getByte(int index) {
        Component c = this.findComponent0(index);
        return c.buf.getByte(c.idx(index));
    }

    @Override
    protected short _getShort(int index) {
        Component c = this.findComponent0(index);
        if (index + 2 <= c.endOffset) {
            return c.buf.getShort(c.idx(index));
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (short)((this._getByte(index) & 0xFF) << 8 | this._getByte(index + 1) & 0xFF);
        }
        return (short)(this._getByte(index) & 0xFF | (this._getByte(index + 1) & 0xFF) << 8);
    }

    @Override
    protected short _getShortLE(int index) {
        Component c = this.findComponent0(index);
        if (index + 2 <= c.endOffset) {
            return c.buf.getShortLE(c.idx(index));
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (short)(this._getByte(index) & 0xFF | (this._getByte(index + 1) & 0xFF) << 8);
        }
        return (short)((this._getByte(index) & 0xFF) << 8 | this._getByte(index + 1) & 0xFF);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        Component c = this.findComponent0(index);
        if (index + 3 <= c.endOffset) {
            return c.buf.getUnsignedMedium(c.idx(index));
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (this._getShort(index) & 0xFFFF) << 8 | this._getByte(index + 2) & 0xFF;
        }
        return this._getShort(index) & 0xFFFF | (this._getByte(index + 2) & 0xFF) << 16;
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        Component c = this.findComponent0(index);
        if (index + 3 <= c.endOffset) {
            return c.buf.getUnsignedMediumLE(c.idx(index));
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return this._getShortLE(index) & 0xFFFF | (this._getByte(index + 2) & 0xFF) << 16;
        }
        return (this._getShortLE(index) & 0xFFFF) << 8 | this._getByte(index + 2) & 0xFF;
    }

    @Override
    protected int _getInt(int index) {
        Component c = this.findComponent0(index);
        if (index + 4 <= c.endOffset) {
            return c.buf.getInt(c.idx(index));
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (this._getShort(index) & 0xFFFF) << 16 | this._getShort(index + 2) & 0xFFFF;
        }
        return this._getShort(index) & 0xFFFF | (this._getShort(index + 2) & 0xFFFF) << 16;
    }

    @Override
    protected int _getIntLE(int index) {
        Component c = this.findComponent0(index);
        if (index + 4 <= c.endOffset) {
            return c.buf.getIntLE(c.idx(index));
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return this._getShortLE(index) & 0xFFFF | (this._getShortLE(index + 2) & 0xFFFF) << 16;
        }
        return (this._getShortLE(index) & 0xFFFF) << 16 | this._getShortLE(index + 2) & 0xFFFF;
    }

    @Override
    protected long _getLong(int index) {
        Component c = this.findComponent0(index);
        if (index + 8 <= c.endOffset) {
            return c.buf.getLong(c.idx(index));
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return ((long)this._getInt(index) & 0xFFFFFFFFL) << 32 | (long)this._getInt(index + 4) & 0xFFFFFFFFL;
        }
        return (long)this._getInt(index) & 0xFFFFFFFFL | ((long)this._getInt(index + 4) & 0xFFFFFFFFL) << 32;
    }

    @Override
    protected long _getLongLE(int index) {
        Component c = this.findComponent0(index);
        if (index + 8 <= c.endOffset) {
            return c.buf.getLongLE(c.idx(index));
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (long)this._getIntLE(index) & 0xFFFFFFFFL | ((long)this._getIntLE(index + 4) & 0xFFFFFFFFL) << 32;
        }
        return ((long)this._getIntLE(index) & 0xFFFFFFFFL) << 32 | (long)this._getIntLE(index + 4) & 0xFFFFFFFFL;
    }

    @Override
    public CompositeByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.checkDstIndex(index, length, dstIndex, dst.length);
        if (length == 0) {
            return this;
        }
        int i = this.toComponentIndex0(index);
        while (length > 0) {
            Component c = this.components[i];
            int localLength = Math.min(length, c.endOffset - index);
            c.buf.getBytes(c.idx(index), dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
            length -= localLength;
            ++i;
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompositeByteBuf getBytes(int index, ByteBuffer dst) {
        int limit = dst.limit();
        int length = dst.remaining();
        this.checkIndex(index, length);
        if (length == 0) {
            return this;
        }
        int i = this.toComponentIndex0(index);
        try {
            while (length > 0) {
                Component c = this.components[i];
                int localLength = Math.min(length, c.endOffset - index);
                dst.limit(dst.position() + localLength);
                c.buf.getBytes(c.idx(index), dst);
                index += localLength;
                length -= localLength;
                ++i;
            }
        }
        finally {
            dst.limit(limit);
        }
        return this;
    }

    @Override
    public CompositeByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.checkDstIndex(index, length, dstIndex, dst.capacity());
        if (length == 0) {
            return this;
        }
        int i = this.toComponentIndex0(index);
        while (length > 0) {
            Component c = this.components[i];
            int localLength = Math.min(length, c.endOffset - index);
            c.buf.getBytes(c.idx(index), dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
            length -= localLength;
            ++i;
        }
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        int count = this.nioBufferCount();
        if (count == 1) {
            return out.write(this.internalNioBuffer(index, length));
        }
        long writtenBytes = out.write(this.nioBuffers(index, length));
        if (writtenBytes > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)writtenBytes;
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        int count = this.nioBufferCount();
        if (count == 1) {
            return out.write(this.internalNioBuffer(index, length), position);
        }
        long writtenBytes = 0L;
        for (ByteBuffer buf : this.nioBuffers(index, length)) {
            writtenBytes += (long)out.write(buf, position + writtenBytes);
        }
        if (writtenBytes > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)writtenBytes;
    }

    @Override
    public CompositeByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.checkIndex(index, length);
        if (length == 0) {
            return this;
        }
        int i = this.toComponentIndex0(index);
        while (length > 0) {
            Component c = this.components[i];
            int localLength = Math.min(length, c.endOffset - index);
            c.buf.getBytes(c.idx(index), out, localLength);
            index += localLength;
            length -= localLength;
            ++i;
        }
        return this;
    }

    @Override
    public CompositeByteBuf setByte(int index, int value) {
        Component c = this.findComponent(index);
        c.buf.setByte(c.idx(index), value);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        Component c = this.findComponent0(index);
        c.buf.setByte(c.idx(index), value);
    }

    @Override
    public CompositeByteBuf setShort(int index, int value) {
        this.checkIndex(index, 2);
        this._setShort(index, value);
        return this;
    }

    @Override
    protected void _setShort(int index, int value) {
        Component c = this.findComponent0(index);
        if (index + 2 <= c.endOffset) {
            c.buf.setShort(c.idx(index), value);
        } else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setByte(index, (byte)(value >>> 8));
            this._setByte(index + 1, (byte)value);
        } else {
            this._setByte(index, (byte)value);
            this._setByte(index + 1, (byte)(value >>> 8));
        }
    }

    @Override
    protected void _setShortLE(int index, int value) {
        Component c = this.findComponent0(index);
        if (index + 2 <= c.endOffset) {
            c.buf.setShortLE(c.idx(index), value);
        } else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setByte(index, (byte)value);
            this._setByte(index + 1, (byte)(value >>> 8));
        } else {
            this._setByte(index, (byte)(value >>> 8));
            this._setByte(index + 1, (byte)value);
        }
    }

    @Override
    public CompositeByteBuf setMedium(int index, int value) {
        this.checkIndex(index, 3);
        this._setMedium(index, value);
        return this;
    }

    @Override
    protected void _setMedium(int index, int value) {
        Component c = this.findComponent0(index);
        if (index + 3 <= c.endOffset) {
            c.buf.setMedium(c.idx(index), value);
        } else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setShort(index, (short)(value >> 8));
            this._setByte(index + 2, (byte)value);
        } else {
            this._setShort(index, (short)value);
            this._setByte(index + 2, (byte)(value >>> 16));
        }
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        Component c = this.findComponent0(index);
        if (index + 3 <= c.endOffset) {
            c.buf.setMediumLE(c.idx(index), value);
        } else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setShortLE(index, (short)value);
            this._setByte(index + 2, (byte)(value >>> 16));
        } else {
            this._setShortLE(index, (short)(value >> 8));
            this._setByte(index + 2, (byte)value);
        }
    }

    @Override
    public CompositeByteBuf setInt(int index, int value) {
        this.checkIndex(index, 4);
        this._setInt(index, value);
        return this;
    }

    @Override
    protected void _setInt(int index, int value) {
        Component c = this.findComponent0(index);
        if (index + 4 <= c.endOffset) {
            c.buf.setInt(c.idx(index), value);
        } else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setShort(index, (short)(value >>> 16));
            this._setShort(index + 2, (short)value);
        } else {
            this._setShort(index, (short)value);
            this._setShort(index + 2, (short)(value >>> 16));
        }
    }

    @Override
    protected void _setIntLE(int index, int value) {
        Component c = this.findComponent0(index);
        if (index + 4 <= c.endOffset) {
            c.buf.setIntLE(c.idx(index), value);
        } else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setShortLE(index, (short)value);
            this._setShortLE(index + 2, (short)(value >>> 16));
        } else {
            this._setShortLE(index, (short)(value >>> 16));
            this._setShortLE(index + 2, (short)value);
        }
    }

    @Override
    public CompositeByteBuf setLong(int index, long value) {
        this.checkIndex(index, 8);
        this._setLong(index, value);
        return this;
    }

    @Override
    protected void _setLong(int index, long value) {
        Component c = this.findComponent0(index);
        if (index + 8 <= c.endOffset) {
            c.buf.setLong(c.idx(index), value);
        } else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setInt(index, (int)(value >>> 32));
            this._setInt(index + 4, (int)value);
        } else {
            this._setInt(index, (int)value);
            this._setInt(index + 4, (int)(value >>> 32));
        }
    }

    @Override
    protected void _setLongLE(int index, long value) {
        Component c = this.findComponent0(index);
        if (index + 8 <= c.endOffset) {
            c.buf.setLongLE(c.idx(index), value);
        } else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this._setIntLE(index, (int)value);
            this._setIntLE(index + 4, (int)(value >>> 32));
        } else {
            this._setIntLE(index, (int)(value >>> 32));
            this._setIntLE(index + 4, (int)value);
        }
    }

    @Override
    public CompositeByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        this.checkSrcIndex(index, length, srcIndex, src.length);
        if (length == 0) {
            return this;
        }
        int i = this.toComponentIndex0(index);
        while (length > 0) {
            Component c = this.components[i];
            int localLength = Math.min(length, c.endOffset - index);
            c.buf.setBytes(c.idx(index), src, srcIndex, localLength);
            index += localLength;
            srcIndex += localLength;
            length -= localLength;
            ++i;
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompositeByteBuf setBytes(int index, ByteBuffer src) {
        int limit = src.limit();
        int length = src.remaining();
        this.checkIndex(index, length);
        if (length == 0) {
            return this;
        }
        int i = this.toComponentIndex0(index);
        try {
            while (length > 0) {
                Component c = this.components[i];
                int localLength = Math.min(length, c.endOffset - index);
                src.limit(src.position() + localLength);
                c.buf.setBytes(c.idx(index), src);
                index += localLength;
                length -= localLength;
                ++i;
            }
        }
        finally {
            src.limit(limit);
        }
        return this;
    }

    @Override
    public CompositeByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.checkSrcIndex(index, length, srcIndex, src.capacity());
        if (length == 0) {
            return this;
        }
        int i = this.toComponentIndex0(index);
        while (length > 0) {
            Component c = this.components[i];
            int localLength = Math.min(length, c.endOffset - index);
            c.buf.setBytes(c.idx(index), src, srcIndex, localLength);
            index += localLength;
            srcIndex += localLength;
            length -= localLength;
            ++i;
        }
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        this.checkIndex(index, length);
        if (length == 0) {
            return in.read(EmptyArrays.EMPTY_BYTES);
        }
        int i = this.toComponentIndex0(index);
        int readBytes = 0;
        do {
            Component c = this.components[i];
            int localLength = Math.min(length, c.endOffset - index);
            if (localLength == 0) {
                ++i;
                continue;
            }
            int localReadBytes = c.buf.setBytes(c.idx(index), in, localLength);
            if (localReadBytes < 0) {
                if (readBytes != 0) break;
                return -1;
            }
            index += localReadBytes;
            length -= localReadBytes;
            readBytes += localReadBytes;
            if (localReadBytes != localLength) continue;
            ++i;
        } while (length > 0);
        return readBytes;
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        this.checkIndex(index, length);
        if (length == 0) {
            return in.read(EMPTY_NIO_BUFFER);
        }
        int i = this.toComponentIndex0(index);
        int readBytes = 0;
        do {
            Component c = this.components[i];
            int localLength = Math.min(length, c.endOffset - index);
            if (localLength == 0) {
                ++i;
                continue;
            }
            int localReadBytes = c.buf.setBytes(c.idx(index), in, localLength);
            if (localReadBytes == 0) break;
            if (localReadBytes < 0) {
                if (readBytes != 0) break;
                return -1;
            }
            index += localReadBytes;
            length -= localReadBytes;
            readBytes += localReadBytes;
            if (localReadBytes != localLength) continue;
            ++i;
        } while (length > 0);
        return readBytes;
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        this.checkIndex(index, length);
        if (length == 0) {
            return in.read(EMPTY_NIO_BUFFER, position);
        }
        int i = this.toComponentIndex0(index);
        int readBytes = 0;
        do {
            Component c = this.components[i];
            int localLength = Math.min(length, c.endOffset - index);
            if (localLength == 0) {
                ++i;
                continue;
            }
            int localReadBytes = c.buf.setBytes(c.idx(index), in, position + (long)readBytes, localLength);
            if (localReadBytes == 0) break;
            if (localReadBytes < 0) {
                if (readBytes != 0) break;
                return -1;
            }
            index += localReadBytes;
            length -= localReadBytes;
            readBytes += localReadBytes;
            if (localReadBytes != localLength) continue;
            ++i;
        } while (length > 0);
        return readBytes;
    }

    @Override
    public ByteBuf copy(int index, int length) {
        this.checkIndex(index, length);
        ByteBuf dst = this.allocBuffer(length);
        if (length != 0) {
            this.copyTo(index, length, this.toComponentIndex0(index), dst);
        }
        return dst;
    }

    private void copyTo(int index, int length, int componentId, ByteBuf dst) {
        int dstIndex = 0;
        int i = componentId;
        while (length > 0) {
            Component c = this.components[i];
            int localLength = Math.min(length, c.endOffset - index);
            c.buf.getBytes(c.idx(index), dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
            length -= localLength;
            ++i;
        }
        dst.writerIndex(dst.capacity());
    }

    public ByteBuf component(int cIndex) {
        this.checkComponentIndex(cIndex);
        return this.components[cIndex].duplicate();
    }

    public ByteBuf componentAtOffset(int offset) {
        return this.findComponent(offset).duplicate();
    }

    public ByteBuf internalComponent(int cIndex) {
        this.checkComponentIndex(cIndex);
        return this.components[cIndex].slice();
    }

    public ByteBuf internalComponentAtOffset(int offset) {
        return this.findComponent(offset).slice();
    }

    private Component findComponent(int offset) {
        Component la = this.lastAccessed;
        if (la != null && offset >= la.offset && offset < la.endOffset) {
            this.ensureAccessible();
            return la;
        }
        this.checkIndex(offset);
        return this.findIt(offset);
    }

    private Component findComponent0(int offset) {
        Component la = this.lastAccessed;
        if (la != null && offset >= la.offset && offset < la.endOffset) {
            return la;
        }
        return this.findIt(offset);
    }

    private Component findIt(int offset) {
        int low = 0;
        int high = this.componentCount;
        while (low <= high) {
            int mid = low + high >>> 1;
            Component c = this.components[mid];
            if (c == null) {
                throw new IllegalStateException("No component found for offset. Composite buffer layout might be outdated, e.g. from a discardReadBytes call.");
            }
            if (offset >= c.endOffset) {
                low = mid + 1;
                continue;
            }
            if (offset < c.offset) {
                high = mid - 1;
                continue;
            }
            this.lastAccessed = c;
            return c;
        }
        throw new Error("should not reach here");
    }

    @Override
    public int nioBufferCount() {
        int size = this.componentCount;
        switch (size) {
            case 0: {
                return 1;
            }
            case 1: {
                return this.components[0].buf.nioBufferCount();
            }
        }
        int count = 0;
        for (int i = 0; i < size; ++i) {
            count += this.components[i].buf.nioBufferCount();
        }
        return count;
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        switch (this.componentCount) {
            case 0: {
                return EMPTY_NIO_BUFFER;
            }
            case 1: {
                return this.components[0].internalNioBuffer(index, length);
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        this.checkIndex(index, length);
        switch (this.componentCount) {
            case 0: {
                return EMPTY_NIO_BUFFER;
            }
            case 1: {
                Component c = this.components[0];
                ByteBuf buf = c.buf;
                if (buf.nioBufferCount() != 1) break;
                return buf.nioBuffer(c.idx(index), length);
            }
        }
        ByteBuffer[] buffers = this.nioBuffers(index, length);
        if (buffers.length == 1) {
            return buffers[0];
        }
        ByteBuffer merged = ByteBuffer.allocate(length).order(this.order());
        for (ByteBuffer buf : buffers) {
            merged.put(buf);
        }
        merged.flip();
        return merged;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        this.checkIndex(index, length);
        if (length == 0) {
            return new ByteBuffer[]{EMPTY_NIO_BUFFER};
        }
        RecyclableArrayList buffers = RecyclableArrayList.newInstance((int)this.componentCount);
        try {
            int i = this.toComponentIndex0(index);
            while (length > 0) {
                Component c = this.components[i];
                ByteBuf s = c.buf;
                int localLength = Math.min(length, c.endOffset - index);
                switch (s.nioBufferCount()) {
                    case 0: {
                        throw new UnsupportedOperationException();
                    }
                    case 1: {
                        buffers.add((Object)s.nioBuffer(c.idx(index), localLength));
                        break;
                    }
                    default: {
                        Collections.addAll(buffers, s.nioBuffers(c.idx(index), localLength));
                    }
                }
                index += localLength;
                length -= localLength;
                ++i;
            }
            ByteBuffer[] byteBufferArray = (ByteBuffer[])buffers.toArray((Object[])EmptyArrays.EMPTY_BYTE_BUFFERS);
            return byteBufferArray;
        }
        finally {
            buffers.recycle();
        }
    }

    public CompositeByteBuf consolidate() {
        this.ensureAccessible();
        this.consolidate0(0, this.componentCount);
        return this;
    }

    public CompositeByteBuf consolidate(int cIndex, int numComponents) {
        this.checkComponentIndex(cIndex, numComponents);
        this.consolidate0(cIndex, numComponents);
        return this;
    }

    private void consolidate0(int cIndex, int numComponents) {
        if (numComponents <= 1) {
            return;
        }
        int endCIndex = cIndex + numComponents;
        int startOffset = cIndex != 0 ? this.components[cIndex].offset : 0;
        int capacity = this.components[endCIndex - 1].endOffset - startOffset;
        ByteBuf consolidated = this.allocBuffer(capacity);
        for (int i = cIndex; i < endCIndex; ++i) {
            this.components[i].transferTo(consolidated);
        }
        this.lastAccessed = null;
        this.removeCompRange(cIndex + 1, endCIndex);
        this.components[cIndex] = this.newComponent(consolidated, 0);
        if (cIndex != 0 || numComponents != this.componentCount) {
            this.updateComponentOffsets(cIndex);
        }
    }

    public CompositeByteBuf discardReadComponents() {
        int firstComponentId;
        this.ensureAccessible();
        int readerIndex = this.readerIndex();
        if (readerIndex == 0) {
            return this;
        }
        int writerIndex = this.writerIndex();
        if (readerIndex == writerIndex && writerIndex == this.capacity()) {
            int size = this.componentCount;
            for (int i = 0; i < size; ++i) {
                this.components[i].free();
            }
            this.lastAccessed = null;
            this.clearComps();
            this.setIndex(0, 0);
            this.adjustMarkers(readerIndex);
            return this;
        }
        Component c = null;
        int size = this.componentCount;
        for (firstComponentId = 0; firstComponentId < size; ++firstComponentId) {
            c = this.components[firstComponentId];
            if (c.endOffset > readerIndex) break;
            c.free();
        }
        if (firstComponentId == 0) {
            return this;
        }
        Component la = this.lastAccessed;
        if (la != null && la.endOffset <= readerIndex) {
            this.lastAccessed = null;
        }
        this.removeCompRange(0, firstComponentId);
        int offset = c.offset;
        this.updateComponentOffsets(0);
        this.setIndex(readerIndex - offset, writerIndex - offset);
        this.adjustMarkers(offset);
        return this;
    }

    @Override
    public CompositeByteBuf discardReadBytes() {
        Component la;
        int firstComponentId;
        this.ensureAccessible();
        int readerIndex = this.readerIndex();
        if (readerIndex == 0) {
            return this;
        }
        int writerIndex = this.writerIndex();
        if (readerIndex == writerIndex && writerIndex == this.capacity()) {
            int size = this.componentCount;
            for (int i = 0; i < size; ++i) {
                this.components[i].free();
            }
            this.lastAccessed = null;
            this.clearComps();
            this.setIndex(0, 0);
            this.adjustMarkers(readerIndex);
            return this;
        }
        Component c = null;
        int size = this.componentCount;
        for (firstComponentId = 0; firstComponentId < size; ++firstComponentId) {
            c = this.components[firstComponentId];
            if (c.endOffset > readerIndex) break;
            c.free();
        }
        int trimmedBytes = readerIndex - c.offset;
        c.offset = 0;
        c.endOffset -= readerIndex;
        c.srcAdjustment += readerIndex;
        c.adjustment += readerIndex;
        ByteBuf slice = c.slice;
        if (slice != null) {
            c.slice = slice.slice(trimmedBytes, c.length());
        }
        if ((la = this.lastAccessed) != null && la.endOffset <= readerIndex) {
            this.lastAccessed = null;
        }
        this.removeCompRange(0, firstComponentId);
        this.updateComponentOffsets(0);
        this.setIndex(0, writerIndex - readerIndex);
        this.adjustMarkers(readerIndex);
        return this;
    }

    private ByteBuf allocBuffer(int capacity) {
        return this.direct ? this.alloc().directBuffer(capacity) : this.alloc().heapBuffer(capacity);
    }

    @Override
    public String toString() {
        String result = super.toString();
        result = result.substring(0, result.length() - 1);
        return result + ", components=" + this.componentCount + ')';
    }

    @Override
    public CompositeByteBuf readerIndex(int readerIndex) {
        super.readerIndex(readerIndex);
        return this;
    }

    @Override
    public CompositeByteBuf writerIndex(int writerIndex) {
        super.writerIndex(writerIndex);
        return this;
    }

    @Override
    public CompositeByteBuf setIndex(int readerIndex, int writerIndex) {
        super.setIndex(readerIndex, writerIndex);
        return this;
    }

    @Override
    public CompositeByteBuf clear() {
        super.clear();
        return this;
    }

    @Override
    public CompositeByteBuf markReaderIndex() {
        super.markReaderIndex();
        return this;
    }

    @Override
    public CompositeByteBuf resetReaderIndex() {
        super.resetReaderIndex();
        return this;
    }

    @Override
    public CompositeByteBuf markWriterIndex() {
        super.markWriterIndex();
        return this;
    }

    @Override
    public CompositeByteBuf resetWriterIndex() {
        super.resetWriterIndex();
        return this;
    }

    @Override
    public CompositeByteBuf ensureWritable(int minWritableBytes) {
        super.ensureWritable(minWritableBytes);
        return this;
    }

    @Override
    public CompositeByteBuf getBytes(int index, ByteBuf dst) {
        return this.getBytes(index, dst, dst.writableBytes());
    }

    @Override
    public CompositeByteBuf getBytes(int index, ByteBuf dst, int length) {
        this.getBytes(index, dst, dst.writerIndex(), length);
        dst.writerIndex(dst.writerIndex() + length);
        return this;
    }

    @Override
    public CompositeByteBuf getBytes(int index, byte[] dst) {
        return this.getBytes(index, dst, 0, dst.length);
    }

    @Override
    public CompositeByteBuf setBoolean(int index, boolean value) {
        return this.setByte(index, value ? 1 : 0);
    }

    @Override
    public CompositeByteBuf setChar(int index, int value) {
        return this.setShort(index, value);
    }

    @Override
    public CompositeByteBuf setFloat(int index, float value) {
        return this.setInt(index, Float.floatToRawIntBits(value));
    }

    @Override
    public CompositeByteBuf setDouble(int index, double value) {
        return this.setLong(index, Double.doubleToRawLongBits(value));
    }

    @Override
    public CompositeByteBuf setBytes(int index, ByteBuf src) {
        super.setBytes(index, src, src.readableBytes());
        return this;
    }

    @Override
    public CompositeByteBuf setBytes(int index, ByteBuf src, int length) {
        super.setBytes(index, src, length);
        return this;
    }

    @Override
    public CompositeByteBuf setBytes(int index, byte[] src) {
        return this.setBytes(index, src, 0, src.length);
    }

    @Override
    public CompositeByteBuf setZero(int index, int length) {
        super.setZero(index, length);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(ByteBuf dst) {
        super.readBytes(dst, dst.writableBytes());
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(ByteBuf dst, int length) {
        super.readBytes(dst, length);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        super.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(byte[] dst) {
        super.readBytes(dst, 0, dst.length);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        super.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(ByteBuffer dst) {
        super.readBytes(dst);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(OutputStream out, int length) throws IOException {
        super.readBytes(out, length);
        return this;
    }

    @Override
    public CompositeByteBuf skipBytes(int length) {
        super.skipBytes(length);
        return this;
    }

    @Override
    public CompositeByteBuf writeBoolean(boolean value) {
        this.writeByte(value ? 1 : 0);
        return this;
    }

    @Override
    public CompositeByteBuf writeByte(int value) {
        this.ensureWritable0(1);
        this._setByte(this.writerIndex++, value);
        return this;
    }

    @Override
    public CompositeByteBuf writeShort(int value) {
        super.writeShort(value);
        return this;
    }

    @Override
    public CompositeByteBuf writeMedium(int value) {
        super.writeMedium(value);
        return this;
    }

    @Override
    public CompositeByteBuf writeInt(int value) {
        super.writeInt(value);
        return this;
    }

    @Override
    public CompositeByteBuf writeLong(long value) {
        super.writeLong(value);
        return this;
    }

    @Override
    public CompositeByteBuf writeChar(int value) {
        super.writeShort(value);
        return this;
    }

    @Override
    public CompositeByteBuf writeFloat(float value) {
        super.writeInt(Float.floatToRawIntBits(value));
        return this;
    }

    @Override
    public CompositeByteBuf writeDouble(double value) {
        super.writeLong(Double.doubleToRawLongBits(value));
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(ByteBuf src) {
        super.writeBytes(src, src.readableBytes());
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(ByteBuf src, int length) {
        super.writeBytes(src, length);
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        super.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(byte[] src) {
        super.writeBytes(src, 0, src.length);
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        super.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(ByteBuffer src) {
        super.writeBytes(src);
        return this;
    }

    @Override
    public CompositeByteBuf writeZero(int length) {
        super.writeZero(length);
        return this;
    }

    @Override
    public CompositeByteBuf retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public CompositeByteBuf retain() {
        super.retain();
        return this;
    }

    @Override
    public CompositeByteBuf touch() {
        return this;
    }

    @Override
    public CompositeByteBuf touch(Object hint) {
        return this;
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.nioBuffers(this.readerIndex(), this.readableBytes());
    }

    @Override
    public CompositeByteBuf discardSomeReadBytes() {
        return this.discardReadComponents();
    }

    @Override
    protected void deallocate() {
        if (this.freed) {
            return;
        }
        this.freed = true;
        int size = this.componentCount;
        for (int i = 0; i < size; ++i) {
            this.components[i].free();
        }
    }

    @Override
    boolean isAccessible() {
        return !this.freed;
    }

    @Override
    public ByteBuf unwrap() {
        return null;
    }

    private void clearComps() {
        this.removeCompRange(0, this.componentCount);
    }

    private void removeComp(int i) {
        this.removeCompRange(i, i + 1);
    }

    private void removeCompRange(int from, int to) {
        int newSize;
        if (from >= to) {
            return;
        }
        int size = this.componentCount;
        assert (from >= 0 && to <= size);
        if (to < size) {
            System.arraycopy(this.components, to, this.components, from, size - to);
        }
        for (int i = newSize = size - to + from; i < size; ++i) {
            this.components[i] = null;
        }
        this.componentCount = newSize;
    }

    private void addComp(int i, Component c) {
        this.shiftComps(i, 1);
        this.components[i] = c;
    }

    private void shiftComps(int i, int count) {
        int size = this.componentCount;
        int newSize = size + count;
        assert (i >= 0 && i <= size && count > 0);
        if (newSize > this.components.length) {
            Component[] newArr;
            int newArrSize = Math.max(size + (size >> 1), newSize);
            if (i == size) {
                newArr = (Component[])Arrays.copyOf(this.components, newArrSize, Component[].class);
            } else {
                newArr = new Component[newArrSize];
                if (i > 0) {
                    System.arraycopy(this.components, 0, newArr, 0, i);
                }
                if (i < size) {
                    System.arraycopy(this.components, i, newArr, i + count, size - i);
                }
            }
            this.components = newArr;
        } else if (i < size) {
            System.arraycopy(this.components, i, this.components, i + count, size - i);
        }
        this.componentCount = newSize;
    }

    private final class CompositeByteBufIterator
    implements Iterator<ByteBuf> {
        private final int size;
        private int index;

        private CompositeByteBufIterator() {
            this.size = CompositeByteBuf.this.numComponents();
        }

        @Override
        public boolean hasNext() {
            return this.size > this.index;
        }

        @Override
        public ByteBuf next() {
            if (this.size != CompositeByteBuf.this.numComponents()) {
                throw new ConcurrentModificationException();
            }
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            try {
                return CompositeByteBuf.this.components[this.index++].slice();
            }
            catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Read-Only");
        }
    }

    private static final class Component {
        final ByteBuf srcBuf;
        final ByteBuf buf;
        int srcAdjustment;
        int adjustment;
        int offset;
        int endOffset;
        private ByteBuf slice;

        Component(ByteBuf srcBuf, int srcOffset, ByteBuf buf, int bufOffset, int offset, int len, ByteBuf slice) {
            this.srcBuf = srcBuf;
            this.srcAdjustment = srcOffset - offset;
            this.buf = buf;
            this.adjustment = bufOffset - offset;
            this.offset = offset;
            this.endOffset = offset + len;
            this.slice = slice;
        }

        int srcIdx(int index) {
            return index + this.srcAdjustment;
        }

        int idx(int index) {
            return index + this.adjustment;
        }

        int length() {
            return this.endOffset - this.offset;
        }

        void reposition(int newOffset) {
            int move = newOffset - this.offset;
            this.endOffset += move;
            this.srcAdjustment -= move;
            this.adjustment -= move;
            this.offset = newOffset;
        }

        void transferTo(ByteBuf dst) {
            dst.writeBytes(this.buf, this.idx(this.offset), this.length());
            this.free();
        }

        ByteBuf slice() {
            ByteBuf s = this.slice;
            if (s == null) {
                this.slice = s = this.srcBuf.slice(this.srcIdx(this.offset), this.length());
            }
            return s;
        }

        ByteBuf duplicate() {
            return this.srcBuf.duplicate();
        }

        ByteBuffer internalNioBuffer(int index, int length) {
            return this.srcBuf.internalNioBuffer(this.srcIdx(index), length);
        }

        void free() {
            this.slice = null;
            this.srcBuf.release();
        }
    }

    static interface ByteWrapper<T> {
        public ByteBuf wrap(T var1);

        public boolean isEmpty(T var1);
    }
}

