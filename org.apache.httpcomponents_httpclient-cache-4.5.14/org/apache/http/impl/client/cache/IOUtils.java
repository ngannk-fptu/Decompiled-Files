/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpEntity
 */
package org.apache.http.impl.client.cache;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import org.apache.http.HttpEntity;

class IOUtils {
    IOUtils() {
    }

    static void consume(HttpEntity entity) throws IOException {
        InputStream inStream;
        if (entity == null) {
            return;
        }
        if (entity.isStreaming() && (inStream = entity.getContent()) != null) {
            inStream.close();
        }
    }

    static void copy(InputStream in, OutputStream out) throws IOException {
        int len;
        byte[] buf = new byte[2048];
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }

    static void closeSilently(Closeable closable) {
        try {
            closable.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    static void copyAndClose(InputStream in, OutputStream out) throws IOException {
        try {
            IOUtils.copy(in, out);
            in.close();
            out.close();
        }
        catch (IOException ex) {
            IOUtils.closeSilently(in);
            IOUtils.closeSilently(out);
            throw ex;
        }
    }

    static void copyFile(File in, File out) throws IOException {
        RandomAccessFile f1 = new RandomAccessFile(in, "r");
        RandomAccessFile f2 = new RandomAccessFile(out, "rw");
        try {
            FileChannel c1 = f1.getChannel();
            FileChannel c2 = f2.getChannel();
            try {
                c1.transferTo(0L, f1.length(), c2);
                c1.close();
                c2.close();
            }
            catch (IOException ex) {
                IOUtils.closeSilently(c1);
                IOUtils.closeSilently(c2);
                throw ex;
            }
            f1.close();
            f2.close();
        }
        catch (IOException ex) {
            IOUtils.closeSilently(f1);
            IOUtils.closeSilently(f2);
            throw ex;
        }
    }
}

