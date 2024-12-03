/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 *  javax.mail.MessagingException
 */
package org.apache.axiom.attachments;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import org.apache.axiom.attachments.CachedFileDataSource;
import org.apache.axiom.attachments.PartContent;
import org.apache.axiom.attachments.impl.BufferUtils;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.attachments.lifecycle.impl.FileAccessor;

class PartContentOnFile
extends PartContent {
    private final FileAccessor fileAccessor;
    private final LifecycleManager manager;

    PartContentOnFile(LifecycleManager manager, InputStream is1, InputStream is2, String attachmentDir) throws IOException {
        this.manager = manager;
        this.fileAccessor = manager.create(attachmentDir);
        OutputStream fos = this.fileAccessor.getOutputStream();
        BufferUtils.inputStream2OutputStream(is1, fos);
        BufferUtils.inputStream2OutputStream(is2, fos);
        fos.flush();
        fos.close();
    }

    InputStream getInputStream() throws IOException {
        try {
            return this.fileAccessor.getInputStream();
        }
        catch (MessagingException ex) {
            IOException ex2 = new IOException(ex.getMessage());
            ex2.setStackTrace(ex.getStackTrace());
            throw ex2;
        }
    }

    DataSource getDataSource(String contentType) {
        CachedFileDataSource ds = new CachedFileDataSource(this.fileAccessor.getFile());
        ds.setContentType(contentType);
        return ds;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void writeTo(OutputStream out) throws IOException {
        InputStream in = this.getInputStream();
        try {
            BufferUtils.inputStream2OutputStream(in, out);
        }
        finally {
            in.close();
        }
    }

    long getSize() {
        return this.fileAccessor.getSize();
    }

    void destroy() throws IOException {
        this.manager.delete(this.fileAccessor.getFile());
    }
}

