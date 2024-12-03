/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http2;

import java.nio.ByteBuffer;
import org.apache.coyote.http2.HPackHuffman;
import org.apache.coyote.http2.Hpack;
import org.apache.coyote.http2.HpackException;
import org.apache.coyote.http2.StreamException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class HpackDecoder {
    private static final Log log = LogFactory.getLog(HpackDecoder.class);
    private static final StringManager sm = StringManager.getManager(HpackDecoder.class);
    private static final int DEFAULT_RING_BUFFER_SIZE = 10;
    private HeaderEmitter headerEmitter;
    private Hpack.HeaderField[] headerTable;
    private int firstSlotPosition = 0;
    private int filledTableSlots = 0;
    private int currentMemorySize = 0;
    private int maxMemorySizeHard;
    private int maxMemorySizeSoft;
    private int maxHeaderCount = 100;
    private int maxHeaderSize = 8192;
    private volatile int headerCount = 0;
    private volatile boolean countedCookie;
    private volatile int headerSize = 0;

    HpackDecoder(int maxMemorySize) {
        this.maxMemorySizeHard = maxMemorySize;
        this.maxMemorySizeSoft = maxMemorySize;
        this.headerTable = new Hpack.HeaderField[10];
    }

    HpackDecoder() {
        this(4096);
    }

    void decode(ByteBuffer buffer) throws HpackException {
        while (buffer.hasRemaining()) {
            String headerValue;
            int originalPos = buffer.position();
            byte b = buffer.get();
            if ((b & 0x80) != 0) {
                buffer.position(buffer.position() - 1);
                int index = Hpack.decodeInteger(buffer, 7);
                if (index == -1) {
                    buffer.position(originalPos);
                    return;
                }
                if (index == 0) {
                    throw new HpackException(sm.getString("hpackdecoder.zeroNotValidHeaderTableIndex"));
                }
                this.handleIndex(index);
                continue;
            }
            if ((b & 0x40) != 0) {
                String headerName = this.readHeaderName(buffer, 6);
                if (headerName == null) {
                    buffer.position(originalPos);
                    return;
                }
                headerValue = this.readHpackString(buffer);
                if (headerValue == null) {
                    buffer.position(originalPos);
                    return;
                }
                this.emitHeader(headerName, headerValue);
                this.addEntryToHeaderTable(new Hpack.HeaderField(headerName, headerValue));
                continue;
            }
            if ((b & 0xF0) == 0) {
                String headerName = this.readHeaderName(buffer, 4);
                if (headerName == null) {
                    buffer.position(originalPos);
                    return;
                }
                headerValue = this.readHpackString(buffer);
                if (headerValue == null) {
                    buffer.position(originalPos);
                    return;
                }
                this.emitHeader(headerName, headerValue);
                continue;
            }
            if ((b & 0xF0) == 16) {
                String headerName = this.readHeaderName(buffer, 4);
                if (headerName == null) {
                    buffer.position(originalPos);
                    return;
                }
                headerValue = this.readHpackString(buffer);
                if (headerValue == null) {
                    buffer.position(originalPos);
                    return;
                }
                this.emitHeader(headerName, headerValue);
                continue;
            }
            if ((b & 0xE0) == 32) {
                if (this.handleMaxMemorySizeChange(buffer, originalPos)) continue;
                return;
            }
            throw new RuntimeException(sm.getString("hpackdecoder.notImplemented"));
        }
    }

    private boolean handleMaxMemorySizeChange(ByteBuffer buffer, int originalPos) throws HpackException {
        if (this.headerCount != 0) {
            throw new HpackException(sm.getString("hpackdecoder.tableSizeUpdateNotAtStart"));
        }
        buffer.position(buffer.position() - 1);
        int size = Hpack.decodeInteger(buffer, 5);
        if (size == -1) {
            buffer.position(originalPos);
            return false;
        }
        if (size > this.maxMemorySizeHard) {
            throw new HpackException(sm.getString("hpackdecoder.maxMemorySizeExceeded", new Object[]{size, this.maxMemorySizeHard}));
        }
        this.maxMemorySizeSoft = size;
        if (this.currentMemorySize > this.maxMemorySizeSoft) {
            int newTableSlots = this.filledTableSlots;
            int tableLength = this.headerTable.length;
            int newSize = this.currentMemorySize;
            while (newSize > this.maxMemorySizeSoft) {
                int clearIndex = this.firstSlotPosition++;
                if (this.firstSlotPosition == tableLength) {
                    this.firstSlotPosition = 0;
                }
                Hpack.HeaderField oldData = this.headerTable[clearIndex];
                this.headerTable[clearIndex] = null;
                newSize -= oldData.size;
                --newTableSlots;
            }
            this.filledTableSlots = newTableSlots;
            this.currentMemorySize = newSize;
        }
        return true;
    }

    private String readHeaderName(ByteBuffer buffer, int prefixLength) throws HpackException {
        buffer.position(buffer.position() - 1);
        int index = Hpack.decodeInteger(buffer, prefixLength);
        if (index == -1) {
            return null;
        }
        if (index != 0) {
            return this.handleIndexedHeaderName(index);
        }
        return this.readHpackString(buffer);
    }

    private String readHpackString(ByteBuffer buffer) throws HpackException {
        boolean huffman;
        if (!buffer.hasRemaining()) {
            return null;
        }
        byte data = buffer.get(buffer.position());
        int length = Hpack.decodeInteger(buffer, 7);
        if (buffer.remaining() < length || length == -1) {
            return null;
        }
        boolean bl = huffman = (data & 0x80) != 0;
        if (huffman) {
            return this.readHuffmanString(length, buffer);
        }
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            stringBuilder.append((char)buffer.get());
        }
        return stringBuilder.toString();
    }

    private String readHuffmanString(int length, ByteBuffer buffer) throws HpackException {
        StringBuilder stringBuilder = new StringBuilder(length);
        HPackHuffman.decode(buffer, length, stringBuilder);
        return stringBuilder.toString();
    }

    private String handleIndexedHeaderName(int index) throws HpackException {
        if (index <= Hpack.STATIC_TABLE_LENGTH) {
            return Hpack.STATIC_TABLE[index].name;
        }
        if (index > Hpack.STATIC_TABLE_LENGTH + this.filledTableSlots) {
            throw new HpackException(sm.getString("hpackdecoder.headerTableIndexInvalid", new Object[]{index, Hpack.STATIC_TABLE_LENGTH, this.filledTableSlots}));
        }
        int adjustedIndex = this.getRealIndex(index - Hpack.STATIC_TABLE_LENGTH);
        Hpack.HeaderField res = this.headerTable[adjustedIndex];
        if (res == null) {
            throw new HpackException(sm.getString("hpackdecoder.nullHeader", new Object[]{index}));
        }
        return res.name;
    }

    private void handleIndex(int index) throws HpackException {
        if (index <= Hpack.STATIC_TABLE_LENGTH) {
            this.addStaticTableEntry(index);
        } else {
            int adjustedIndex = this.getRealIndex(index - Hpack.STATIC_TABLE_LENGTH);
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("hpackdecoder.useDynamic", new Object[]{adjustedIndex}));
            }
            Hpack.HeaderField headerField = this.headerTable[adjustedIndex];
            this.emitHeader(headerField.name, headerField.value);
        }
    }

    int getRealIndex(int index) throws HpackException {
        int realIndex = (this.firstSlotPosition + (this.filledTableSlots - index)) % this.headerTable.length;
        if (realIndex < 0) {
            throw new HpackException(sm.getString("hpackdecoder.headerTableIndexInvalid", new Object[]{index, Hpack.STATIC_TABLE_LENGTH, this.filledTableSlots}));
        }
        return realIndex;
    }

    private void addStaticTableEntry(int index) throws HpackException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("hpackdecoder.useStatic", new Object[]{index}));
        }
        Hpack.HeaderField entry = Hpack.STATIC_TABLE[index];
        this.emitHeader(entry.name, entry.value == null ? "" : entry.value);
    }

    private void addEntryToHeaderTable(Hpack.HeaderField entry) {
        if (entry.size > this.maxMemorySizeSoft) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("hpackdecoder.clearDynamic"));
            }
            while (this.filledTableSlots > 0) {
                this.headerTable[this.firstSlotPosition] = null;
                ++this.firstSlotPosition;
                if (this.firstSlotPosition == this.headerTable.length) {
                    this.firstSlotPosition = 0;
                }
                --this.filledTableSlots;
            }
            this.currentMemorySize = 0;
            return;
        }
        this.resizeIfRequired();
        int newTableSlots = this.filledTableSlots + 1;
        int tableLength = this.headerTable.length;
        int index = (this.firstSlotPosition + this.filledTableSlots) % tableLength;
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("hpackdecoder.addDynamic", new Object[]{index, entry.name, entry.value}));
        }
        this.headerTable[index] = entry;
        int newSize = this.currentMemorySize + entry.size;
        while (newSize > this.maxMemorySizeSoft) {
            int clearIndex = this.firstSlotPosition++;
            if (this.firstSlotPosition == tableLength) {
                this.firstSlotPosition = 0;
            }
            Hpack.HeaderField oldData = this.headerTable[clearIndex];
            this.headerTable[clearIndex] = null;
            newSize -= oldData.size;
            --newTableSlots;
        }
        this.filledTableSlots = newTableSlots;
        this.currentMemorySize = newSize;
    }

    private void resizeIfRequired() {
        if (this.filledTableSlots == this.headerTable.length) {
            Hpack.HeaderField[] newArray = new Hpack.HeaderField[this.headerTable.length + 10];
            for (int i = 0; i < this.headerTable.length; ++i) {
                newArray[i] = this.headerTable[(this.firstSlotPosition + i) % this.headerTable.length];
            }
            this.firstSlotPosition = 0;
            this.headerTable = newArray;
        }
    }

    HeaderEmitter getHeaderEmitter() {
        return this.headerEmitter;
    }

    void setHeaderEmitter(HeaderEmitter headerEmitter) {
        this.headerEmitter = headerEmitter;
        this.headerCount = 0;
        this.countedCookie = false;
        this.headerSize = 0;
    }

    void setMaxHeaderCount(int maxHeaderCount) {
        this.maxHeaderCount = maxHeaderCount;
    }

    void setMaxHeaderSize(int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
    }

    private void emitHeader(String name, String value) throws HpackException {
        if ("cookie".equals(name)) {
            if (!this.countedCookie) {
                ++this.headerCount;
                this.countedCookie = true;
            }
        } else {
            ++this.headerCount;
        }
        int inc = 3 + name.length() + value.length();
        this.headerSize += inc;
        if (!this.isHeaderCountExceeded() && !this.isHeaderSizeExceeded(0)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("hpackdecoder.emitHeader", new Object[]{name, value}));
            }
            this.headerEmitter.emitHeader(name, value);
        }
    }

    boolean isHeaderCountExceeded() {
        if (this.maxHeaderCount < 0) {
            return false;
        }
        return this.headerCount > this.maxHeaderCount;
    }

    boolean isHeaderSizeExceeded(int unreadSize) {
        if (this.maxHeaderSize < 0) {
            return false;
        }
        return this.headerSize + unreadSize > this.maxHeaderSize;
    }

    boolean isHeaderSwallowSizeExceeded(int unreadSize) {
        if (this.maxHeaderSize < 0) {
            return false;
        }
        return this.headerSize + unreadSize > 2 * this.maxHeaderSize;
    }

    int getFirstSlotPosition() {
        return this.firstSlotPosition;
    }

    Hpack.HeaderField[] getHeaderTable() {
        return this.headerTable;
    }

    int getFilledTableSlots() {
        return this.filledTableSlots;
    }

    int getCurrentMemorySize() {
        return this.currentMemorySize;
    }

    int getMaxMemorySizeSoft() {
        return this.maxMemorySizeSoft;
    }

    static interface HeaderEmitter {
        public void emitHeader(String var1, String var2) throws HpackException;

        public void setHeaderException(StreamException var1);

        public void validateHeaders() throws StreamException;
    }
}

