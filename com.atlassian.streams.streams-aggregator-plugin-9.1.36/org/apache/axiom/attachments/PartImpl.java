/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.james.mime4j.MimeException
 *  org.apache.james.mime4j.stream.EntityState
 *  org.apache.james.mime4j.stream.MimeTokenStream
 */
package org.apache.axiom.attachments;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import org.apache.axiom.attachments.MIMEMessage;
import org.apache.axiom.attachments.Part;
import org.apache.axiom.attachments.PartContent;
import org.apache.axiom.attachments.PartContentFactory;
import org.apache.axiom.attachments.PartDataHandler;
import org.apache.axiom.attachments.ReadOnceInputStreamWrapper;
import org.apache.axiom.mime.Header;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.util.DetachableInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.stream.EntityState;
import org.apache.james.mime4j.stream.MimeTokenStream;

final class PartImpl
implements Part {
    private static final int STATE_UNREAD = 0;
    private static final int STATE_BUFFERED = 1;
    private static final int STATE_STREAMING = 2;
    private static final int STATE_DISCARDED = 3;
    private static final Log log = LogFactory.getLog(PartImpl.class);
    private final MIMEMessage message;
    private final boolean isRootPart;
    private List headers;
    private int state = 0;
    private MimeTokenStream parser;
    private PartContent content;
    private final DataHandler dataHandler;
    private DetachableInputStream detachableInputStream;

    PartImpl(MIMEMessage message, boolean isRootPart, List headers, MimeTokenStream parser) {
        this.message = message;
        this.isRootPart = isRootPart;
        this.headers = headers;
        this.parser = parser;
        this.dataHandler = new PartDataHandler(this);
    }

    public String getHeader(String name) {
        String value = null;
        int l = this.headers.size();
        for (int i = 0; i < l; ++i) {
            Header header = (Header)this.headers.get(i);
            if (!header.getName().equalsIgnoreCase(name)) continue;
            value = header.getValue();
            break;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("getHeader name=(" + name + ") value=(" + value + ")"));
        }
        return value;
    }

    public String getContentID() {
        return this.getHeader("content-id");
    }

    public String getContentType() {
        return this.getHeader("content-type");
    }

    String getDataSourceContentType() {
        String ct = this.getContentType();
        return ct == null ? "application/octet-stream" : ct;
    }

    public DataHandler getDataHandler() {
        return this.dataHandler;
    }

    public long getSize() {
        return this.getContent().getSize();
    }

    private PartContent getContent() {
        switch (this.state) {
            case 0: {
                this.fetch();
            }
            case 1: {
                return this.content;
            }
        }
        throw new IllegalStateException("The content of the MIME part has already been consumed");
    }

    private static void checkParserState(EntityState state, EntityState expected) throws IllegalStateException {
        if (expected != state) {
            throw new IllegalStateException("Internal error: expected parser to be in state " + expected + ", but got " + state);
        }
    }

    void fetch() {
        switch (this.state) {
            case 0: {
                PartImpl.checkParserState(this.parser.getState(), EntityState.T_BODY);
                this.content = PartContentFactory.createPartContent(this.message.getLifecycleManager(), this.parser.getDecodedInputStream(), this.isRootPart, this.message.getThreshold(), this.message.getAttachmentRepoDir(), this.message.getContentLengthIfKnown());
                this.moveToNextPart();
                this.state = 1;
                break;
            }
            case 2: {
                try {
                    this.detachableInputStream.detach();
                }
                catch (IOException ex) {
                    throw new OMException(ex);
                }
                this.detachableInputStream = null;
                this.moveToNextPart();
                this.state = 3;
            }
        }
    }

    private void moveToNextPart() {
        try {
            PartImpl.checkParserState(this.parser.next(), EntityState.T_END_BODYPART);
            EntityState state = this.parser.next();
            if (state == EntityState.T_EPILOGUE) {
                while (this.parser.next() != EntityState.T_END_MULTIPART) {
                }
            } else if (state != EntityState.T_START_BODYPART && state != EntityState.T_END_MULTIPART) {
                throw new IllegalStateException("Internal error: unexpected parser state " + state);
            }
        }
        catch (IOException ex) {
            throw new OMException(ex);
        }
        catch (MimeException ex) {
            throw new OMException(ex);
        }
        this.parser = null;
    }

    InputStream getInputStream(boolean preserve) throws IOException {
        if (!preserve && this.state == 0) {
            PartImpl.checkParserState(this.parser.getState(), EntityState.T_BODY);
            this.state = 2;
            this.detachableInputStream = new DetachableInputStream(this.parser.getDecodedInputStream());
            return this.detachableInputStream;
        }
        PartContent content = this.getContent();
        InputStream stream = content.getInputStream();
        if (!preserve) {
            stream = new ReadOnceInputStreamWrapper(this, stream);
        }
        return stream;
    }

    DataSource getDataSource() {
        return this.getContent().getDataSource(this.getDataSourceContentType());
    }

    void writeTo(OutputStream out) throws IOException {
        this.getContent().writeTo(out);
    }

    void releaseContent() throws IOException {
        switch (this.state) {
            case 0: {
                try {
                    EntityState state;
                    while ((state = this.parser.next()) != EntityState.T_START_BODYPART && state != EntityState.T_END_MULTIPART) {
                    }
                }
                catch (MimeException ex) {
                    throw new OMException(ex);
                }
                this.state = 3;
                break;
            }
            case 1: {
                this.content.destroy();
            }
        }
    }
}

