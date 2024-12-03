/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.utf8properties;

import aQute.lib.io.IO;
import aQute.lib.io.NonClosingInputStream;
import aQute.lib.io.NonClosingReader;
import aQute.lib.utf8properties.PropertiesParser;
import aQute.lib.utf8properties.ThreadLocalCharsetDecoder;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class UTF8Properties
extends Properties {
    private static final long serialVersionUID = 1L;
    private static final List<ThreadLocalCharsetDecoder> decoders = Collections.unmodifiableList(Arrays.asList(new ThreadLocalCharsetDecoder(StandardCharsets.UTF_8), new ThreadLocalCharsetDecoder(StandardCharsets.ISO_8859_1)));

    public UTF8Properties(Properties p) {
        super(p);
    }

    public UTF8Properties() {
    }

    public void load(InputStream in, File file, Reporter reporter) throws IOException {
        String source = this.decode(IO.read(in));
        this.load(source, file, reporter);
    }

    public void load(String source, File file, Reporter reporter) throws IOException {
        PropertiesParser parser = new PropertiesParser(source, file == null ? null : file.getAbsolutePath(), reporter, this);
        parser.parse();
    }

    public void load(File file, Reporter reporter) throws Exception {
        String source = this.decode(IO.read(file));
        this.load(source, file, reporter);
    }

    @Override
    public void load(InputStream in) throws IOException {
        this.load(new NonClosingInputStream(in), null, null);
    }

    @Override
    public void load(Reader r) throws IOException {
        String source = IO.collect(new NonClosingReader(r));
        this.load(source, null, null);
    }

    private String decode(byte[] buffer) throws IOException {
        ByteBuffer bb = ByteBuffer.wrap(buffer);
        CharBuffer cb = CharBuffer.allocate(buffer.length * 4);
        for (ThreadLocalCharsetDecoder tl : decoders) {
            boolean success;
            CharsetDecoder decoder = (CharsetDecoder)tl.get();
            boolean bl = success = !decoder.decode(bb, cb, true).isError();
            if (success) {
                decoder.flush(cb);
            }
            decoder.reset();
            if (success) {
                return cb.flip().toString();
            }
            bb.rewind();
            cb.clear();
        }
        return new String(buffer);
    }

    @Override
    public void store(OutputStream out, String msg) throws IOException {
        String[] lines;
        StringWriter sw = new StringWriter();
        super.store(sw, null);
        for (String line : lines = sw.toString().split("\n\r?")) {
            if (line.startsWith("#")) continue;
            out.write(line.getBytes(StandardCharsets.UTF_8));
            out.write("\n".getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public void store(Writer out, String msg) throws IOException {
        String[] lines;
        StringWriter sw = new StringWriter();
        super.store(sw, null);
        for (String line : lines = sw.toString().split("\n\r?")) {
            if (line.startsWith("#")) continue;
            out.write(line);
            out.write("\n");
        }
    }

    public void store(OutputStream out) throws IOException {
        this.store(out, null);
    }

    public UTF8Properties replaceAll(String pattern, String replacement) {
        UTF8Properties result = new UTF8Properties(this.defaults);
        Pattern regex = Pattern.compile(pattern);
        for (Map.Entry<Object, Object> entry : this.entrySet()) {
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            value = regex.matcher(value).replaceAll(replacement);
            result.put(key, value);
        }
        return result;
    }
}

