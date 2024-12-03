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
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import org.apache.axis.AxisFault;
import org.apache.axis.Part;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.attachments.DimeDelimitedInputStream;
import org.apache.axis.attachments.DimeTypeNameFormat;
import org.apache.axis.attachments.ManagedMemoryDataSource;
import org.apache.axis.attachments.MultiPartInputStream;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class MultiPartDimeInputStream
extends MultiPartInputStream {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$attachments$MultiPartDimeInputStream == null ? (class$org$apache$axis$attachments$MultiPartDimeInputStream = MultiPartDimeInputStream.class$("org.apache.axis.attachments.MultiPartDimeInputStream")) : class$org$apache$axis$attachments$MultiPartDimeInputStream).getName());
    protected HashMap parts = new HashMap();
    protected LinkedList orderedParts = new LinkedList();
    protected int rootPartLength = 0;
    protected boolean closed = false;
    protected boolean eos = false;
    protected DimeDelimitedInputStream dimeDelimitedStream = null;
    protected InputStream soapStream = null;
    protected byte[] boundary = null;
    protected ByteArrayInputStream cachedSOAPEnvelope = null;
    protected String contentId = null;
    protected static final String[] READ_ALL = new String[]{" * \u0000 ".intern()};
    static /* synthetic */ Class class$org$apache$axis$attachments$MultiPartDimeInputStream;

    public MultiPartDimeInputStream(InputStream is) throws IOException {
        super(null);
        this.dimeDelimitedStream = new DimeDelimitedInputStream(is);
        this.soapStream = this.dimeDelimitedStream;
        this.contentId = this.dimeDelimitedStream.getContentId();
    }

    public Part getAttachmentByReference(String[] id) throws AxisFault {
        Part ret = null;
        try {
            for (int i = id.length - 1; ret == null && i > -1; --i) {
                ret = (AttachmentPart)this.parts.get(id[i]);
            }
            if (null == ret) {
                ret = this.readTillFound(id);
            }
            log.debug((Object)Messages.getMessage("return02", "getAttachmentByReference(\"" + id + "\"", ret == null ? "null" : ret.toString()));
        }
        catch (IOException e) {
            throw new AxisFault(e.getClass().getName() + e.getMessage());
        }
        return ret;
    }

    protected void addPart(String contentId, String locationId, AttachmentPart ap) {
        if (contentId != null && contentId.trim().length() != 0) {
            this.parts.put(contentId, ap);
        }
        this.orderedParts.add(ap);
    }

    protected void readAll() throws AxisFault {
        try {
            this.readTillFound(READ_ALL);
        }
        catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    public Collection getAttachments() throws AxisFault {
        this.readAll();
        return new LinkedList(this.orderedParts);
    }

    protected Part readTillFound(String[] id) throws IOException {
        if (this.dimeDelimitedStream == null) {
            return null;
        }
        AttachmentPart ret = null;
        try {
            if (this.soapStream != null) {
                if (!this.eos) {
                    ByteArrayOutputStream soapdata = new ByteArrayOutputStream(8192);
                    byte[] buf = new byte[16384];
                    int byteread = 0;
                    do {
                        if ((byteread = this.soapStream.read(buf)) <= 0) continue;
                        soapdata.write(buf, 0, byteread);
                    } while (byteread > -1);
                    soapdata.close();
                    this.soapStream.close();
                    this.soapStream = new ByteArrayInputStream(soapdata.toByteArray());
                }
                this.dimeDelimitedStream = this.dimeDelimitedStream.getNextStream();
            }
            if (null != this.dimeDelimitedStream) {
                do {
                    String contentId = this.dimeDelimitedStream.getContentId();
                    String type = this.dimeDelimitedStream.getType();
                    if (type != null && !this.dimeDelimitedStream.getDimeTypeNameFormat().equals(DimeTypeNameFormat.MIME)) {
                        type = "application/uri; uri=\"" + type + "\"";
                    }
                    ManagedMemoryDataSource source = new ManagedMemoryDataSource(this.dimeDelimitedStream, 16384, type, true);
                    DataHandler dh = new DataHandler((DataSource)source);
                    AttachmentPart ap = new AttachmentPart(dh);
                    if (contentId != null) {
                        ap.setMimeHeader("Content-Id", contentId);
                    }
                    this.addPart(contentId, "", ap);
                    for (int i = id.length - 1; ret == null && i > -1; --i) {
                        if (contentId == null || !id[i].equals(contentId)) continue;
                        ret = ap;
                    }
                    this.dimeDelimitedStream = this.dimeDelimitedStream.getNextStream();
                } while (null == ret && null != this.dimeDelimitedStream);
            }
        }
        catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
        return ret;
    }

    public String getContentLocation() {
        return null;
    }

    public String getContentId() {
        return this.contentId;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.closed) {
            throw new IOException(Messages.getMessage("streamClosed"));
        }
        if (this.eos) {
            return -1;
        }
        int read = this.soapStream.read(b, off, len);
        if (read < 0) {
            this.eos = true;
        }
        return read;
    }

    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    public int read() throws IOException {
        if (this.closed) {
            throw new IOException(Messages.getMessage("streamClosed"));
        }
        if (this.eos) {
            return -1;
        }
        int ret = this.soapStream.read();
        if (ret < 0) {
            this.eos = true;
        }
        return ret;
    }

    public void close() throws IOException {
        this.closed = true;
        this.soapStream.close();
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

