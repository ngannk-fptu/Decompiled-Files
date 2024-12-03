/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.BERSequenceGenerator
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.operator.OutputCompressor;

public class CMSCompressedDataStreamGenerator {
    public static final String ZLIB = CMSObjectIdentifiers.zlibCompress.getId();
    private int _bufferSize;

    public void setBufferSize(int bufferSize) {
        this._bufferSize = bufferSize;
    }

    public OutputStream open(OutputStream out, OutputCompressor compressor) throws IOException {
        return this.open(CMSObjectIdentifiers.data, out, compressor);
    }

    public OutputStream open(ASN1ObjectIdentifier contentOID, OutputStream out, OutputCompressor compressor) throws IOException {
        BERSequenceGenerator sGen = new BERSequenceGenerator(out);
        sGen.addObject((ASN1Primitive)CMSObjectIdentifiers.compressedData);
        BERSequenceGenerator cGen = new BERSequenceGenerator(sGen.getRawOutputStream(), 0, true);
        cGen.addObject((ASN1Primitive)new ASN1Integer(0L));
        cGen.addObject((ASN1Encodable)compressor.getAlgorithmIdentifier());
        BERSequenceGenerator eiGen = new BERSequenceGenerator(cGen.getRawOutputStream());
        eiGen.addObject((ASN1Primitive)contentOID);
        OutputStream octetStream = CMSUtils.createBEROctetOutputStream(eiGen.getRawOutputStream(), 0, true, this._bufferSize);
        return new CmsCompressedOutputStream(compressor.getOutputStream(octetStream), sGen, cGen, eiGen);
    }

    private static class CmsCompressedOutputStream
    extends OutputStream {
        private OutputStream _out;
        private BERSequenceGenerator _sGen;
        private BERSequenceGenerator _cGen;
        private BERSequenceGenerator _eiGen;

        CmsCompressedOutputStream(OutputStream out, BERSequenceGenerator sGen, BERSequenceGenerator cGen, BERSequenceGenerator eiGen) {
            this._out = out;
            this._sGen = sGen;
            this._cGen = cGen;
            this._eiGen = eiGen;
        }

        @Override
        public void write(int b) throws IOException {
            this._out.write(b);
        }

        @Override
        public void write(byte[] bytes, int off, int len) throws IOException {
            this._out.write(bytes, off, len);
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            this._out.write(bytes);
        }

        @Override
        public void close() throws IOException {
            this._out.close();
            this._eiGen.close();
            this._cGen.close();
            this._sGen.close();
        }
    }
}

