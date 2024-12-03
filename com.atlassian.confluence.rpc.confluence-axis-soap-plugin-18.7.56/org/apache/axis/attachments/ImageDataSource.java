/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.attachments;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import org.apache.axis.components.image.ImageIOFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class ImageDataSource
implements DataSource {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$attachments$ImageDataSource == null ? (class$org$apache$axis$attachments$ImageDataSource = ImageDataSource.class$("org.apache.axis.attachments.ImageDataSource")) : class$org$apache$axis$attachments$ImageDataSource).getName());
    public static final String CONTENT_TYPE = "image/jpeg";
    private final String name;
    private final String contentType;
    private byte[] data;
    private ByteArrayOutputStream os;
    static /* synthetic */ Class class$org$apache$axis$attachments$ImageDataSource;

    public ImageDataSource(String name, Image data) {
        this(name, CONTENT_TYPE, data);
    }

    public ImageDataSource(String name, String contentType, Image data) {
        this.name = name;
        this.contentType = contentType == null ? CONTENT_TYPE : contentType;
        this.os = new ByteArrayOutputStream();
        try {
            if (data != null) {
                ImageIOFactory.getImageIO().saveImage(this.contentType, data, this.os);
            }
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
        }
    }

    public String getName() {
        return this.name;
    }

    public String getContentType() {
        return this.contentType;
    }

    public InputStream getInputStream() throws IOException {
        if (this.os.size() != 0) {
            this.data = this.os.toByteArray();
            this.os.reset();
        }
        return new ByteArrayInputStream(this.data == null ? new byte[]{} : this.data);
    }

    public OutputStream getOutputStream() throws IOException {
        if (this.os.size() != 0) {
            this.data = this.os.toByteArray();
            this.os.reset();
        }
        return this.os;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

