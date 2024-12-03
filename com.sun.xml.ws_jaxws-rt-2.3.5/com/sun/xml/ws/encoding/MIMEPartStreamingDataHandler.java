/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 *  org.jvnet.mimepull.MIMEPart
 */
package com.sun.xml.ws.encoding;

import com.sun.xml.ws.developer.StreamingDataHandler;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import org.jvnet.mimepull.MIMEPart;

public class MIMEPartStreamingDataHandler
extends StreamingDataHandler {
    private final StreamingDataSource ds = (StreamingDataSource)this.getDataSource();

    public MIMEPartStreamingDataHandler(MIMEPart part) {
        super(new StreamingDataSource(part));
    }

    public InputStream readOnce() throws IOException {
        return this.ds.readOnce();
    }

    public void moveTo(File file) throws IOException {
        this.ds.moveTo(file);
    }

    public void close() throws IOException {
        this.ds.close();
    }

    private static final class MyIOException
    extends IOException {
        private final Exception linkedException;

        MyIOException(Exception linkedException) {
            this.linkedException = linkedException;
        }

        @Override
        public Throwable getCause() {
            return this.linkedException;
        }
    }

    private static final class StreamingDataSource
    implements DataSource {
        private final MIMEPart part;

        StreamingDataSource(MIMEPart part) {
            this.part = part;
        }

        public InputStream getInputStream() throws IOException {
            return this.part.read();
        }

        InputStream readOnce() throws IOException {
            try {
                return this.part.readOnce();
            }
            catch (Exception e) {
                throw new MyIOException(e);
            }
        }

        void moveTo(File file) throws IOException {
            this.part.moveTo(file);
        }

        public OutputStream getOutputStream() throws IOException {
            return null;
        }

        public String getContentType() {
            return this.part.getContentType();
        }

        public String getName() {
            return "";
        }

        public void close() throws IOException {
            this.part.close();
        }
    }
}

