/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.methods.multipart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.httpclient.methods.multipart.FilePartSource;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FilePart
extends PartBase {
    public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    public static final String DEFAULT_CHARSET = "ISO-8859-1";
    public static final String DEFAULT_TRANSFER_ENCODING = "binary";
    private static final Log LOG = LogFactory.getLog(FilePart.class);
    protected static final String FILE_NAME = "; filename=";
    private static final byte[] FILE_NAME_BYTES = EncodingUtil.getAsciiBytes("; filename=");
    private PartSource source;

    public FilePart(String name, PartSource partSource, String contentType, String charset) {
        super(name, contentType == null ? DEFAULT_CONTENT_TYPE : contentType, charset == null ? DEFAULT_CHARSET : charset, DEFAULT_TRANSFER_ENCODING);
        if (partSource == null) {
            throw new IllegalArgumentException("Source may not be null");
        }
        this.source = partSource;
    }

    public FilePart(String name, PartSource partSource) {
        this(name, partSource, null, null);
    }

    public FilePart(String name, File file) throws FileNotFoundException {
        this(name, new FilePartSource(file), null, null);
    }

    public FilePart(String name, File file, String contentType, String charset) throws FileNotFoundException {
        this(name, new FilePartSource(file), contentType, charset);
    }

    public FilePart(String name, String fileName, File file) throws FileNotFoundException {
        this(name, new FilePartSource(fileName, file), null, null);
    }

    public FilePart(String name, String fileName, File file, String contentType, String charset) throws FileNotFoundException {
        this(name, new FilePartSource(fileName, file), contentType, charset);
    }

    @Override
    protected void sendDispositionHeader(OutputStream out) throws IOException {
        LOG.trace((Object)"enter sendDispositionHeader(OutputStream out)");
        super.sendDispositionHeader(out);
        String filename = this.source.getFileName();
        if (filename != null) {
            out.write(FILE_NAME_BYTES);
            out.write(QUOTE_BYTES);
            out.write(EncodingUtil.getAsciiBytes(filename));
            out.write(QUOTE_BYTES);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void sendData(OutputStream out) throws IOException {
        LOG.trace((Object)"enter sendData(OutputStream out)");
        if (this.lengthOfData() == 0L) {
            LOG.debug((Object)"No data to send.");
            return;
        }
        byte[] tmp = new byte[4096];
        InputStream instream = this.source.createInputStream();
        try {
            int len;
            while ((len = instream.read(tmp)) >= 0) {
                out.write(tmp, 0, len);
            }
        }
        finally {
            instream.close();
        }
    }

    protected PartSource getSource() {
        LOG.trace((Object)"enter getSource()");
        return this.source;
    }

    @Override
    protected long lengthOfData() throws IOException {
        LOG.trace((Object)"enter lengthOfData()");
        return this.source.getLength();
    }
}

