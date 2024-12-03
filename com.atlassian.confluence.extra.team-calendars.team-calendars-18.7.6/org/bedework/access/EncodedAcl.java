/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.access;

import java.io.CharArrayWriter;
import java.io.Serializable;
import org.apache.log4j.Logger;
import org.bedework.access.AccessException;

public class EncodedAcl
implements Serializable {
    private char[] encoded;
    private int pos;
    private transient CharArrayWriter caw;
    protected static transient Logger log;
    private static boolean debug;
    private static final String[] encodedLengths;

    public void setEncoded(char[] val) {
        this.encoded = val;
        this.pos = 0;
    }

    public char[] getEncoded() {
        return this.encoded;
    }

    public String getErrorInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append("at ");
        sb.append(this.pos - 1);
        sb.append(" in '");
        sb.append(this.encoded);
        sb.append("'");
        return sb.toString();
    }

    public char getChar() {
        if (this.encoded == null || this.pos == this.encoded.length) {
            if (debug) {
                EncodedAcl.debugMsg("getChar=-1");
            }
            return '\uffff';
        }
        char c = this.encoded[this.pos];
        if (debug) {
            EncodedAcl.debugMsg("getChar='" + c + "'");
        }
        ++this.pos;
        return c;
    }

    public void back() throws AccessException {
        this.back(1);
    }

    public void back(int n) throws AccessException {
        if (this.pos - n < 0) {
            throw AccessException.badACLRewind();
        }
        this.pos -= n;
        if (debug) {
            EncodedAcl.debugMsg("pos back to " + this.pos);
        }
    }

    public int getPos() {
        return this.pos;
    }

    public void setPos(int val) {
        this.pos = val;
        if (debug) {
            EncodedAcl.debugMsg("set pos to " + this.pos);
        }
    }

    public void rewind() {
        this.pos = 0;
        if (debug) {
            EncodedAcl.debugMsg("rewind");
        }
    }

    public int remaining() {
        if (this.encoded == null) {
            return 0;
        }
        return this.encoded.length - this.pos;
    }

    public boolean hasMore() {
        return this.remaining() > 0;
    }

    public boolean empty() {
        return this.encoded == null || this.encoded.length == 0;
    }

    public int getLength() throws AccessException {
        char c;
        int res = 0;
        while ((c = this.getChar()) != ' ') {
            if (c < '\u0000') {
                throw AccessException.badACLLength();
            }
            if (c < '0' || c > '9') {
                throw AccessException.badACL("digit=" + c);
            }
            res = res * 10 + Character.digit(c, 10);
        }
        return res;
    }

    public String getString() throws AccessException {
        if (this.getChar() == 'N') {
            return null;
        }
        this.back();
        int len = this.getLength();
        if (this.encoded.length - this.pos < len) {
            throw AccessException.badACLLength();
        }
        String s = new String(this.encoded, this.pos, len);
        this.pos += len;
        return s;
    }

    public void skipString() throws AccessException {
        if (this.getChar() == 'N') {
            return;
        }
        this.back();
        int len = this.getLength();
        this.pos += len;
    }

    public String getString(int begin) throws AccessException {
        return new String(this.encoded, begin, this.pos - begin);
    }

    public void startEncoding() {
        this.caw = new CharArrayWriter();
    }

    public void encodeLength(int len) throws AccessException {
        try {
            if (len < encodedLengths.length) {
                this.caw.write(encodedLengths[len]);
                return;
            }
            String slen = String.valueOf(len);
            this.caw.write(48);
            this.caw.write(slen, 0, slen.length());
            this.caw.write(32);
        }
        catch (Throwable t) {
            throw new AccessException(t);
        }
    }

    public static String encodedLength(int len) {
        if (len < encodedLengths.length) {
            return encodedLengths[len];
        }
        return EncodedAcl.intEncodedLength(len);
    }

    private static String intEncodedLength(int len) {
        StringBuilder sb = new StringBuilder();
        sb.append('0');
        sb.append(len);
        sb.append(' ');
        return sb.toString();
    }

    public void encodeString(String val) throws AccessException {
        try {
            if (val == null) {
                this.caw.write(78);
            } else {
                this.encodeLength(val.length());
                this.caw.write(val, 0, val.length());
            }
        }
        catch (AccessException ae) {
            throw ae;
        }
        catch (Throwable t) {
            throw new AccessException(t);
        }
    }

    public static String encodedString(String val) {
        if (val == null) {
            return "N";
        }
        StringBuilder sb = new StringBuilder(EncodedAcl.encodedLength(val.length()));
        sb.append(val);
        return sb.toString();
    }

    public void addChar(char c) throws AccessException {
        try {
            this.caw.write(c);
        }
        catch (Throwable t) {
            throw new AccessException(t);
        }
    }

    public void addChar(char[] c) throws AccessException {
        try {
            this.caw.write(c);
        }
        catch (Throwable t) {
            throw new AccessException(t);
        }
    }

    public char[] getEncoding() {
        char[] enc = this.caw.toCharArray();
        this.caw = null;
        if (enc == null || enc.length == 0) {
            return null;
        }
        return enc;
    }

    protected static Logger getLog() {
        if (log == null) {
            log = Logger.getLogger(EncodedAcl.class);
        }
        return log;
    }

    protected static void debugMsg(String msg) {
        EncodedAcl.getLog().debug(msg);
    }

    protected void error(Throwable t) {
        EncodedAcl.getLog().error(this, t);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("EncodedAcl{pos=");
        sb.append(this.pos);
        sb.append("}");
        return sb.toString();
    }

    static {
        debug = false;
        encodedLengths = new String[200];
        for (int i = 0; i < encodedLengths.length; ++i) {
            EncodedAcl.encodedLengths[i] = EncodedAcl.intEncodedLength(i);
        }
    }
}

