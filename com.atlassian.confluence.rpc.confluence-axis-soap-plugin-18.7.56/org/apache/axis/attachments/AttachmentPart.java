/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.attachments;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.transform.stream.StreamSource;
import org.apache.axis.Part;
import org.apache.axis.attachments.ManagedMemoryDataSource;
import org.apache.axis.components.image.ImageIOFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.IOUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.SessionUtils;
import org.apache.commons.logging.Log;

public class AttachmentPart
extends javax.xml.soap.AttachmentPart
implements Part {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$attachments$AttachmentPart == null ? (class$org$apache$axis$attachments$AttachmentPart = AttachmentPart.class$("org.apache.axis.attachments.AttachmentPart")) : class$org$apache$axis$attachments$AttachmentPart).getName());
    DataHandler datahandler = null;
    private MimeHeaders mimeHeaders = new MimeHeaders();
    private Object contentObject;
    private String attachmentFile;
    static /* synthetic */ Class class$org$apache$axis$attachments$AttachmentPart;

    public AttachmentPart() {
        this.setMimeHeader("Content-Id", SessionUtils.generateSessionId());
    }

    public AttachmentPart(DataHandler dh) {
        this.setMimeHeader("Content-Id", SessionUtils.generateSessionId());
        this.datahandler = dh;
        if (dh != null) {
            this.setMimeHeader("Content-Type", dh.getContentType());
            DataSource ds = dh.getDataSource();
            if (ds instanceof ManagedMemoryDataSource) {
                this.extractFilename((ManagedMemoryDataSource)ds);
            }
        }
    }

    protected void finalize() throws Throwable {
        this.dispose();
    }

    public DataHandler getActivationDataHandler() {
        return this.datahandler;
    }

    public String getContentType() {
        return this.getFirstMimeHeader("Content-Type");
    }

    public void addMimeHeader(String header, String value) {
        this.mimeHeaders.addHeader(header, value);
    }

    public String getFirstMimeHeader(String header) {
        String[] values = this.mimeHeaders.getHeader(header.toLowerCase());
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }

    public boolean matches(MimeHeaders headers) {
        Iterator i = headers.getAllHeaders();
        while (i.hasNext()) {
            MimeHeader hdr = (MimeHeader)i.next();
            String[] values = this.mimeHeaders.getHeader(hdr.getName());
            boolean found = false;
            if (values != null) {
                for (int j = 0; j < values.length; ++j) {
                    if (!hdr.getValue().equalsIgnoreCase(values[j])) continue;
                    found = true;
                    break;
                }
            }
            if (found) continue;
            return false;
        }
        return true;
    }

    public String getContentLocation() {
        return this.getFirstMimeHeader("Content-Location");
    }

    public void setContentLocation(String loc) {
        this.setMimeHeader("Content-Location", loc);
    }

    public void setContentId(String newCid) {
        this.setMimeHeader("Content-Id", newCid);
    }

    public String getContentId() {
        return this.getFirstMimeHeader("Content-Id");
    }

    public Iterator getMatchingMimeHeaders(String[] match) {
        return this.mimeHeaders.getMatchingHeaders(match);
    }

    public Iterator getNonMatchingMimeHeaders(String[] match) {
        return this.mimeHeaders.getNonMatchingHeaders(match);
    }

    public Iterator getAllMimeHeaders() {
        return this.mimeHeaders.getAllHeaders();
    }

    public void setMimeHeader(String name, String value) {
        this.mimeHeaders.setHeader(name, value);
    }

    public void removeAllMimeHeaders() {
        this.mimeHeaders.removeAllHeaders();
    }

    public void removeMimeHeader(String header) {
        this.mimeHeaders.removeHeader(header);
    }

    public DataHandler getDataHandler() throws SOAPException {
        if (this.datahandler == null) {
            throw new SOAPException(Messages.getMessage("noContent"));
        }
        return this.datahandler;
    }

    public void setDataHandler(DataHandler datahandler) {
        if (datahandler == null) {
            throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));
        }
        this.datahandler = datahandler;
        this.setMimeHeader("Content-Type", datahandler.getContentType());
        DataSource ds = datahandler.getDataSource();
        if (ds instanceof ManagedMemoryDataSource) {
            this.extractFilename((ManagedMemoryDataSource)ds);
        }
    }

    public Object getContent() throws SOAPException {
        if (this.contentObject != null) {
            return this.contentObject;
        }
        if (this.datahandler == null) {
            throw new SOAPException(Messages.getMessage("noContent"));
        }
        DataSource ds = this.datahandler.getDataSource();
        InputStream is = null;
        try {
            is = ds.getInputStream();
        }
        catch (IOException io) {
            log.error((Object)Messages.getMessage("javaIOException00"), (Throwable)io);
            throw new SOAPException(io);
        }
        if (ds.getContentType().equals("text/plain")) {
            try {
                byte[] bytes = new byte[is.available()];
                IOUtils.readFully(is, bytes);
                return new String(bytes);
            }
            catch (IOException io) {
                log.error((Object)Messages.getMessage("javaIOException00"), (Throwable)io);
                throw new SOAPException(io);
            }
        }
        if (ds.getContentType().equals("text/xml")) {
            return new StreamSource(is);
        }
        if (ds.getContentType().equals("image/gif") || ds.getContentType().equals("image/jpeg")) {
            try {
                return ImageIOFactory.getImageIO().loadImage(is);
            }
            catch (Exception ex) {
                log.error((Object)Messages.getMessage("javaIOException00"), (Throwable)ex);
                throw new SOAPException(ex);
            }
        }
        return is;
    }

    public void setContent(Object object, String contentType) {
        ManagedMemoryDataSource source = null;
        this.setMimeHeader("Content-Type", contentType);
        if (object instanceof String) {
            try {
                String s = (String)object;
                ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
                source = new ManagedMemoryDataSource(bais, 16384, contentType, true);
                this.extractFilename(source);
                this.datahandler = new DataHandler((DataSource)source);
                this.contentObject = object;
                return;
            }
            catch (IOException io) {
                log.error((Object)Messages.getMessage("javaIOException00"), (Throwable)io);
                throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));
            }
        }
        if (object instanceof InputStream) {
            try {
                source = new ManagedMemoryDataSource((InputStream)object, 16384, contentType, true);
                this.extractFilename(source);
                this.datahandler = new DataHandler((DataSource)source);
                this.contentObject = null;
                return;
            }
            catch (IOException io) {
                log.error((Object)Messages.getMessage("javaIOException00"), (Throwable)io);
                throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));
            }
        }
        if (object instanceof StreamSource) {
            try {
                source = new ManagedMemoryDataSource(((StreamSource)object).getInputStream(), 16384, contentType, true);
                this.extractFilename(source);
                this.datahandler = new DataHandler((DataSource)source);
                this.contentObject = null;
                return;
            }
            catch (IOException io) {
                log.error((Object)Messages.getMessage("javaIOException00"), (Throwable)io);
                throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));
            }
        }
        throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));
    }

    public void clearContent() {
        this.datahandler = null;
        this.contentObject = null;
    }

    public int getSize() throws SOAPException {
        if (this.datahandler == null) {
            return 0;
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            this.datahandler.writeTo((OutputStream)bout);
        }
        catch (IOException ex) {
            log.error((Object)Messages.getMessage("javaIOException00"), (Throwable)ex);
            throw new SOAPException(Messages.getMessage("javaIOException01", ex.getMessage()), ex);
        }
        return bout.size();
    }

    public String[] getMimeHeader(String name) {
        return this.mimeHeaders.getHeader(name);
    }

    public String getContentIdRef() {
        return "cid:" + this.getContentId();
    }

    private void extractFilename(ManagedMemoryDataSource source) {
        if (source.getDiskCacheFile() != null) {
            String path = source.getDiskCacheFile().getAbsolutePath();
            this.setAttachmentFile(path);
        }
    }

    protected void setAttachmentFile(String path) {
        this.attachmentFile = path;
    }

    public void detachAttachmentFile() {
        this.attachmentFile = null;
    }

    public String getAttachmentFile() {
        return this.attachmentFile;
    }

    public synchronized void dispose() {
        if (this.attachmentFile != null) {
            DataSource ds = this.datahandler.getDataSource();
            if (ds instanceof ManagedMemoryDataSource) {
                ((ManagedMemoryDataSource)ds).delete();
            } else {
                File f = new File(this.attachmentFile);
                f.delete();
            }
            this.setAttachmentFile(null);
        }
        this.datahandler = null;
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

