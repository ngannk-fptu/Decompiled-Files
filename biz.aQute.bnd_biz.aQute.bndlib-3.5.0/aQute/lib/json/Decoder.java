/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.json;

import aQute.lib.converter.TypeReference;
import aQute.lib.io.IO;
import aQute.lib.json.JSONCodec;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.InflaterInputStream;

public class Decoder
implements Closeable {
    final JSONCodec codec;
    Reader reader;
    int current;
    MessageDigest digest;
    Map<String, Object> extra;
    Charset encoding = StandardCharsets.UTF_8;
    boolean strict;
    boolean inflate;
    boolean keepOpen = false;

    Decoder(JSONCodec codec) {
        this.codec = codec;
    }

    public Decoder from(File file) throws Exception {
        return this.from(IO.stream(file));
    }

    public Decoder from(InputStream in) throws Exception {
        if (this.inflate) {
            in = new InflaterInputStream(in);
        }
        return this.from(new InputStreamReader(in, this.encoding));
    }

    public Decoder from(byte[] data) throws Exception {
        return this.from(new ByteArrayInputStream(data));
    }

    public Decoder charset(String encoding) {
        return this.charset(Charset.forName(encoding));
    }

    public Decoder charset(Charset encoding) {
        this.encoding = encoding;
        return this;
    }

    public Decoder strict() {
        this.strict = true;
        return this;
    }

    public Decoder from(Reader in) throws Exception {
        this.reader = in;
        this.read();
        return this;
    }

    public Decoder faq(String in) throws Exception {
        return this.from(in.replace('\'', '\"'));
    }

    public Decoder from(String in) throws Exception {
        return this.from(new StringReader(in));
    }

    public Decoder mark() throws NoSuchAlgorithmException {
        if (this.digest == null) {
            this.digest = MessageDigest.getInstance("SHA1");
        }
        this.digest.reset();
        return this;
    }

    public byte[] digest() {
        if (this.digest == null) {
            return null;
        }
        return this.digest.digest();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T get(Class<T> clazz) throws Exception {
        try {
            Object object = this.codec.decode(clazz, this);
            return (T)object;
        }
        finally {
            if (!this.keepOpen) {
                this.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object get(Type type) throws Exception {
        try {
            Object object = this.codec.decode(type, this);
            return object;
        }
        finally {
            if (!this.keepOpen) {
                this.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object get() throws Exception {
        try {
            Object object = this.codec.decode(null, this);
            return object;
        }
        finally {
            if (!this.keepOpen) {
                this.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T get(TypeReference<T> ref) throws Exception {
        try {
            Object object = this.codec.decode(ref.getType(), this);
            return (T)object;
        }
        finally {
            if (!this.keepOpen) {
                this.close();
            }
        }
    }

    public Decoder keepOpen() {
        this.keepOpen = true;
        return this;
    }

    int read() throws Exception {
        this.current = this.reader.read();
        if (this.digest != null) {
            this.digest.update((byte)(this.current / 256));
            this.digest.update((byte)(this.current % 256));
        }
        return this.current;
    }

    int current() {
        return this.current;
    }

    int skipWs() throws Exception {
        while (Character.isWhitespace(this.current())) {
            this.read();
        }
        return this.current();
    }

    int next() throws Exception {
        this.read();
        return this.skipWs();
    }

    void expect(String s) throws Exception {
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == this.read()) continue;
            throw new IllegalArgumentException("Expected " + s + " but got something different");
        }
        this.read();
    }

    public boolean isEof() throws Exception {
        int c = this.skipWs();
        return c < 0;
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }

    public Map<String, Object> getExtra() {
        if (this.extra == null) {
            this.extra = new HashMap<String, Object>();
        }
        return this.extra;
    }

    public Decoder inflate() {
        if (this.reader != null) {
            throw new IllegalStateException("Reader already set, inflate must come before from()");
        }
        this.inflate = true;
        return this;
    }
}

