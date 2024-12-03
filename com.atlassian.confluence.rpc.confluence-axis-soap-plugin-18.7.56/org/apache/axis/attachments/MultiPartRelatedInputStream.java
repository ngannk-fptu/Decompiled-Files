/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.mail.Header
 *  javax.mail.MessagingException
 *  javax.mail.internet.ContentType
 *  javax.mail.internet.InternetHeaders
 *  javax.mail.internet.MimeUtility
 *  javax.mail.internet.ParseException
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.attachments;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParseException;
import org.apache.axis.AxisFault;
import org.apache.axis.Part;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.attachments.BoundaryDelimitedStream;
import org.apache.axis.attachments.ManagedMemoryDataSource;
import org.apache.axis.attachments.MultiPartInputStream;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.IOUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class MultiPartRelatedInputStream
extends MultiPartInputStream {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$attachments$MultiPartRelatedInputStream == null ? (class$org$apache$axis$attachments$MultiPartRelatedInputStream = MultiPartRelatedInputStream.class$("org.apache.axis.attachments.MultiPartRelatedInputStream")) : class$org$apache$axis$attachments$MultiPartRelatedInputStream).getName());
    public static final String MIME_MULTIPART_RELATED = "multipart/related";
    protected HashMap parts = new HashMap();
    protected LinkedList orderedParts = new LinkedList();
    protected int rootPartLength = 0;
    protected boolean closed = false;
    protected boolean eos = false;
    protected BoundaryDelimitedStream boundaryDelimitedStream = null;
    protected InputStream soapStream = null;
    protected InputStream soapStreamBDS = null;
    protected byte[] boundary = null;
    protected ByteArrayInputStream cachedSOAPEnvelope = null;
    protected String contentLocation = null;
    protected String contentId = null;
    private static final int MAX_CACHED = 16384;
    protected static final String[] READ_ALL = new String[]{" * \u0000 ".intern()};
    static /* synthetic */ Class class$org$apache$axis$attachments$MultiPartRelatedInputStream;

    public MultiPartRelatedInputStream(String contentType, InputStream stream) throws AxisFault {
        super(null);
        if (!(stream instanceof BufferedInputStream)) {
            stream = new BufferedInputStream(stream);
        }
        try {
            ContentType ct = new ContentType(contentType);
            String rootPartContentId = ct.getParameter("start");
            if (rootPartContentId != null) {
                if ((rootPartContentId = rootPartContentId.trim()).startsWith("<")) {
                    rootPartContentId = rootPartContentId.substring(1);
                }
                if (rootPartContentId.endsWith(">")) {
                    rootPartContentId = rootPartContentId.substring(0, rootPartContentId.length() - 1);
                }
            }
            if (ct.getParameter("boundary") != null) {
                String boundaryStr = "--" + ct.getParameter("boundary");
                byte[][] boundaryMarker = new byte[2][boundaryStr.length() + 2];
                IOUtils.readFully(stream, boundaryMarker[0]);
                this.boundary = (boundaryStr + "\r\n").getBytes("US-ASCII");
                int current = 0;
                boolean found = false;
                while (!found) {
                    found = Arrays.equals(boundaryMarker[current & 1], this.boundary);
                    if (!found) {
                        System.arraycopy(boundaryMarker[current & 1], 1, boundaryMarker[current + 1 & 1], 0, boundaryMarker[0].length - 1);
                        if (stream.read(boundaryMarker[current + 1 & 1], boundaryMarker[0].length - 1, 1) < 1) {
                            throw new AxisFault(Messages.getMessage("mimeErrorNoBoundary", new String(this.boundary)));
                        }
                    }
                    ++current;
                }
                boundaryStr = "\r\n" + boundaryStr;
                this.boundary = boundaryStr.getBytes("US-ASCII");
            } else {
                boolean found = false;
                while (!found) {
                    this.boundary = this.readLine(stream);
                    if (this.boundary == null) {
                        throw new AxisFault(Messages.getMessage("mimeErrorNoBoundary", "--"));
                    }
                    found = this.boundary.length > 4 && this.boundary[2] == 45 && this.boundary[3] == 45;
                }
            }
            this.boundaryDelimitedStream = new BoundaryDelimitedStream(stream, this.boundary, 1024);
            String contentTransferEncoding = null;
            do {
                this.contentId = null;
                this.contentLocation = null;
                contentTransferEncoding = null;
                InternetHeaders headers = new InternetHeaders((InputStream)this.boundaryDelimitedStream);
                this.contentId = headers.getHeader("Content-Id", null);
                if (this.contentId != null) {
                    this.contentId = this.contentId.trim();
                    if (this.contentId.startsWith("<")) {
                        this.contentId = this.contentId.substring(1);
                    }
                    if (this.contentId.endsWith(">")) {
                        this.contentId = this.contentId.substring(0, this.contentId.length() - 1);
                    }
                    this.contentId = this.contentId.trim();
                }
                this.contentLocation = headers.getHeader("Content-Location", null);
                if (this.contentLocation != null) {
                    this.contentLocation = this.contentLocation.trim();
                    if (this.contentLocation.startsWith("<")) {
                        this.contentLocation = this.contentLocation.substring(1);
                    }
                    if (this.contentLocation.endsWith(">")) {
                        this.contentLocation = this.contentLocation.substring(0, this.contentLocation.length() - 1);
                    }
                    this.contentLocation = this.contentLocation.trim();
                }
                if ((contentType = headers.getHeader("Content-Type", null)) != null) {
                    contentType = contentType.trim();
                }
                if ((contentTransferEncoding = headers.getHeader("Content-Transfer-Encoding", null)) != null) {
                    contentTransferEncoding = contentTransferEncoding.trim();
                }
                InputStream decodedStream = this.boundaryDelimitedStream;
                if (contentTransferEncoding != null && 0 != contentTransferEncoding.length()) {
                    decodedStream = MimeUtility.decode((InputStream)decodedStream, (String)contentTransferEncoding);
                }
                if (rootPartContentId == null || rootPartContentId.equals(this.contentId)) continue;
                DataHandler dh = new DataHandler((DataSource)new ManagedMemoryDataSource(decodedStream, 16384, contentType, true));
                AttachmentPart ap = new AttachmentPart(dh);
                if (this.contentId != null) {
                    ap.setMimeHeader("Content-Id", this.contentId);
                }
                if (this.contentLocation != null) {
                    ap.setMimeHeader("Content-Location", this.contentLocation);
                }
                Enumeration en = headers.getNonMatchingHeaders(new String[]{"Content-Id", "Content-Location", "Content-Type"});
                while (en.hasMoreElements()) {
                    Header header = (Header)en.nextElement();
                    String name = header.getName();
                    String value = header.getValue();
                    if (name == null || value == null || (name = name.trim()).length() == 0) continue;
                    ap.addMimeHeader(name, value);
                }
                this.addPart(this.contentId, this.contentLocation, ap);
                this.boundaryDelimitedStream = this.boundaryDelimitedStream.getNextStream();
            } while (null != this.boundaryDelimitedStream && rootPartContentId != null && !rootPartContentId.equals(this.contentId));
            if (this.boundaryDelimitedStream == null) {
                throw new AxisFault(Messages.getMessage("noRoot", rootPartContentId));
            }
            this.soapStreamBDS = this.boundaryDelimitedStream;
            this.soapStream = contentTransferEncoding != null && 0 != contentTransferEncoding.length() ? MimeUtility.decode((InputStream)this.boundaryDelimitedStream, (String)contentTransferEncoding) : this.boundaryDelimitedStream;
        }
        catch (ParseException e) {
            throw new AxisFault(Messages.getMessage("mimeErrorParsing", e.getMessage()));
        }
        catch (IOException e) {
            throw new AxisFault(Messages.getMessage("readError", e.getMessage()));
        }
        catch (MessagingException e) {
            throw new AxisFault(Messages.getMessage("readError", e.getMessage()));
        }
    }

    private final byte[] readLine(InputStream is) throws IOException {
        ByteArrayOutputStream input = new ByteArrayOutputStream(1024);
        int c = 0;
        input.write(13);
        input.write(10);
        int next = -1;
        block4: while (c != -1) {
            c = -1 != next ? next : is.read();
            next = -1;
            switch (c) {
                case -1: {
                    continue block4;
                }
                case 13: {
                    next = is.read();
                    if (next == 10) {
                        return input.toByteArray();
                    }
                    if (next != -1) break;
                    return null;
                }
            }
            input.write((byte)c);
        }
        return null;
    }

    public Part getAttachmentByReference(String[] id) throws AxisFault {
        Part ret = null;
        for (int i = id.length - 1; ret == null && i > -1; --i) {
            ret = (AttachmentPart)this.parts.get(id[i]);
        }
        if (null == ret) {
            ret = this.readTillFound(id);
        }
        log.debug((Object)Messages.getMessage("return02", "getAttachmentByReference(\"" + id + "\"", ret == null ? "null" : ret.toString()));
        return ret;
    }

    protected void addPart(String contentId, String locationId, AttachmentPart ap) {
        if (contentId != null && contentId.trim().length() != 0) {
            this.parts.put(contentId, ap);
        }
        if (locationId != null && locationId.trim().length() != 0) {
            this.parts.put(locationId, ap);
        }
        this.orderedParts.add(ap);
    }

    protected void readAll() throws AxisFault {
        this.readTillFound(READ_ALL);
    }

    public Collection getAttachments() throws AxisFault {
        this.readAll();
        return this.orderedParts;
    }

    protected Part readTillFound(String[] id) throws AxisFault {
        if (this.boundaryDelimitedStream == null) {
            return null;
        }
        AttachmentPart ret = null;
        try {
            if (this.soapStreamBDS == this.boundaryDelimitedStream) {
                if (!this.eos) {
                    ByteArrayOutputStream soapdata = new ByteArrayOutputStream(8192);
                    byte[] buf = new byte[16384];
                    int byteread = 0;
                    do {
                        if ((byteread = this.soapStream.read(buf)) <= 0) continue;
                        soapdata.write(buf, 0, byteread);
                    } while (byteread > -1);
                    soapdata.close();
                    this.soapStream = new ByteArrayInputStream(soapdata.toByteArray());
                }
                this.boundaryDelimitedStream = this.boundaryDelimitedStream.getNextStream();
            }
            if (null != this.boundaryDelimitedStream) {
                do {
                    String contentType = null;
                    String contentId = null;
                    String contentTransferEncoding = null;
                    String contentLocation = null;
                    InternetHeaders headers = new InternetHeaders((InputStream)this.boundaryDelimitedStream);
                    contentId = headers.getHeader("Content-Id", null);
                    if (contentId != null) {
                        if ((contentId = contentId.trim()).startsWith("<")) {
                            contentId = contentId.substring(1);
                        }
                        if (contentId.endsWith(">")) {
                            contentId = contentId.substring(0, contentId.length() - 1);
                        }
                        contentId = contentId.trim();
                    }
                    if ((contentType = headers.getHeader("Content-Type", null)) != null) {
                        contentType = contentType.trim();
                    }
                    if ((contentLocation = headers.getHeader("Content-Location", null)) != null) {
                        contentLocation = contentLocation.trim();
                    }
                    if ((contentTransferEncoding = headers.getHeader("Content-Transfer-Encoding", null)) != null) {
                        contentTransferEncoding = contentTransferEncoding.trim();
                    }
                    InputStream decodedStream = this.boundaryDelimitedStream;
                    if (contentTransferEncoding != null && 0 != contentTransferEncoding.length()) {
                        decodedStream = MimeUtility.decode((InputStream)decodedStream, (String)contentTransferEncoding);
                    }
                    ManagedMemoryDataSource source = new ManagedMemoryDataSource(decodedStream, 16384, contentType, true);
                    DataHandler dh = new DataHandler((DataSource)source);
                    AttachmentPart ap = new AttachmentPart(dh);
                    if (contentId != null) {
                        ap.setMimeHeader("Content-Id", contentId);
                    }
                    if (contentLocation != null) {
                        ap.setMimeHeader("Content-Location", contentLocation);
                    }
                    Enumeration en = headers.getNonMatchingHeaders(new String[]{"Content-Id", "Content-Location", "Content-Type"});
                    while (en.hasMoreElements()) {
                        Header header = (Header)en.nextElement();
                        String name = header.getName();
                        String value = header.getValue();
                        if (name == null || value == null || (name = name.trim()).length() == 0) continue;
                        ap.addMimeHeader(name, value);
                    }
                    this.addPart(contentId, contentLocation, ap);
                    for (int i = id.length - 1; ret == null && i > -1; --i) {
                        if (contentId != null && id[i].equals(contentId)) {
                            ret = ap;
                            continue;
                        }
                        if (contentLocation == null || !id[i].equals(contentLocation)) continue;
                        ret = ap;
                    }
                    this.boundaryDelimitedStream = this.boundaryDelimitedStream.getNextStream();
                } while (null == ret && null != this.boundaryDelimitedStream);
            }
        }
        catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
        return ret;
    }

    public String getContentLocation() {
        return this.contentLocation;
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

    public int available() throws IOException {
        return this.closed || this.eos ? 0 : this.soapStream.available();
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

