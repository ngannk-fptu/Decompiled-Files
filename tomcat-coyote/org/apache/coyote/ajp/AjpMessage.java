/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.buf.HexUtils
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.ajp;

import java.nio.ByteBuffer;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.res.StringManager;

public class AjpMessage {
    private static final Log log = LogFactory.getLog(AjpMessage.class);
    protected static final StringManager sm = StringManager.getManager(AjpMessage.class);
    protected final byte[] buf;
    protected int pos;
    protected int len;

    public AjpMessage(int packetSize) {
        this.buf = new byte[packetSize];
    }

    public void reset() {
        this.len = 4;
        this.pos = 4;
    }

    public void end() {
        this.len = this.pos;
        int dLen = this.len - 4;
        this.buf[0] = 65;
        this.buf[1] = 66;
        this.buf[2] = (byte)(dLen >>> 8 & 0xFF);
        this.buf[3] = (byte)(dLen & 0xFF);
    }

    public byte[] getBuffer() {
        return this.buf;
    }

    public int getLen() {
        return this.len;
    }

    public void appendInt(int val) {
        this.buf[this.pos++] = (byte)(val >>> 8 & 0xFF);
        this.buf[this.pos++] = (byte)(val & 0xFF);
    }

    public void appendByte(int val) {
        this.buf[this.pos++] = (byte)val;
    }

    public void appendBytes(MessageBytes mb) {
        if (mb == null) {
            log.error((Object)sm.getString("ajpmessage.null"), (Throwable)new NullPointerException());
            this.appendInt(0);
            this.appendByte(0);
            return;
        }
        if (mb.getType() != 2) {
            mb.toBytes();
            ByteChunk bc = mb.getByteChunk();
            byte[] buffer = bc.getBuffer();
            for (int i = bc.getOffset(); i < bc.getLength(); ++i) {
                if ((buffer[i] <= -1 || buffer[i] > 31 || buffer[i] == 9) && buffer[i] != 127) continue;
                buffer[i] = 32;
            }
        }
        this.appendByteChunk(mb.getByteChunk());
    }

    public void appendByteChunk(ByteChunk bc) {
        if (bc == null) {
            log.error((Object)sm.getString("ajpmessage.null"), (Throwable)new NullPointerException());
            this.appendInt(0);
            this.appendByte(0);
            return;
        }
        this.appendBytes(bc.getBytes(), bc.getStart(), bc.getLength());
    }

    public void appendBytes(byte[] b, int off, int numBytes) {
        if (this.checkOverflow(numBytes)) {
            return;
        }
        this.appendInt(numBytes);
        System.arraycopy(b, off, this.buf, this.pos, numBytes);
        this.pos += numBytes;
        this.appendByte(0);
    }

    public void appendBytes(ByteBuffer b) {
        int numBytes = b.remaining();
        if (this.checkOverflow(numBytes)) {
            return;
        }
        this.appendInt(numBytes);
        b.get(this.buf, this.pos, numBytes);
        this.pos += numBytes;
        this.appendByte(0);
    }

    private boolean checkOverflow(int numBytes) {
        if (this.pos + numBytes + 3 > this.buf.length) {
            log.error((Object)sm.getString("ajpmessage.overflow", new Object[]{"" + numBytes, "" + this.pos}), (Throwable)new ArrayIndexOutOfBoundsException());
            if (log.isDebugEnabled()) {
                this.dump("Overflow/coBytes");
            }
            return true;
        }
        return false;
    }

    public int getInt() {
        int b1 = this.buf[this.pos++] & 0xFF;
        int b2 = this.buf[this.pos++] & 0xFF;
        this.validatePos(this.pos);
        return (b1 << 8) + b2;
    }

    public int peekInt() {
        this.validatePos(this.pos + 2);
        int b1 = this.buf[this.pos] & 0xFF;
        int b2 = this.buf[this.pos + 1] & 0xFF;
        return (b1 << 8) + b2;
    }

    public byte getByte() {
        byte res = this.buf[this.pos++];
        this.validatePos(this.pos);
        return res;
    }

    public void getBytes(MessageBytes mb) {
        this.doGetBytes(mb, true);
    }

    public void getBodyBytes(MessageBytes mb) {
        this.doGetBytes(mb, false);
    }

    private void doGetBytes(MessageBytes mb, boolean terminated) {
        int length = this.getInt();
        if (length == 65535 || length == -1) {
            mb.recycle();
            return;
        }
        if (terminated) {
            this.validatePos(this.pos + length + 1);
        } else {
            this.validatePos(this.pos + length);
        }
        mb.setBytes(this.buf, this.pos, length);
        mb.getCharChunk().recycle();
        this.pos += length;
        if (terminated) {
            ++this.pos;
        }
    }

    public int getLongInt() {
        int b1 = this.buf[this.pos++] & 0xFF;
        b1 <<= 8;
        b1 |= this.buf[this.pos++] & 0xFF;
        b1 <<= 8;
        b1 |= this.buf[this.pos++] & 0xFF;
        b1 <<= 8;
        this.validatePos(this.pos);
        return b1 |= this.buf[this.pos++] & 0xFF;
    }

    public int processHeader(boolean toContainer) {
        this.pos = 0;
        int mark = this.getInt();
        this.len = this.getInt();
        if (toContainer && mark != 4660 || !toContainer && mark != 16706) {
            log.error((Object)sm.getString("ajpmessage.invalid", new Object[]{"" + mark}));
            if (log.isDebugEnabled()) {
                this.dump("In");
            }
            return -1;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Received " + this.len + " " + this.buf[0]));
        }
        return this.len;
    }

    private void dump(String prefix) {
        if (log.isDebugEnabled()) {
            log.debug((Object)(prefix + ": " + HexUtils.toHexString((byte[])this.buf) + " " + this.pos + "/" + (this.len + 4)));
        }
        int max = this.pos;
        if (this.len + 4 > this.pos) {
            max = this.len + 4;
        }
        if (max > 1000) {
            max = 1000;
        }
        if (log.isDebugEnabled()) {
            for (int j = 0; j < max; j += 16) {
                log.debug((Object)AjpMessage.hexLine(this.buf, j, this.len));
            }
        }
    }

    private void validatePos(int posToTest) {
        if (posToTest > this.len + 4) {
            throw new ArrayIndexOutOfBoundsException(sm.getString("ajpMessage.invalidPos", new Object[]{posToTest}));
        }
    }

    protected static String hexLine(byte[] buf, int start, int len) {
        int i;
        StringBuilder sb = new StringBuilder();
        for (i = start; i < start + 16; ++i) {
            if (i < len + 4) {
                sb.append(AjpMessage.hex(buf[i])).append(' ');
                continue;
            }
            sb.append("   ");
        }
        sb.append(" | ");
        for (i = start; i < start + 16 && i < len + 4; ++i) {
            if (!Character.isISOControl((char)buf[i])) {
                sb.append(Character.valueOf((char)buf[i]));
                continue;
            }
            sb.append('.');
        }
        return sb.toString();
    }

    protected static String hex(int x) {
        String h = Integer.toHexString(x);
        if (h.length() == 1) {
            h = "0" + h;
        }
        return h.substring(h.length() - 2);
    }
}

