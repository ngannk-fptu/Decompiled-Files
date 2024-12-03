/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common.bytesource;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.bytesource.ByteSource;

public class ByteSourceFile
extends ByteSource {
    private final File file;

    public ByteSourceFile(File file) {
        super(file.getName());
        this.file = file;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new BufferedInputStream(new FileInputStream(this.file));
    }

    @Override
    public byte[] getBlock(long start, int length) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(this.file, "r");){
            if (start < 0L || length < 0 || start + (long)length < 0L || start + (long)length > raf.length()) {
                throw new IOException("Could not read block (block start: " + start + ", block length: " + length + ", data length: " + raf.length() + ").");
            }
            byte[] byArray = BinaryFunctions.getRAFBytes(raf, start, length, "Could not read value from file");
            return byArray;
        }
    }

    @Override
    public long getLength() {
        return this.file.length();
    }

    @Override
    public byte[] getAll() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream is = this.getInputStream();){
            int read;
            byte[] buffer = new byte[1024];
            while ((read = is.read(buffer)) > 0) {
                baos.write(buffer, 0, read);
            }
            byte[] byArray = baos.toByteArray();
            return byArray;
        }
    }

    @Override
    public String getDescription() {
        return "File: '" + this.file.getAbsolutePath() + "'";
    }
}

