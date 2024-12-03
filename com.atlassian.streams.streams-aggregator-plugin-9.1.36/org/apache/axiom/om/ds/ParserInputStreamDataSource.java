/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.ds;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.attachments.impl.BufferUtils;
import org.apache.axiom.attachments.utils.BAAInputStream;
import org.apache.axiom.attachments.utils.BAAOutputStream;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.ds.OMDataSourceExtBase;
import org.apache.axiom.om.util.CommonUtils;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ParserInputStreamDataSource
extends OMDataSourceExtBase {
    private static final Log log = LogFactory.getLog(ParserInputStreamDataSource.class);
    private Data data = null;
    private static final int defaultBehavior = 1;

    public ParserInputStreamDataSource(InputStream payload, String encoding) {
        this(payload, encoding, 1);
    }

    public ParserInputStreamDataSource(InputStream payload, String encoding, int behavior) {
        this.data = new Data(payload, encoding != null ? encoding : "UTF-8", behavior);
    }

    public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Entry ParserInputStreamDataSource.serialize(OutputStream, OMOutputFormat");
        }
        String encoding = format != null ? format.getCharSetEncoding() : null;
        try {
            if (!this.data.encoding.equalsIgnoreCase(encoding)) {
                byte[] bytes = this.getXMLBytes(encoding);
                output.write(bytes);
            } else {
                InputStream is = this.data.readParserInputStream();
                if (is != null) {
                    BufferUtils.inputStream2OutputStream(is, output);
                }
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)"Exit ParserInputStreamDataSource.serialize(OutputStream, OMOutputFormat");
            }
        }
        catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Entry ParserInputStreamDataSource.serialize(XMLStreamWriter)");
        }
        super.serialize(xmlWriter);
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit ParserInputStreamDataSource.serialize(XMLStreamWriter)");
        }
    }

    public XMLStreamReader getReader() throws XMLStreamException {
        InputStream is;
        if (log.isDebugEnabled()) {
            log.debug((Object)"Entry ParserInputStreamDataSource.getReader()");
        }
        if ((is = this.data.readParserInputStream()) == null && log.isDebugEnabled()) {
            log.warn((Object)"Parser content has already been read");
        }
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(is, this.data.encoding);
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit ParserInputStreamDataSource.getReader()");
        }
        return reader;
    }

    public InputStream getXMLInputStream(String encoding) throws UnsupportedEncodingException {
        try {
            return this.data.readParserInputStream();
        }
        catch (XMLStreamException e) {
            throw new OMException(e);
        }
    }

    public int numReads() {
        return this.data.numReads;
    }

    public Object getObject() {
        return this.data;
    }

    public boolean isDestructiveRead() {
        return this.data.behavior == 0;
    }

    public boolean isDestructiveWrite() {
        return this.data.behavior == 0;
    }

    public byte[] getXMLBytes(String encoding) {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Entry ParserInputStreamDataSource.getXMLBytes(encoding)");
        }
        try {
            InputStream is = this.data.readParserInputStream();
            if (is != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                OMOutputFormat format = new OMOutputFormat();
                format.setCharSetEncoding(encoding);
                try {
                    BufferUtils.inputStream2OutputStream(is, baos);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)"Exit ParserInputStreamDataSource.getXMLBytes(encoding)");
                    }
                    return baos.toByteArray();
                }
                catch (IOException e) {
                    throw new OMException(e);
                }
            }
            if (log.isDebugEnabled()) {
                log.warn((Object)"Parser was already read, recovering by just returning new byte[0]");
                log.debug((Object)"Exit ParserInputStreamDataSource.getXMLBytes(encoding)");
            }
            return new byte[0];
        }
        catch (XMLStreamException e) {
            throw new OMException(e);
        }
    }

    public void close() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Entry ParserInputStreamDataSource.close()");
        }
        if (this.data.payload != null) {
            try {
                this.data.payload.close();
            }
            catch (IOException e) {
                throw new OMException(e);
            }
            this.data.payload = null;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit ParserInputStreamDataSource.close()");
        }
    }

    public OMDataSourceExt copy() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter ParserInputStreamDataSource.copy()");
        }
        try {
            BAAOutputStream baaos = new BAAOutputStream();
            BufferUtils.inputStream2OutputStream(this.data.readParserInputStream(), baaos);
            BAAInputStream baais = new BAAInputStream(baaos.buffers(), baaos.length());
            if (log.isDebugEnabled()) {
                log.debug((Object)"Exit ParserInputStreamDataSource.copy()");
            }
            return new ParserInputStreamDataSource(baais, this.data.encoding, this.data.behavior);
        }
        catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Error ParserInputStreamDataSource.copy(): ", t);
            }
            throw new OMException(t);
        }
    }

    public class Data {
        private InputStream payload = null;
        private String encoding = null;
        private int behavior;
        private int numReads = 0;
        private String firstUseStack = null;

        private Data(InputStream payload, String encoding, int behavior) {
            this.payload = payload;
            this.encoding = encoding;
            this.behavior = behavior;
            this.setInputStream(payload);
        }

        public InputStream readParserInputStream() throws XMLStreamException {
            ++this.numReads;
            if (log.isDebugEnabled()) {
                log.debug((Object)"Entry readParserInputStream()");
                log.debug((Object)("Data Encoding = " + this.encoding));
                log.debug((Object)("numReads = " + this.numReads));
                log.debug((Object)("behavior = " + this.behavior));
                String stack = CommonUtils.stackToString(new OMException());
                log.debug((Object)("call stack:" + stack));
            }
            if (this.payload == null) {
                throw new OMException("ParserInputStreamDataSource's InputStream is null.");
            }
            if (this.behavior == 1) {
                if (this.numReads > 1) {
                    try {
                        if (log.isDebugEnabled()) {
                            log.debug((Object)"reset InputStream for reuse");
                        }
                        this.payload.reset();
                    }
                    catch (Throwable t) {
                        throw new OMException(t);
                    }
                }
            } else if (this.behavior == 2) {
                if (this.numReads == 1) {
                    this.firstUseStack = CommonUtils.stackToString(new OMException());
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("ONE_USE_UNSAFE mode stack:" + this.firstUseStack));
                    }
                } else {
                    OMException ome = new OMException("A second read of ParserInputStreamDataSource is not allowed.The first read was done here: " + this.firstUseStack);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("ONE_USE_UNSAFE second use exception:" + ome));
                    }
                    throw ome;
                }
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)"Exit readParserInputStream()");
            }
            return this.payload;
        }

        public void setInputStream(InputStream inputStream) {
            if (log.isDebugEnabled()) {
                String clsName = inputStream == null ? null : inputStream.getClass().getName();
                log.debug((Object)("Enter setInputStream: The kind of InputStream is:" + clsName));
            }
            this.numReads = 0;
            this.firstUseStack = null;
            if (inputStream == null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)"The inputStream is null");
                }
                this.payload = null;
            } else if (this.behavior == 1) {
                if (inputStream.markSupported()) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)"The inputStream supports mark().  Setting mark()");
                    }
                    this.payload = inputStream;
                    this.payload.mark(Integer.MAX_VALUE);
                } else {
                    try {
                        if (log.isDebugEnabled()) {
                            log.debug((Object)"The inputStream does not supports mark().  Copying Stream");
                        }
                        BAAOutputStream baaos = new BAAOutputStream();
                        BufferUtils.inputStream2OutputStream(inputStream, baaos);
                        BAAInputStream baais = new BAAInputStream(baaos.buffers(), baaos.length());
                        this.payload = baais;
                        this.payload.mark(Integer.MAX_VALUE);
                    }
                    catch (Throwable t) {
                        if (log.isDebugEnabled()) {
                            log.debug((Object)"Error:", t);
                        }
                        throw new OMException(t);
                    }
                }
            } else {
                this.payload = inputStream;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)"Exit setInputStream");
            }
        }
    }
}

