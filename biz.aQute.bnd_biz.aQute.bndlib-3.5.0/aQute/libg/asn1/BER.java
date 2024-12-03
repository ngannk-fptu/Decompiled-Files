/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.asn1;

import aQute.libg.asn1.PDU;
import aQute.libg.asn1.Types;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BER
implements Types {
    static final DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss\\Z");
    final DataInputStream xin;
    long position;

    public BER(InputStream in) {
        this.xin = new DataInputStream(in);
    }

    public void dump(PrintStream out) throws Exception {
        int type = this.readByte();
        long length = this.readLength();
        if (type == -1 || length == -1L) {
            throw new EOFException("Empty file");
        }
        this.dump(out, type, length, "");
    }

    void dump(PrintStream out, int type, long length, String indent) throws Exception {
        int clss = type >> 6;
        int nmbr = type & 0x1F;
        boolean cnst = (type & 0x20) != 0;
        String tag = "[" + nmbr + "]";
        if (clss == 0) {
            tag = TAGS[nmbr];
        }
        if (cnst) {
            System.err.printf("%5d %s %s %s%n", length, indent, CLASSES[clss], tag);
            while (length > 1L) {
                long atStart = this.getPosition();
                int t2 = this.read();
                long l2 = this.readLength();
                this.dump(out, t2, l2, indent + "  ");
                length -= this.getPosition() - atStart;
            }
        } else {
            String summary;
            assert (length < Integer.MAX_VALUE);
            assert (length >= 0L);
            byte[] data = new byte[(int)length];
            this.readFully(data);
            switch (nmbr) {
                case 1: {
                    assert (length == 1L);
                    summary = data[0] != 0 ? "true" : "false";
                    break;
                }
                case 2: {
                    long n = this.toLong(data);
                    summary = n + "";
                    break;
                }
                case 12: 
                case 19: 
                case 22: 
                case 23: 
                case 26: 
                case 28: {
                    summary = new String(data, StandardCharsets.UTF_8);
                    break;
                }
                case 6: {
                    summary = this.readOID(data);
                    break;
                }
                default: {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 10 && i < data.length; ++i) {
                        sb.append(Integer.toHexString(data[i]));
                    }
                    if (data.length > 10) {
                        sb.append("...");
                    }
                    summary = sb.toString();
                }
            }
            out.printf("%5d %s %s %s %s\n", length, indent, CLASSES[clss], tag, summary);
        }
    }

    long toLong(byte[] data) {
        if (data[0] < 0) {
            for (int i = 0; i < data.length; ++i) {
                data[i] = (byte)(0xFF ^ data[i]);
            }
            return -(this.toLong(data) + 1L);
        }
        long n = 0L;
        for (int i = 0; i < data.length; ++i) {
            n = n * 256L + (long)data[i];
        }
        return n;
    }

    private long readLength() throws IOException {
        long n = this.readByte();
        if (n > 0L) {
            return n;
        }
        int count = (int)(n & 0x7FL);
        if (count == 0) {
            return 0L;
        }
        n = 0L;
        while (count-- > 0) {
            n = n * 256L + (long)this.read();
        }
        return n;
    }

    private int readByte() throws IOException {
        ++this.position;
        return this.xin.readByte();
    }

    private void readFully(byte[] data) throws IOException {
        this.position += (long)data.length;
        this.xin.readFully(data);
    }

    private long getPosition() {
        return this.position;
    }

    private int read() throws IOException {
        ++this.position;
        return this.xin.read();
    }

    String readOID(byte[] data) {
        StringBuilder sb = new StringBuilder();
        sb.append((0xFF & data[0]) / 40);
        sb.append(".");
        sb.append((0xFF & data[0]) % 40);
        int i = 0;
        while (++i < data.length) {
            int n = 0;
            while (data[i] < 0) {
                n = n * 128 + (0x7F & data[i]);
                ++i;
            }
            n = n * 128 + data[i];
            sb.append(".");
            sb.append(n);
        }
        return sb.toString();
    }

    int getPayloadLength(PDU pdu) throws Exception {
        switch (pdu.getTag() & 0x1F) {
            case 0: {
                return 1;
            }
            case 1: {
                return 1;
            }
            case 2: {
                return this.size(pdu.getInt());
            }
            case 12: {
                String s = pdu.getString();
                byte[] encoded = s.getBytes(StandardCharsets.UTF_8);
                return encoded.length;
            }
            case 18: 
            case 19: 
            case 22: 
            case 23: 
            case 24: 
            case 25: 
            case 26: 
            case 27: 
            case 28: 
            case 29: {
                String str = pdu.getString();
                byte[] encoded = str.getBytes("ASCII");
                return encoded.length;
            }
            case 3: 
            case 6: 
            case 9: {
                return pdu.getBytes().length;
            }
            case 4: 
            case 5: 
            case 7: 
            case 8: 
            case 10: 
            case 11: 
            case 13: 
            case 20: 
            case 21: 
            case 30: {
                return pdu.getBytes().length;
            }
        }
        throw new IllegalArgumentException("Invalid type: " + pdu);
    }

    int size(long value) {
        if (value < 128L) {
            return 1;
        }
        if (value <= 255L) {
            return 2;
        }
        if (value <= 65535L) {
            return 3;
        }
        if (value <= 0xFFFFFFL) {
            return 4;
        }
        if (value <= -1L) {
            return 5;
        }
        if (value <= 0xFFFFFFFFFFL) {
            return 6;
        }
        if (value <= 0xFFFFFFFFFFFFL) {
            return 7;
        }
        if (value <= 0xFFFFFFFFFFFFFFL) {
            return 8;
        }
        if (value <= -1L) {
            return 9;
        }
        throw new IllegalArgumentException("length too long");
    }

    public void write(OutputStream out, PDU pdu) throws Exception {
        int tag;
        int id = 0;
        switch (pdu.getClss()) {
            case 0: {
                id = (byte)(id | 0);
                break;
            }
            case 0x40000000: {
                id = (byte)(id | 0x40);
                break;
            }
            case -2147483648: {
                id = (byte)(id | 0x80);
                break;
            }
            case -1073741824: {
                id = (byte)(id | 0xC0);
            }
        }
        if (pdu.isConstructed()) {
            id = (byte)(id | 0x20);
        }
        if ((tag = pdu.getTag()) < 0 || tag >= 31) {
            throw new UnsupportedOperationException("Cant do tags > 30");
        }
        id = (byte)(id | tag);
        out.write(id);
        int length = this.getPayloadLength(pdu);
        int size = this.size(length);
        if (size == 1) {
            out.write(length);
        } else {
            out.write(size);
            while (--size >= 0) {
                byte data = (byte)(length >> size * 8 & 0xFF);
                out.write(data);
            }
        }
        this.writePayload(out, pdu);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void writePayload(OutputStream out, PDU pdu) throws Exception {
        switch (pdu.getTag()) {
            case 0: {
                out.write(0);
                break;
            }
            case 1: {
                if (pdu.getBoolean().booleanValue()) {
                    out.write(-1);
                    break;
                }
                out.write(0);
                break;
            }
            case 2: 
            case 10: {
                int size;
                long value = pdu.getInt();
                for (int i = size = this.size(value); i >= 0; --i) {
                    byte b = (byte)(value >> i * 8 & 0xFFL);
                    out.write(b);
                }
            }
            case 3: {
                byte[] bytes = pdu.getBytes();
                byte unused = bytes[0];
                assert (unused <= 7);
                int[] mask = new int[]{255, 127, 63, 31, 15, 7, 3, 1};
                int n = bytes.length - 1;
                bytes[n] = (byte)(bytes[n] & (byte)mask[unused]);
                out.write(bytes);
                break;
            }
            case 6: 
            case 13: {
                int[] oid = pdu.getOID();
                assert (oid.length > 2);
                assert (oid[0] < 4);
                assert (oid[1] < 40);
                byte top = (byte)(oid[0] * 40 + oid[1]);
                out.write(top);
                for (int i = 2; i < oid.length; ++i) {
                    this.putOid(out, oid[i]);
                }
                break;
            }
            case 4: {
                byte[] bytes = pdu.getBytes();
                out.write(bytes);
                break;
            }
            case 5: {
                break;
            }
            case 8: 
            case 9: 
            case 11: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 25: 
            case 26: 
            case 27: 
            case 28: 
            case 29: 
            case 30: {
                throw new UnsupportedEncodingException("dont know real, embedded PDV or external");
            }
            case 12: {
                String s = pdu.getString();
                byte[] data = s.getBytes(StandardCharsets.UTF_8);
                out.write(data);
                break;
            }
            case 7: 
            case 22: {
                String s = pdu.getString();
                byte[] data = s.getBytes("ASCII");
                out.write(data);
                break;
            }
            case 16: 
            case 17: {
                PDU[] pdus;
                for (PDU p : pdus = pdu.getChildren()) {
                    this.write(out, p);
                }
            }
            case 23: 
            case 24: {
                Date date = pdu.getDate();
                DateFormat dateFormat = df;
                synchronized (dateFormat) {
                    String ss = df.format(date);
                    byte[] d = ss.getBytes("ASCII");
                    out.write(d);
                    break;
                }
            }
        }
    }

    private void putOid(OutputStream out, int i) throws IOException {
        if (i > 127) {
            this.putOid(out, i >> 7);
            out.write(128 + (i & 0x7F));
        } else {
            out.write(i & 0x7F);
        }
    }
}

