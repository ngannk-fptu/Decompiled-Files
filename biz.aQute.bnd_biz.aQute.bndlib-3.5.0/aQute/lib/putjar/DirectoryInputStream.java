/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.putjar;

import aQute.lib.io.IO;
import aQute.libg.fileiterator.FileIterator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class DirectoryInputStream
extends InputStream {
    static final int BUFFER_SIZE = 65536;
    final File root;
    final FileIterator fi;
    File element;
    int entries = 0;
    int state = 0;
    long where = 0L;
    static final int START = 0;
    static final int HEADER = 1;
    static final int DATA = 2;
    static final int DIRECTORY = 4;
    static final int EOF = 5;
    static final InputStream eof = new ByteArrayInputStream(new byte[0]);
    ByteArrayOutputStream directory = new ByteArrayOutputStream();
    InputStream current = eof;

    public DirectoryInputStream(File dir) {
        this.root = dir;
        this.fi = new FileIterator(dir);
    }

    @Override
    public int read() throws IOException {
        if (this.fi == null) {
            return -1;
        }
        int c = this.current.read();
        if (c < 0) {
            this.next();
            c = this.current.read();
        }
        if (c >= 0) {
            ++this.where;
        }
        return c;
    }

    void next() throws IOException {
        switch (this.state) {
            case 0: 
            case 2: {
                this.nextHeader();
                break;
            }
            case 1: {
                if (this.element.isFile() && this.element.length() > 0L) {
                    this.current = IO.stream(this.element);
                    this.state = 2;
                    break;
                }
                this.nextHeader();
                break;
            }
            case 4: {
                this.state = 5;
                this.current = eof;
                break;
            }
        }
    }

    private void nextHeader() throws IOException {
        if (this.fi.hasNext()) {
            this.element = this.fi.next();
            this.state = 1;
            this.current = this.getHeader(this.root, this.element);
            ++this.entries;
        } else {
            this.current = this.getDirectory();
            this.state = 4;
        }
    }

    InputStream getDirectory() throws IOException {
        long where = this.where;
        int sizeDirectory = this.directory.size();
        this.writeInt(this.directory, 1347093766);
        this.writeShort(this.directory, 0);
        this.writeShort(this.directory, 0);
        this.writeShort(this.directory, this.entries);
        this.writeInt(this.directory, sizeDirectory);
        this.writeInt(this.directory, (int)where);
        this.writeShort(this.directory, 0);
        this.directory.close();
        byte[] data = this.directory.toByteArray();
        return new ByteArrayInputStream(data);
    }

    private void writeShort(OutputStream out, int v) throws IOException {
        for (int i = 0; i < 2; ++i) {
            out.write((byte)(v & 0xFF));
            v >>= 8;
        }
    }

    private void writeInt(OutputStream out, int v) throws IOException {
        for (int i = 0; i < 4; ++i) {
            out.write((byte)(v & 0xFF));
            v >>= 8;
        }
    }

    private InputStream getHeader(File root, File file) throws IOException {
        long where = this.where;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        this.writeInt(bout, 67324752);
        this.writeInt(this.directory, 1347092738);
        this.writeShort(this.directory, 0);
        this.writeShort(bout, 10);
        this.writeShort(this.directory, 10);
        this.writeShort(bout, 0);
        this.writeShort(this.directory, 0);
        this.writeShort(bout, 0);
        this.writeShort(this.directory, 0);
        this.writeInt(bout, 0);
        this.writeInt(this.directory, 0);
        if (file.isDirectory()) {
            this.writeInt(bout, 0);
            this.writeInt(bout, 0);
            this.writeInt(bout, 0);
            this.writeInt(this.directory, 0);
            this.writeInt(this.directory, 0);
            this.writeInt(this.directory, 0);
        } else {
            CRC32 crc = this.getCRC(file);
            this.writeInt(bout, (int)crc.getValue());
            this.writeInt(bout, (int)file.length());
            this.writeInt(bout, (int)file.length());
            this.writeInt(this.directory, (int)crc.getValue());
            this.writeInt(this.directory, (int)file.length());
            this.writeInt(this.directory, (int)file.length());
        }
        String p = this.getPath(root, file);
        if (file.isDirectory()) {
            p = p + "/";
        }
        byte[] path = p.getBytes(StandardCharsets.UTF_8);
        this.writeShort(bout, path.length);
        this.writeShort(this.directory, path.length);
        this.writeShort(bout, 0);
        this.writeShort(this.directory, 0);
        bout.write(path);
        this.writeShort(this.directory, 0);
        this.writeShort(this.directory, 0);
        this.writeShort(this.directory, 0);
        this.writeInt(this.directory, 0);
        this.writeInt(this.directory, (int)where);
        this.directory.write(path);
        byte[] bytes = bout.toByteArray();
        return new ByteArrayInputStream(bytes);
    }

    private String getPath(File root, File file) {
        if (file.equals(root)) {
            return "";
        }
        String p = this.getPath(root, file.getParentFile());
        p = p.length() == 0 ? file.getName() : p + "/" + file.getName();
        return p;
    }

    private CRC32 getCRC(File file) throws IOException {
        CRC32 crc = new CRC32();
        try (InputStream in = IO.stream(file);){
            byte[] data = new byte[65536];
            int size = in.read(data);
            while (size > 0) {
                crc.update(data, 0, size);
                size = in.read(data);
            }
        }
        return crc;
    }
}

