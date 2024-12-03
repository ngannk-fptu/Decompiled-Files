/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.collections;

import aQute.lib.io.IO;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LineCollection
implements Iterator<String>,
Closeable {
    private final BufferedReader reader;
    private String next;

    public LineCollection(InputStream in) throws IOException {
        this(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    public LineCollection(File in) throws IOException {
        this(IO.reader(in, StandardCharsets.UTF_8));
    }

    public LineCollection(Reader reader) throws IOException {
        this(new BufferedReader(reader));
    }

    public LineCollection(BufferedReader reader) throws IOException {
        this.reader = reader;
        this.next = reader.readLine();
    }

    @Override
    public boolean hasNext() {
        return this.next != null;
    }

    @Override
    public String next() {
        if (this.next == null) {
            throw new NoSuchElementException("Iterator has finished");
        }
        try {
            String result = this.next;
            this.next = this.reader.readLine();
            if (this.next == null) {
                this.reader.close();
            }
            return result;
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public void remove() {
        if (this.next == null) {
            throw new UnsupportedOperationException("Cannot remove");
        }
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }
}

