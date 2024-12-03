/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.util;

import com.sun.xml.messaging.saaj.util.ByteInputStream;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.messaging.saaj.util.SAAJUtil;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.transform.stream.StreamSource;

public class JAXMStreamSource
extends StreamSource {
    private static final Integer soapBodyPartSizeLimit;
    InputStream in;
    Reader reader;
    private static final boolean lazyContentLength;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JAXMStreamSource(InputStream is) throws IOException {
        if (lazyContentLength) {
            this.in = is;
        } else if (is instanceof ByteInputStream) {
            this.in = (ByteInputStream)is;
        } else {
            try (ByteOutputStream bout = null;){
                bout = new ByteOutputStream();
                bout.write(is);
                ByteInputStream byteInputStream = bout.newInputStream();
                if (soapBodyPartSizeLimit != null && byteInputStream.getCount() > soapBodyPartSizeLimit) {
                    throw new IOException("SOAP body part of size " + byteInputStream.getCount() + " exceeded size limitation: " + soapBodyPartSizeLimit);
                }
                this.in = byteInputStream;
            }
        }
    }

    public JAXMStreamSource(Reader rdr) throws IOException {
        int len;
        if (lazyContentLength) {
            this.reader = rdr;
            return;
        }
        CharArrayWriter cout = new CharArrayWriter();
        char[] temp = new char[1024];
        while (-1 != (len = rdr.read(temp))) {
            cout.write(temp, 0, len);
        }
        this.reader = new CharArrayReader(cout.toCharArray(), 0, cout.size());
    }

    @Override
    public InputStream getInputStream() {
        return this.in;
    }

    @Override
    public Reader getReader() {
        return this.reader;
    }

    public void reset() throws IOException {
        if (this.in != null) {
            this.in.reset();
        }
        if (this.reader != null) {
            this.reader.reset();
        }
    }

    static {
        lazyContentLength = SAAJUtil.getSystemBoolean("saaj.lazy.contentlength");
        soapBodyPartSizeLimit = SAAJUtil.getSystemInteger("saaj.mime.soapBodyPartSizeLimit");
    }
}

