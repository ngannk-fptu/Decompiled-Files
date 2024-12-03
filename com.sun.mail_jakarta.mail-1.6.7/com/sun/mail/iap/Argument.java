/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.iap;

import com.sun.mail.iap.AString;
import com.sun.mail.iap.Atom;
import com.sun.mail.iap.Literal;
import com.sun.mail.iap.LiteralException;
import com.sun.mail.iap.NString;
import com.sun.mail.iap.Protocol;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.util.ASCIIUtility;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Argument {
    protected List<Object> items = new ArrayList<Object>(1);

    public Argument append(Argument arg) {
        this.items.addAll(arg.items);
        return this;
    }

    public Argument writeString(String s) {
        this.items.add(new AString(ASCIIUtility.getBytes(s)));
        return this;
    }

    public Argument writeString(String s, String charset) throws UnsupportedEncodingException {
        if (charset == null) {
            this.writeString(s);
        } else {
            this.items.add(new AString(s.getBytes(charset)));
        }
        return this;
    }

    public Argument writeString(String s, Charset charset) {
        if (charset == null) {
            this.writeString(s);
        } else {
            this.items.add(new AString(s.getBytes(charset)));
        }
        return this;
    }

    public Argument writeNString(String s) {
        if (s == null) {
            this.items.add(new NString(null));
        } else {
            this.items.add(new NString(ASCIIUtility.getBytes(s)));
        }
        return this;
    }

    public Argument writeNString(String s, String charset) throws UnsupportedEncodingException {
        if (s == null) {
            this.items.add(new NString(null));
        } else if (charset == null) {
            this.writeString(s);
        } else {
            this.items.add(new NString(s.getBytes(charset)));
        }
        return this;
    }

    public Argument writeNString(String s, Charset charset) {
        if (s == null) {
            this.items.add(new NString(null));
        } else if (charset == null) {
            this.writeString(s);
        } else {
            this.items.add(new NString(s.getBytes(charset)));
        }
        return this;
    }

    public Argument writeBytes(byte[] b) {
        this.items.add(b);
        return this;
    }

    public Argument writeBytes(ByteArrayOutputStream b) {
        this.items.add(b);
        return this;
    }

    public Argument writeBytes(Literal b) {
        this.items.add(b);
        return this;
    }

    public Argument writeAtom(String s) {
        this.items.add(new Atom(s));
        return this;
    }

    public Argument writeNumber(int i) {
        this.items.add(i);
        return this;
    }

    public Argument writeNumber(long i) {
        this.items.add(i);
        return this;
    }

    public Argument writeArgument(Argument c) {
        this.items.add(c);
        return this;
    }

    public void write(Protocol protocol) throws IOException, ProtocolException {
        int size = this.items != null ? this.items.size() : 0;
        DataOutputStream os = (DataOutputStream)protocol.getOutputStream();
        for (int i = 0; i < size; ++i) {
            Object o;
            if (i > 0) {
                os.write(32);
            }
            if ((o = this.items.get(i)) instanceof Atom) {
                os.writeBytes(((Atom)o).string);
                continue;
            }
            if (o instanceof Number) {
                os.writeBytes(((Number)o).toString());
                continue;
            }
            if (o instanceof AString) {
                this.astring(((AString)o).bytes, protocol);
                continue;
            }
            if (o instanceof NString) {
                this.nstring(((NString)o).bytes, protocol);
                continue;
            }
            if (o instanceof byte[]) {
                this.literal((byte[])o, protocol);
                continue;
            }
            if (o instanceof ByteArrayOutputStream) {
                this.literal((ByteArrayOutputStream)o, protocol);
                continue;
            }
            if (o instanceof Literal) {
                this.literal((Literal)o, protocol);
                continue;
            }
            if (!(o instanceof Argument)) continue;
            os.write(40);
            ((Argument)o).write(protocol);
            os.write(41);
        }
    }

    private void astring(byte[] bytes, Protocol protocol) throws IOException, ProtocolException {
        this.nastring(bytes, protocol, false);
    }

    private void nstring(byte[] bytes, Protocol protocol) throws IOException, ProtocolException {
        if (bytes == null) {
            DataOutputStream os = (DataOutputStream)protocol.getOutputStream();
            os.writeBytes("NIL");
        } else {
            this.nastring(bytes, protocol, true);
        }
    }

    private void nastring(byte[] bytes, Protocol protocol, boolean doQuote) throws IOException, ProtocolException {
        byte b;
        int i;
        DataOutputStream os = (DataOutputStream)protocol.getOutputStream();
        int len = bytes.length;
        if (len > 1024) {
            this.literal(bytes, protocol);
            return;
        }
        boolean quote = len == 0 ? true : doQuote;
        boolean escape = false;
        boolean utf8 = protocol.supportsUtf8();
        for (i = 0; i < len; ++i) {
            b = bytes[i];
            if (b == 0 || b == 13 || b == 10 || !utf8 && (b & 0xFF) > 127) {
                this.literal(bytes, protocol);
                return;
            }
            if (b != 42 && b != 37 && b != 40 && b != 41 && b != 123 && b != 34 && b != 92 && (b & 0xFF) > 32 && (b & 0xFF) <= 127) continue;
            quote = true;
            if (b != 34 && b != 92) continue;
            escape = true;
        }
        if (!(quote || bytes.length != 3 || bytes[0] != 78 && bytes[0] != 110 || bytes[1] != 73 && bytes[1] != 105 || bytes[2] != 76 && bytes[2] != 108)) {
            quote = true;
        }
        if (quote) {
            os.write(34);
        }
        if (escape) {
            for (i = 0; i < len; ++i) {
                b = bytes[i];
                if (b == 34 || b == 92) {
                    os.write(92);
                }
                os.write(b);
            }
        } else {
            os.write(bytes);
        }
        if (quote) {
            os.write(34);
        }
    }

    private void literal(byte[] b, Protocol protocol) throws IOException, ProtocolException {
        this.startLiteral(protocol, b.length).write(b);
    }

    private void literal(ByteArrayOutputStream b, Protocol protocol) throws IOException, ProtocolException {
        b.writeTo(this.startLiteral(protocol, b.size()));
    }

    private void literal(Literal b, Protocol protocol) throws IOException, ProtocolException {
        b.writeTo(this.startLiteral(protocol, b.size()));
    }

    private OutputStream startLiteral(Protocol protocol, int size) throws IOException, ProtocolException {
        DataOutputStream os = (DataOutputStream)protocol.getOutputStream();
        boolean nonSync = protocol.supportsNonSyncLiterals();
        os.write(123);
        os.writeBytes(Integer.toString(size));
        if (nonSync) {
            os.writeBytes("+}\r\n");
        } else {
            os.writeBytes("}\r\n");
        }
        os.flush();
        if (!nonSync) {
            Response r;
            while (!(r = protocol.readResponse()).isContinuation()) {
                if (!r.isTagged()) continue;
                throw new LiteralException(r);
            }
        }
        return os;
    }
}

