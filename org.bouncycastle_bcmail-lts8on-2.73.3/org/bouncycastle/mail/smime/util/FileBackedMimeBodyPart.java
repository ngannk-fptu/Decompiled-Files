/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.MessagingException
 *  javax.mail.internet.InternetHeaders
 *  javax.mail.internet.MimeBodyPart
 */
package org.bouncycastle.mail.smime.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.mail.smime.util.SharedFileInputStream;

public class FileBackedMimeBodyPart
extends MimeBodyPart {
    private static final int BUF_SIZE = 32760;
    private final File _file;

    public FileBackedMimeBodyPart(File file) throws MessagingException, IOException {
        super((InputStream)new SharedFileInputStream(file));
        this._file = file;
    }

    public FileBackedMimeBodyPart(InputStream content, File file) throws MessagingException, IOException {
        this(FileBackedMimeBodyPart.saveStreamToFile(content, file));
    }

    public FileBackedMimeBodyPart(InternetHeaders headers, InputStream body, File file) throws MessagingException, IOException {
        this(FileBackedMimeBodyPart.saveStreamToFile(headers, body, file));
    }

    public void writeTo(OutputStream out) throws IOException, MessagingException {
        if (!this._file.exists()) {
            throw new IOException("file " + this._file.getCanonicalPath() + " no longer exists.");
        }
        super.writeTo(out);
    }

    public void dispose() throws IOException {
        ((SharedFileInputStream)this.contentStream).getRoot().dispose();
        if (this._file.exists() && !this._file.delete()) {
            throw new IOException("deletion of underlying file <" + this._file.getCanonicalPath() + "> failed.");
        }
    }

    private static File saveStreamToFile(InputStream content, File tempFile) throws IOException {
        FileBackedMimeBodyPart.saveContentToStream(new FileOutputStream(tempFile), content);
        return tempFile;
    }

    private static File saveStreamToFile(InternetHeaders headers, InputStream content, File tempFile) throws IOException {
        FileOutputStream out = new FileOutputStream(tempFile);
        Enumeration en = headers.getAllHeaderLines();
        while (en.hasMoreElements()) {
            FileBackedMimeBodyPart.writeHeader(out, (String)en.nextElement());
        }
        FileBackedMimeBodyPart.writeSeperator(out);
        FileBackedMimeBodyPart.saveContentToStream(out, content);
        return tempFile;
    }

    private static void writeHeader(OutputStream out, String header) throws IOException {
        for (int i = 0; i != header.length(); ++i) {
            out.write(header.charAt(i));
        }
        FileBackedMimeBodyPart.writeSeperator(out);
    }

    private static void writeSeperator(OutputStream out) throws IOException {
        out.write(13);
        out.write(10);
    }

    private static void saveContentToStream(OutputStream out, InputStream content) throws IOException {
        int len;
        byte[] buf = new byte[32760];
        while ((len = content.read(buf, 0, buf.length)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        content.close();
    }
}

