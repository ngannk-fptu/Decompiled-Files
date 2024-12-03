/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.poifs.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.Ole10NativeException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutputStream;
import org.apache.poi.util.StringUtil;

public class Ole10Native {
    public static final String OLE10_NATIVE = "\u0001Ole10Native";
    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private static final int DEFAULT_MAX_RECORD_LENGTH = 100000000;
    private static int MAX_RECORD_LENGTH = 100000000;
    private static final int DEFAULT_MAX_STRING_LENGTH = 1024;
    private static int MAX_STRING_LENGTH = 1024;
    private static final byte[] OLE_MARKER_BYTES = new byte[]{1, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final String OLE_MARKER_NAME = "\u0001Ole";
    private int totalSize;
    private short flags1 = (short)2;
    private String label;
    private String fileName;
    private short flags2;
    private short unknown1 = (short)3;
    private String command;
    private byte[] dataBuffer;
    private String command2;
    private String label2;
    private String fileName2;
    private EncodingMode mode;

    public static Ole10Native createFromEmbeddedOleObject(POIFSFileSystem poifs) throws IOException, Ole10NativeException {
        return Ole10Native.createFromEmbeddedOleObject(poifs.getRoot());
    }

    public static Ole10Native createFromEmbeddedOleObject(DirectoryNode directory) throws IOException, Ole10NativeException {
        DocumentEntry nativeEntry = (DocumentEntry)directory.getEntry(OLE10_NATIVE);
        try (DocumentInputStream dis = directory.createDocumentInputStream(nativeEntry);){
            byte[] data = IOUtils.toByteArray(dis, nativeEntry.getSize(), MAX_RECORD_LENGTH);
            Ole10Native ole10Native = new Ole10Native(data, 0);
            return ole10Native;
        }
    }

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public static void setMaxStringLength(int length) {
        MAX_STRING_LENGTH = length;
    }

    public static int getMaxStringLength() {
        return MAX_STRING_LENGTH;
    }

    public Ole10Native(String label, String filename, String command, byte[] data) {
        this.setLabel(label);
        this.setFileName(filename);
        this.setCommand(command);
        this.command2 = command;
        this.setDataBuffer(data);
        this.mode = EncodingMode.parsed;
    }

    public Ole10Native(byte[] data, int offset) throws Ole10NativeException {
        LittleEndianByteArrayInputStream leis = new LittleEndianByteArrayInputStream(data, offset);
        this.totalSize = leis.readInt();
        leis.limit(this.totalSize + 4);
        leis.mark(0);
        try {
            this.flags1 = leis.readShort();
            if (this.flags1 == 2) {
                leis.mark(0);
                boolean validFileName = !Character.isISOControl(leis.readByte());
                leis.reset();
                if (validFileName) {
                    this.readParsed(leis);
                } else {
                    this.readCompact(leis);
                }
            } else {
                leis.reset();
                this.readUnparsed(leis);
            }
        }
        catch (IOException e) {
            throw new Ole10NativeException("Invalid Ole10Native", e);
        }
    }

    private void readParsed(LittleEndianByteArrayInputStream leis) throws Ole10NativeException, IOException {
        this.mode = EncodingMode.parsed;
        this.label = Ole10Native.readAsciiZ(leis);
        this.fileName = Ole10Native.readAsciiZ(leis);
        this.flags2 = leis.readShort();
        this.unknown1 = leis.readShort();
        this.command = Ole10Native.readAsciiLen(leis);
        this.dataBuffer = IOUtils.toByteArray(leis, leis.readInt(), MAX_RECORD_LENGTH);
        leis.mark(0);
        short lowSize = leis.readShort();
        if (lowSize != 0) {
            leis.reset();
            this.command2 = Ole10Native.readUtf16(leis);
            this.label2 = Ole10Native.readUtf16(leis);
            this.fileName2 = Ole10Native.readUtf16(leis);
        }
    }

    private void readCompact(LittleEndianByteArrayInputStream leis) throws IOException {
        this.mode = EncodingMode.compact;
        this.dataBuffer = IOUtils.toByteArray(leis, this.totalSize - 2, MAX_RECORD_LENGTH);
    }

    private void readUnparsed(LittleEndianByteArrayInputStream leis) throws IOException {
        this.mode = EncodingMode.unparsed;
        this.dataBuffer = IOUtils.toByteArray(leis, this.totalSize, MAX_RECORD_LENGTH);
    }

    public static void createOleMarkerEntry(DirectoryEntry parent) throws IOException {
        if (!parent.hasEntry(OLE_MARKER_NAME)) {
            parent.createDocument(OLE_MARKER_NAME, (InputStream)new UnsynchronizedByteArrayInputStream(OLE_MARKER_BYTES));
        }
    }

    public static void createOleMarkerEntry(POIFSFileSystem poifs) throws IOException {
        Ole10Native.createOleMarkerEntry(poifs.getRoot());
    }

    private static String readAsciiZ(LittleEndianInput is) throws Ole10NativeException {
        byte[] buf = new byte[MAX_STRING_LENGTH];
        for (int i = 0; i < buf.length; ++i) {
            buf[i] = is.readByte();
            if (buf[i] != 0) continue;
            return StringUtil.getFromCompressedUTF8(buf, 0, i);
        }
        throw new Ole10NativeException("AsciiZ string was not null terminated after " + MAX_STRING_LENGTH + " bytes - Exiting.");
    }

    private static String readAsciiLen(LittleEndianByteArrayInputStream leis) throws IOException {
        int size = leis.readInt();
        byte[] buf = IOUtils.toByteArray(leis, size, MAX_STRING_LENGTH);
        return buf.length == 0 ? "" : StringUtil.getFromCompressedUnicode(buf, 0, size - 1);
    }

    private static String readUtf16(LittleEndianByteArrayInputStream leis) throws IOException {
        int size = leis.readInt();
        byte[] buf = IOUtils.toByteArray(leis, size * 2, MAX_STRING_LENGTH);
        return StringUtil.getFromUnicodeLE(buf, 0, size);
    }

    public int getTotalSize() {
        return this.totalSize;
    }

    public short getFlags1() {
        return this.flags1;
    }

    public String getLabel() {
        return this.label;
    }

    public String getFileName() {
        return this.fileName;
    }

    public short getFlags2() {
        return this.flags2;
    }

    public short getUnknown1() {
        return this.unknown1;
    }

    public String getCommand() {
        return this.command;
    }

    public int getDataSize() {
        return this.dataBuffer.length;
    }

    public byte[] getDataBuffer() {
        return this.dataBuffer;
    }

    public void writeOut(OutputStream out) throws IOException {
        LittleEndianOutputStream leosOut = new LittleEndianOutputStream(out);
        switch (this.mode) {
            case parsed: {
                UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();
                try (LittleEndianOutputStream leos = new LittleEndianOutputStream((OutputStream)bos);){
                    leos.writeShort(this.getFlags1());
                    leos.write(this.getLabel().getBytes(UTF8));
                    leos.write(0);
                    leos.write(this.getFileName().getBytes(UTF8));
                    leos.write(0);
                    leos.writeShort(this.getFlags2());
                    leos.writeShort(this.getUnknown1());
                    leos.writeInt(this.getCommand().length() + 1);
                    leos.write(this.getCommand().getBytes(UTF8));
                    leos.write(0);
                    leos.writeInt(this.getDataSize());
                    leos.write(this.getDataBuffer());
                    if (this.command2 == null || this.label2 == null || this.fileName2 == null) {
                        leos.writeShort(0);
                    } else {
                        leos.writeUInt(this.command2.length());
                        leos.write(StringUtil.getToUnicodeLE(this.command2));
                        leos.writeUInt(this.label2.length());
                        leos.write(StringUtil.getToUnicodeLE(this.label2));
                        leos.writeUInt(this.fileName2.length());
                        leos.write(StringUtil.getToUnicodeLE(this.fileName2));
                    }
                }
                leosOut.writeInt(bos.size());
                bos.writeTo(out);
                break;
            }
            case compact: {
                leosOut.writeInt(this.getDataSize() + 2);
                leosOut.writeShort(this.getFlags1());
                out.write(this.getDataBuffer());
                break;
            }
            default: {
                leosOut.writeInt(this.getDataSize());
                out.write(this.getDataBuffer());
            }
        }
    }

    public void setFlags1(short flags1) {
        this.flags1 = flags1;
    }

    public void setFlags2(short flags2) {
        this.flags2 = flags2;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setUnknown1(short unknown1) {
        this.unknown1 = unknown1;
    }

    public void setDataBuffer(byte[] dataBuffer) {
        this.dataBuffer = (byte[])dataBuffer.clone();
    }

    public String getCommand2() {
        return this.command2;
    }

    public void setCommand2(String command2) {
        this.command2 = command2;
    }

    public String getLabel2() {
        return this.label2;
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }

    public String getFileName2() {
        return this.fileName2;
    }

    public void setFileName2(String fileName2) {
        this.fileName2 = fileName2;
    }

    private static enum EncodingMode {
        parsed,
        unparsed,
        compact;

    }
}

