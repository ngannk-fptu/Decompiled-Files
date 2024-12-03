/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.ANTLRInputStream
 *  org.antlr.v4.runtime.misc.Interval
 */
package cz.vutbr.web.csskit.antlr4;

import cz.vutbr.web.css.NetworkProcessor;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.misc.Interval;

public class CSSInputStream
extends ANTLRInputStream {
    private ANTLRInputStream input;
    private String rawData;
    private URL base = null;
    private URL url;
    private NetworkProcessor network;
    private InputStream source = null;
    private String encoding;

    public static CSSInputStream stringStream(String source) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(source.getBytes());
        String encoding = Charset.defaultCharset().name();
        BufferedReader br = new BufferedReader(new InputStreamReader((InputStream)is, encoding));
        CSSInputStream stream = new CSSInputStream();
        stream.rawData = source;
        stream.encoding = encoding;
        stream.input = new ANTLRInputStream((Reader)br);
        return stream;
    }

    public static CSSInputStream urlStream(URL source, NetworkProcessor network, String encoding) throws IOException {
        InputStream is = network.fetch(source);
        if (encoding == null) {
            encoding = Charset.defaultCharset().name();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
        CSSInputStream stream = new CSSInputStream();
        stream.base = source;
        stream.network = network;
        stream.url = source;
        stream.encoding = encoding;
        stream.source = is;
        stream.input = new ANTLRInputStream((Reader)br);
        return stream;
    }

    private CSSInputStream() {
    }

    public int LA(int i) {
        return this.input.LA(i);
    }

    public int LT(int i) {
        return this.input.LT(i);
    }

    public void consume() {
        this.input.consume();
    }

    public String getText(Interval interval) {
        return this.input.getText(interval);
    }

    public int index() {
        return this.input.index();
    }

    public void load(Reader arg0, int arg1, int arg2) throws IOException {
        this.input.load(arg0, arg1, arg2);
    }

    public int mark() {
        return this.input.mark();
    }

    public void release(int marker) {
        this.input.release(marker);
    }

    public void reset() {
        this.input.reset();
    }

    public void seek(int index) {
        this.input.seek(index);
    }

    public int size() {
        return this.input.size();
    }

    public String getSourceName() {
        return this.base != null ? this.base.toString() : "";
    }

    public URL getBase() {
        return this.base;
    }

    public void setBase(URL base) {
        this.base = base;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String enc) throws IOException {
        if (this.source != null) {
            String current = this.encoding;
            if (current == null) {
                current = Charset.defaultCharset().name();
            }
            if (!current.equalsIgnoreCase(enc)) {
                int oldindex = this.input.index();
                this.source.close();
                this.encoding = enc;
                CSSInputStream newstream = CSSInputStream.urlStream(this.url, this.network, this.encoding);
                this.input = newstream.input;
                this.input.seek(oldindex);
            }
        }
    }

    public String getRawData() {
        return this.rawData;
    }

    public String toString() {
        return "[CSSInputStream - base: " + this.getBase() + ", encoding: " + this.getEncoding();
    }
}

