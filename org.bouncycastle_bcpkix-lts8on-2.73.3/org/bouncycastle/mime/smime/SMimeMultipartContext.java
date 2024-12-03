/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.io.TeeInputStream
 *  org.bouncycastle.util.io.TeeOutputStream
 */
package org.bouncycastle.mime.smime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.mime.CanonicalOutputStream;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeContext;
import org.bouncycastle.mime.MimeMultipartContext;
import org.bouncycastle.mime.MimeParserContext;
import org.bouncycastle.mime.smime.SMimeParserContext;
import org.bouncycastle.mime.smime.SMimeUtils;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.io.TeeInputStream;
import org.bouncycastle.util.io.TeeOutputStream;

public class SMimeMultipartContext
implements MimeMultipartContext {
    private final SMimeParserContext parserContext;
    private DigestCalculator[] calculators;

    public SMimeMultipartContext(MimeParserContext parserContext, Headers headers) {
        this.parserContext = (SMimeParserContext)parserContext;
        this.calculators = this.createDigestCalculators(headers);
    }

    DigestCalculator[] getDigestCalculators() {
        return this.calculators;
    }

    OutputStream getDigestOutputStream() {
        if (this.calculators.length == 1) {
            return this.calculators[0].getOutputStream();
        }
        OutputStream compoundStream = this.calculators[0].getOutputStream();
        for (int i = 1; i < this.calculators.length; ++i) {
            compoundStream = new TeeOutputStream(this.calculators[i].getOutputStream(), compoundStream);
        }
        return compoundStream;
    }

    private DigestCalculator[] createDigestCalculators(Headers headers) {
        try {
            Map<String, String> contentTypeFields = headers.getContentTypeAttributes();
            String micalgs = contentTypeFields.get("micalg");
            if (micalgs == null) {
                throw new IllegalStateException("No micalg field on content-type header");
            }
            String[] algs = micalgs.substring(micalgs.indexOf(61) + 1).split(",");
            DigestCalculator[] dcOut = new DigestCalculator[algs.length];
            for (int t = 0; t < algs.length; ++t) {
                String alg = SMimeUtils.lessQuotes(algs[t]).trim();
                dcOut[t] = this.parserContext.getDigestCalculatorProvider().get(new AlgorithmIdentifier(SMimeUtils.getDigestOID(alg)));
            }
            return dcOut;
        }
        catch (OperatorCreationException e) {
            return null;
        }
    }

    @Override
    public MimeContext createContext(final int partNo) throws IOException {
        return new MimeContext(){

            @Override
            public InputStream applyContext(Headers headers, InputStream contentStream) throws IOException {
                if (partNo == 0) {
                    OutputStream digestOut = SMimeMultipartContext.this.getDigestOutputStream();
                    headers.dumpHeaders(digestOut);
                    digestOut.write(13);
                    digestOut.write(10);
                    return new TeeInputStream(contentStream, (OutputStream)new CanonicalOutputStream(SMimeMultipartContext.this.parserContext, headers, digestOut));
                }
                return contentStream;
            }
        };
    }

    @Override
    public InputStream applyContext(Headers headers, InputStream contentStream) throws IOException {
        return contentStream;
    }
}

