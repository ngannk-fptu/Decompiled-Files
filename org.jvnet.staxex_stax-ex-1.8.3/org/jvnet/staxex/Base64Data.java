/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 */
package org.jvnet.staxex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.jvnet.staxex.Base64Encoder;
import org.jvnet.staxex.Base64EncoderStream;
import org.jvnet.staxex.ByteArrayOutputStreamEx;
import org.jvnet.staxex.StreamingDataHandler;

public class Base64Data
implements CharSequence,
Cloneable {
    private DataHandler dataHandler;
    private byte[] data;
    private String hrefCid;
    private int dataLen;
    private boolean dataCloneByRef;
    private String mimeType;
    private static final Logger logger = Logger.getLogger(Base64Data.class.getName());
    private static final int CHUNK_SIZE;

    public Base64Data() {
    }

    public Base64Data(Base64Data that) {
        that.get();
        if (that.dataCloneByRef) {
            this.data = that.data;
        } else {
            this.data = new byte[that.dataLen];
            System.arraycopy(that.data, 0, this.data, 0, that.dataLen);
        }
        this.dataCloneByRef = true;
        this.dataLen = that.dataLen;
        this.dataHandler = null;
        this.mimeType = that.mimeType;
    }

    public void set(byte[] data, int len, String mimeType, boolean cloneByRef) {
        this.data = data;
        this.dataLen = len;
        this.dataCloneByRef = cloneByRef;
        this.dataHandler = null;
        this.mimeType = mimeType;
    }

    public void set(byte[] data, int len, String mimeType) {
        this.set(data, len, mimeType, false);
    }

    public void set(byte[] data, String mimeType) {
        this.set(data, data.length, mimeType, false);
    }

    public void set(DataHandler data) {
        assert (data != null);
        this.dataHandler = data;
        this.data = null;
    }

    public DataHandler getDataHandler() {
        if (this.dataHandler == null) {
            this.dataHandler = new Base64StreamingDataHandler(new Base64DataSource());
        } else if (!(this.dataHandler instanceof StreamingDataHandler)) {
            this.dataHandler = new FilterDataHandler(this.dataHandler);
        }
        return this.dataHandler;
    }

    public byte[] getExact() {
        this.get();
        if (this.dataLen != this.data.length) {
            byte[] buf = new byte[this.dataLen];
            System.arraycopy(this.data, 0, buf, 0, this.dataLen);
            this.data = buf;
        }
        return this.data;
    }

    public InputStream getInputStream() throws IOException {
        if (this.dataHandler != null) {
            return this.dataHandler.getInputStream();
        }
        return new ByteArrayInputStream(this.data, 0, this.dataLen);
    }

    public boolean hasData() {
        return this.data != null;
    }

    public byte[] get() {
        if (this.data == null) {
            try {
                ByteArrayOutputStreamEx baos = new ByteArrayOutputStreamEx(1024);
                InputStream is = this.dataHandler.getDataSource().getInputStream();
                baos.readFrom(is);
                is.close();
                this.data = baos.getBuffer();
                this.dataLen = baos.size();
                this.dataCloneByRef = true;
            }
            catch (IOException e) {
                this.dataLen = 0;
            }
        }
        return this.data;
    }

    public int getDataLen() {
        this.get();
        return this.dataLen;
    }

    public String getMimeType() {
        if (this.mimeType == null) {
            return "application/octet-stream";
        }
        return this.mimeType;
    }

    @Override
    public int length() {
        this.get();
        return (this.dataLen + 2) / 3 * 4;
    }

    @Override
    public char charAt(int index) {
        int offset = index % 4;
        int base = index / 4 * 3;
        switch (offset) {
            case 0: {
                return Base64Encoder.encode(this.data[base] >> 2);
            }
            case 1: {
                byte b1 = base + 1 < this.dataLen ? this.data[base + 1] : (byte)0;
                return Base64Encoder.encode((this.data[base] & 3) << 4 | b1 >> 4 & 0xF);
            }
            case 2: {
                if (base + 1 < this.dataLen) {
                    byte b1 = this.data[base + 1];
                    byte b2 = base + 2 < this.dataLen ? this.data[base + 2] : (byte)0;
                    return Base64Encoder.encode((b1 & 0xF) << 2 | b2 >> 6 & 3);
                }
                return '=';
            }
            case 3: {
                if (base + 2 < this.dataLen) {
                    return Base64Encoder.encode(this.data[base + 2] & 0x3F);
                }
                return '=';
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        StringBuilder buf = new StringBuilder();
        this.get();
        for (int i = start; i < end; ++i) {
            buf.append(this.charAt(i));
        }
        return buf;
    }

    @Override
    public String toString() {
        this.get();
        return Base64Encoder.print(this.data, 0, this.dataLen);
    }

    public void writeTo(char[] buf, int start) {
        this.get();
        Base64Encoder.print(this.data, 0, this.dataLen, buf, start);
    }

    public void writeTo(XMLStreamWriter output) throws IOException, XMLStreamException {
        if (this.data == null) {
            try {
                int b;
                InputStream is = this.dataHandler.getDataSource().getInputStream();
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                Base64EncoderStream encWriter = new Base64EncoderStream(output, outStream);
                byte[] buffer = new byte[CHUNK_SIZE];
                while ((b = is.read(buffer)) != -1) {
                    encWriter.write(buffer, 0, b);
                }
                outStream.close();
                encWriter.close();
            }
            catch (IOException e) {
                this.dataLen = 0;
                throw e;
            }
        } else {
            String s = Base64Encoder.print(this.data, 0, this.dataLen);
            output.writeCharacters(s);
        }
    }

    public Base64Data clone() {
        try {
            Base64Data clone = (Base64Data)super.clone();
            clone.get();
            if (clone.dataCloneByRef) {
                this.data = clone.data;
            } else {
                this.data = new byte[clone.dataLen];
                System.arraycopy(clone.data, 0, this.data, 0, clone.dataLen);
            }
            this.dataCloneByRef = true;
            this.dataLen = clone.dataLen;
            this.dataHandler = null;
            this.mimeType = clone.mimeType;
            return clone;
        }
        catch (CloneNotSupportedException ex) {
            Logger.getLogger(Base64Data.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    static String getProperty(final String propName) {
        if (System.getSecurityManager() == null) {
            return System.getProperty(propName);
        }
        return (String)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return System.getProperty(propName);
            }
        });
    }

    public String getHrefCid() {
        if (this.hrefCid == null && this.dataHandler != null && this.dataHandler instanceof StreamingDataHandler) {
            this.hrefCid = ((StreamingDataHandler)this.dataHandler).getHrefCid();
        }
        return this.hrefCid;
    }

    public void setHrefCid(String cid) {
        this.hrefCid = cid;
        if (this.dataHandler != null && this.dataHandler instanceof StreamingDataHandler) {
            ((StreamingDataHandler)this.dataHandler).setHrefCid(cid);
        }
    }

    static {
        int bufSize = 1024;
        try {
            String bufSizeStr = Base64Data.getProperty("org.jvnet.staxex.Base64DataStreamWriteBufferSize");
            if (bufSizeStr != null) {
                bufSize = Integer.parseInt(bufSizeStr);
            }
        }
        catch (Exception e) {
            logger.log(Level.INFO, "Error reading org.jvnet.staxex.Base64DataStreamWriteBufferSize property", e);
        }
        CHUNK_SIZE = bufSize;
    }

    private static final class FilterDataHandler
    extends StreamingDataHandler {
        FilterDataHandler(DataHandler dh) {
            super(dh.getDataSource());
        }

        @Override
        public InputStream readOnce() throws IOException {
            return this.getDataSource().getInputStream();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void moveTo(File dst) throws IOException {
            byte[] buf = new byte[8192];
            InputStream in = null;
            FileOutputStream out = null;
            try {
                int amountRead;
                in = this.getDataSource().getInputStream();
                out = new FileOutputStream(dst);
                while ((amountRead = in.read(buf)) != -1) {
                    ((OutputStream)out).write(buf, 0, amountRead);
                }
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException iOException) {}
                }
                if (out != null) {
                    try {
                        ((OutputStream)out).close();
                    }
                    catch (IOException iOException) {}
                }
            }
        }

        @Override
        public void close() throws IOException {
        }
    }

    private final class Base64StreamingDataHandler
    extends StreamingDataHandler {
        Base64StreamingDataHandler(DataSource source) {
            super(source);
        }

        @Override
        public InputStream readOnce() throws IOException {
            return this.getDataSource().getInputStream();
        }

        @Override
        public void moveTo(File dst) throws IOException {
            try (FileOutputStream fout = new FileOutputStream(dst);){
                fout.write(Base64Data.this.data, 0, Base64Data.this.dataLen);
            }
        }

        @Override
        public void close() throws IOException {
        }
    }

    private final class Base64DataSource
    implements DataSource {
        private Base64DataSource() {
        }

        public String getContentType() {
            return Base64Data.this.getMimeType();
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(Base64Data.this.data, 0, Base64Data.this.dataLen);
        }

        public String getName() {
            return null;
        }

        public OutputStream getOutputStream() {
            throw new UnsupportedOperationException();
        }
    }
}

