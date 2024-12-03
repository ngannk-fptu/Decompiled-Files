/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.util.io.Streams
 */
package org.bouncycastle.cms;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.util.io.Streams;

public class CMSTypedStream {
    private static final int BUF_SIZ = 32768;
    private final ASN1ObjectIdentifier _oid;
    protected InputStream _in;

    public CMSTypedStream(InputStream in) {
        this(PKCSObjectIdentifiers.data.getId(), in, 32768);
    }

    public CMSTypedStream(String oid, InputStream in) {
        this(new ASN1ObjectIdentifier(oid), in, 32768);
    }

    public CMSTypedStream(String oid, InputStream in, int bufSize) {
        this(new ASN1ObjectIdentifier(oid), in, bufSize);
    }

    public CMSTypedStream(ASN1ObjectIdentifier oid, InputStream in) {
        this(oid, in, 32768);
    }

    public CMSTypedStream(ASN1ObjectIdentifier oid, InputStream in, int bufSize) {
        this._oid = oid;
        this._in = new FullReaderStream(new BufferedInputStream(in, bufSize));
    }

    protected CMSTypedStream(ASN1ObjectIdentifier oid) {
        this._oid = oid;
    }

    public ASN1ObjectIdentifier getContentType() {
        return this._oid;
    }

    public InputStream getContentStream() {
        return this._in;
    }

    public void drain() throws IOException {
        Streams.drain((InputStream)this._in);
        this._in.close();
    }

    private static class FullReaderStream
    extends FilterInputStream {
        FullReaderStream(InputStream in) {
            super(in);
        }

        @Override
        public int read(byte[] buf, int off, int len) throws IOException {
            if (len == 0) {
                return 0;
            }
            int totalRead = Streams.readFully((InputStream)this.in, (byte[])buf, (int)off, (int)len);
            return totalRead > 0 ? totalRead : -1;
        }
    }
}

