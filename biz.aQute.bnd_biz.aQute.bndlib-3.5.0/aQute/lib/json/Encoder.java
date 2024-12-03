/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.json;

import aQute.lib.io.IO;
import aQute.lib.json.JSONCodec;
import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;

public class Encoder
implements Appendable,
Closeable,
Flushable {
    final JSONCodec codec;
    Appendable app;
    MessageDigest digest;
    boolean writeDefaults;
    Charset encoding = StandardCharsets.UTF_8;
    boolean deflate;
    String tabs = null;
    String indent = "";
    boolean keepOpen = false;
    boolean closed = false;

    Encoder(JSONCodec codec) {
        this.codec = codec;
    }

    public Encoder put(Object object) throws Exception {
        if (this.app == null) {
            this.to();
        }
        this.codec.encode(this, object, null, new IdentityHashMap<Object, Type>());
        this.flush();
        if (!this.keepOpen) {
            this.close();
        }
        return this;
    }

    public Encoder mark() throws NoSuchAlgorithmException {
        if (this.digest == null) {
            this.digest = MessageDigest.getInstance("SHA1");
        }
        this.digest.reset();
        return this;
    }

    public byte[] digest() throws NoSuchAlgorithmException, IOException {
        if (this.digest == null) {
            return null;
        }
        this.append('\n');
        return this.digest.digest();
    }

    public Encoder to() throws IOException {
        this.to(new StringWriter());
        return this;
    }

    public Encoder to(File file) throws IOException {
        return this.to(IO.outputStream(file));
    }

    public Encoder charset(String encoding) {
        return this.charset(Charset.forName(encoding));
    }

    public Encoder charset(Charset encoding) {
        this.encoding = encoding;
        return this;
    }

    public Encoder to(OutputStream out) throws IOException {
        if (this.deflate) {
            out = new DeflaterOutputStream(out);
        }
        return this.to(new OutputStreamWriter(out, this.encoding));
    }

    public Encoder to(Appendable out) throws IOException {
        this.app = out;
        return this;
    }

    @Override
    public Appendable append(char c) throws IOException {
        if (this.digest != null) {
            this.digest.update((byte)(c / 256));
            this.digest.update((byte)(c % 256));
        }
        this.app.append(c);
        return this;
    }

    @Override
    public Appendable append(CharSequence sq) throws IOException {
        return this.append(sq, 0, sq.length());
    }

    @Override
    public Appendable append(CharSequence sq, int start, int length) throws IOException {
        if (this.digest != null) {
            for (int i = start; i < length; ++i) {
                char c = sq.charAt(i);
                this.digest.update((byte)(c / 256));
                this.digest.update((byte)(c % 256));
            }
        }
        this.app.append(sq, start, length);
        return this;
    }

    public String toString() {
        return this.app.toString();
    }

    @Override
    public void close() throws IOException {
        if (this.app != null && this.app instanceof Closeable) {
            ((Closeable)((Object)this.app)).close();
            this.closed = true;
        }
    }

    void encode(Object object, Type type, Map<Object, Type> visited) throws Exception {
        this.codec.encode(this, object, type, visited);
    }

    public Encoder writeDefaults() {
        this.writeDefaults = true;
        return this;
    }

    @Override
    public void flush() throws IOException {
        if (this.closed) {
            return;
        }
        if (this.app instanceof Flushable) {
            ((Flushable)((Object)this.app)).flush();
        }
    }

    public Encoder deflate() {
        if (this.app != null) {
            throw new IllegalStateException("Writer already set, deflate must come before to(...)");
        }
        this.deflate = true;
        return this;
    }

    public Encoder indent(String tabs) {
        this.tabs = tabs;
        return this;
    }

    void undent() throws IOException {
        if (this.tabs != null) {
            this.app.append("\n");
            this.indent = this.indent.substring(this.tabs.length());
            this.app.append(this.indent);
        }
    }

    void indent() throws IOException {
        if (this.tabs != null) {
            this.app.append("\n");
            this.indent = this.indent + this.tabs;
            this.app.append(this.indent);
        }
    }

    public Encoder keepOpen() {
        this.keepOpen = true;
        return this;
    }
}

