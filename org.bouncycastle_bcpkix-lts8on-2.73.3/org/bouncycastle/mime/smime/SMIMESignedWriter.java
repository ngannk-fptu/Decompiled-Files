/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Store
 *  org.bouncycastle.util.Strings
 */
package org.bouncycastle.mime.smime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedDataStreamGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeWriter;
import org.bouncycastle.mime.encoding.Base64OutputStream;
import org.bouncycastle.mime.smime.SMimeUtils;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.Strings;

public class SMIMESignedWriter
extends MimeWriter {
    public static final Map RFC3851_MICALGS;
    public static final Map RFC5751_MICALGS;
    public static final Map STANDARD_MICALGS;
    private final CMSSignedDataStreamGenerator sigGen;
    private final String boundary;
    private final OutputStream mimeOut;
    private final String contentTransferEncoding;

    private SMIMESignedWriter(Builder builder, Map<String, String> headers, String boundary, OutputStream mimeOut) {
        super(new Headers(SMIMESignedWriter.mapToLines(headers), builder.contentTransferEncoding));
        this.sigGen = builder.sigGen;
        this.contentTransferEncoding = builder.contentTransferEncoding;
        this.boundary = boundary;
        this.mimeOut = mimeOut;
    }

    @Override
    public OutputStream getContentStream() throws IOException {
        this.headers.dumpHeaders(this.mimeOut);
        this.mimeOut.write(Strings.toByteArray((String)"\r\n"));
        if (this.boundary == null) {
            return null;
        }
        this.mimeOut.write(Strings.toByteArray((String)"This is an S/MIME signed message\r\n"));
        this.mimeOut.write(Strings.toByteArray((String)"\r\n--"));
        this.mimeOut.write(Strings.toByteArray((String)this.boundary));
        this.mimeOut.write(Strings.toByteArray((String)"\r\n"));
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        Base64OutputStream stream = new Base64OutputStream(bOut);
        return new ContentOutputStream(this.sigGen.open(stream, false, SMimeUtils.createUnclosable(this.mimeOut)), this.mimeOut, bOut, stream);
    }

    static {
        HashMap<ASN1ObjectIdentifier, String> stdMicAlgs = new HashMap<ASN1ObjectIdentifier, String>();
        stdMicAlgs.put(CMSAlgorithm.MD5, "md5");
        stdMicAlgs.put(CMSAlgorithm.SHA1, "sha-1");
        stdMicAlgs.put(CMSAlgorithm.SHA224, "sha-224");
        stdMicAlgs.put(CMSAlgorithm.SHA256, "sha-256");
        stdMicAlgs.put(CMSAlgorithm.SHA384, "sha-384");
        stdMicAlgs.put(CMSAlgorithm.SHA512, "sha-512");
        stdMicAlgs.put(CMSAlgorithm.GOST3411, "gostr3411-94");
        stdMicAlgs.put(CMSAlgorithm.GOST3411_2012_256, "gostr3411-2012-256");
        stdMicAlgs.put(CMSAlgorithm.GOST3411_2012_512, "gostr3411-2012-512");
        RFC5751_MICALGS = Collections.unmodifiableMap(stdMicAlgs);
        HashMap<ASN1ObjectIdentifier, String> oldMicAlgs = new HashMap<ASN1ObjectIdentifier, String>();
        oldMicAlgs.put(CMSAlgorithm.MD5, "md5");
        oldMicAlgs.put(CMSAlgorithm.SHA1, "sha1");
        oldMicAlgs.put(CMSAlgorithm.SHA224, "sha224");
        oldMicAlgs.put(CMSAlgorithm.SHA256, "sha256");
        oldMicAlgs.put(CMSAlgorithm.SHA384, "sha384");
        oldMicAlgs.put(CMSAlgorithm.SHA512, "sha512");
        oldMicAlgs.put(CMSAlgorithm.GOST3411, "gostr3411-94");
        oldMicAlgs.put(CMSAlgorithm.GOST3411_2012_256, "gostr3411-2012-256");
        oldMicAlgs.put(CMSAlgorithm.GOST3411_2012_512, "gostr3411-2012-512");
        RFC3851_MICALGS = Collections.unmodifiableMap(oldMicAlgs);
        STANDARD_MICALGS = RFC5751_MICALGS;
    }

    public static class Builder {
        private static final String[] detHeaders = new String[]{"Content-Type"};
        private static final String[] detValues = new String[]{"multipart/signed; protocol=\"application/pkcs7-signature\""};
        private static final String[] encHeaders = new String[]{"Content-Type", "Content-Disposition", "Content-Transfer-Encoding", "Content-Description"};
        private static final String[] encValues = new String[]{"application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data", "attachment; filename=\"smime.p7m\"", "base64", "S/MIME Signed Message"};
        private final CMSSignedDataStreamGenerator sigGen = new CMSSignedDataStreamGenerator();
        private final Map<String, String> extraHeaders = new LinkedHashMap<String, String>();
        private final boolean encapsulated;
        private final Map micAlgs = STANDARD_MICALGS;
        String contentTransferEncoding = "base64";

        public Builder() {
            this(false);
        }

        public Builder(boolean encapsulated) {
            this.encapsulated = encapsulated;
        }

        public Builder withHeader(String headerName, String headerValue) {
            this.extraHeaders.put(headerName, headerValue);
            return this;
        }

        public Builder addCertificate(X509CertificateHolder certificate) throws CMSException {
            this.sigGen.addCertificate(certificate);
            return this;
        }

        public Builder addCertificates(Store certificates) throws CMSException {
            this.sigGen.addCertificates(certificates);
            return this;
        }

        public Builder addSignerInfoGenerator(SignerInfoGenerator signerGenerator) {
            this.sigGen.addSignerInfoGenerator(signerGenerator);
            return this;
        }

        public SMIMESignedWriter build(OutputStream mimeOut) {
            String boundary;
            LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
            if (this.encapsulated) {
                boundary = null;
                for (int i = 0; i != encHeaders.length; ++i) {
                    headers.put(encHeaders[i], encValues[i]);
                }
            } else {
                boundary = this.generateBoundary();
                StringBuffer contValue = new StringBuffer(detValues[0]);
                this.addHashHeader(contValue, this.sigGen.getDigestAlgorithms());
                this.addBoundary(contValue, boundary);
                headers.put(detHeaders[0], contValue.toString());
                for (int i = 1; i < detHeaders.length; ++i) {
                    headers.put(detHeaders[i], detValues[i]);
                }
            }
            for (Map.Entry<String, String> ent : this.extraHeaders.entrySet()) {
                headers.put(ent.getKey(), ent.getValue());
            }
            return new SMIMESignedWriter(this, headers, boundary, SMimeUtils.autoBuffer(mimeOut));
        }

        private void addHashHeader(StringBuffer header, List signers) {
            int count = 0;
            Iterator it = signers.iterator();
            TreeSet<String> micAlgSet = new TreeSet<String>();
            while (it.hasNext()) {
                AlgorithmIdentifier digest = (AlgorithmIdentifier)it.next();
                String micAlg = (String)this.micAlgs.get(digest.getAlgorithm());
                if (micAlg == null) {
                    micAlgSet.add("unknown");
                    continue;
                }
                micAlgSet.add(micAlg);
            }
            for (String alg : micAlgSet) {
                if (count == 0) {
                    if (micAlgSet.size() != 1) {
                        header.append("; micalg=\"");
                    } else {
                        header.append("; micalg=");
                    }
                } else {
                    header.append(',');
                }
                header.append(alg);
                ++count;
            }
            if (count != 0 && micAlgSet.size() != 1) {
                header.append('\"');
            }
        }

        private void addBoundary(StringBuffer header, String boundary) {
            header.append(";\r\n\tboundary=\"");
            header.append(boundary);
            header.append("\"");
        }

        private String generateBoundary() {
            SecureRandom random = new SecureRandom();
            return "==" + new BigInteger(180, random).setBit(179).toString(16) + "=";
        }
    }

    private class ContentOutputStream
    extends OutputStream {
        private final OutputStream main;
        private final OutputStream backing;
        private final ByteArrayOutputStream sigStream;
        private final OutputStream sigBase;

        ContentOutputStream(OutputStream main, OutputStream backing, ByteArrayOutputStream sigStream, OutputStream sigBase) {
            this.main = main;
            this.backing = backing;
            this.sigStream = sigStream;
            this.sigBase = sigBase;
        }

        @Override
        public void write(byte[] buf) throws IOException {
            this.main.write(buf);
        }

        @Override
        public void write(byte[] buf, int off, int len) throws IOException {
            this.main.write(buf, off, len);
        }

        @Override
        public void write(int i) throws IOException {
            this.main.write(i);
        }

        @Override
        public void close() throws IOException {
            if (SMIMESignedWriter.this.boundary != null) {
                this.main.close();
                this.backing.write(Strings.toByteArray((String)"\r\n--"));
                this.backing.write(Strings.toByteArray((String)SMIMESignedWriter.this.boundary));
                this.backing.write(Strings.toByteArray((String)"\r\n"));
                this.backing.write(Strings.toByteArray((String)"Content-Type: application/pkcs7-signature; name=\"smime.p7s\"\r\n"));
                this.backing.write(Strings.toByteArray((String)"Content-Transfer-Encoding: base64\r\n"));
                this.backing.write(Strings.toByteArray((String)"Content-Disposition: attachment; filename=\"smime.p7s\"\r\n"));
                this.backing.write(Strings.toByteArray((String)"\r\n"));
                if (this.sigBase != null) {
                    this.sigBase.close();
                }
                this.backing.write(this.sigStream.toByteArray());
                this.backing.write(Strings.toByteArray((String)"\r\n--"));
                this.backing.write(Strings.toByteArray((String)SMIMESignedWriter.this.boundary));
                this.backing.write(Strings.toByteArray((String)"--\r\n"));
            }
            if (this.backing != null) {
                this.backing.close();
            }
        }
    }
}

