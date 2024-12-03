/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.CommandMap
 *  javax.activation.MailcapCommandMap
 *  javax.mail.BodyPart
 *  javax.mail.MessagingException
 *  javax.mail.Multipart
 *  javax.mail.Part
 *  javax.mail.internet.ContentType
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMessage
 *  javax.mail.internet.MimeMultipart
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers
 *  org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers
 *  org.bouncycastle.asn1.x9.X9ObjectIdentifiers
 *  org.bouncycastle.cms.CMSAlgorithm
 *  org.bouncycastle.cms.CMSException
 *  org.bouncycastle.cms.CMSSignedDataStreamGenerator
 *  org.bouncycastle.cms.SignerInfoGenerator
 *  org.bouncycastle.cms.SignerInformation
 *  org.bouncycastle.cms.SignerInformationStore
 *  org.bouncycastle.util.Store
 */
package org.bouncycastle.mail.smime;

import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedDataStreamGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.mail.smime.MailcapUtil;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMEGenerator;
import org.bouncycastle.mail.smime.SMIMEStreamingProcessor;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.mail.smime.util.CRLFOutputStream;
import org.bouncycastle.util.Store;

public class SMIMESignedGenerator
extends SMIMEGenerator {
    public static final String DIGEST_SHA1 = OIWObjectIdentifiers.idSHA1.getId();
    public static final String DIGEST_MD5 = PKCSObjectIdentifiers.md5.getId();
    public static final String DIGEST_SHA224 = NISTObjectIdentifiers.id_sha224.getId();
    public static final String DIGEST_SHA256 = NISTObjectIdentifiers.id_sha256.getId();
    public static final String DIGEST_SHA384 = NISTObjectIdentifiers.id_sha384.getId();
    public static final String DIGEST_SHA512 = NISTObjectIdentifiers.id_sha512.getId();
    public static final String DIGEST_GOST3411 = CryptoProObjectIdentifiers.gostR3411.getId();
    public static final String DIGEST_RIPEMD128 = TeleTrusTObjectIdentifiers.ripemd128.getId();
    public static final String DIGEST_RIPEMD160 = TeleTrusTObjectIdentifiers.ripemd160.getId();
    public static final String DIGEST_RIPEMD256 = TeleTrusTObjectIdentifiers.ripemd256.getId();
    public static final String ENCRYPTION_RSA = PKCSObjectIdentifiers.rsaEncryption.getId();
    public static final String ENCRYPTION_DSA = X9ObjectIdentifiers.id_dsa_with_sha1.getId();
    public static final String ENCRYPTION_ECDSA = X9ObjectIdentifiers.ecdsa_with_SHA1.getId();
    public static final String ENCRYPTION_RSA_PSS = PKCSObjectIdentifiers.id_RSASSA_PSS.getId();
    public static final String ENCRYPTION_GOST3410 = CryptoProObjectIdentifiers.gostR3410_94.getId();
    public static final String ENCRYPTION_ECGOST3410 = CryptoProObjectIdentifiers.gostR3410_2001.getId();
    public static final String ENCRYPTION_ECGOST3410_2012_256 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256.getId();
    public static final String ENCRYPTION_ECGOST3410_2012_512 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512.getId();
    private static final String CERTIFICATE_MANAGEMENT_CONTENT = "application/pkcs7-mime; name=smime.p7c; smime-type=certs-only";
    private static final String DETACHED_SIGNATURE_TYPE = "application/pkcs7-signature; name=smime.p7s; smime-type=signed-data";
    private static final String ENCAPSULATED_SIGNED_CONTENT_TYPE = "application/pkcs7-mime; name=smime.p7m; smime-type=signed-data";
    public static final Map RFC3851_MICALGS;
    public static final Map RFC5751_MICALGS;
    public static final Map STANDARD_MICALGS;
    private final String defaultContentTransferEncoding;
    private final Map micAlgs;
    private List certStores = new ArrayList();
    private List crlStores = new ArrayList();
    private List attrCertStores = new ArrayList();
    private List signerInfoGens = new ArrayList();
    private List _signers = new ArrayList();
    private List _oldSigners = new ArrayList();
    private Map _digests = new HashMap();

    public SMIMESignedGenerator() {
        this("7bit", STANDARD_MICALGS);
    }

    public SMIMESignedGenerator(String defaultContentTransferEncoding) {
        this(defaultContentTransferEncoding, STANDARD_MICALGS);
    }

    public SMIMESignedGenerator(Map micAlgs) {
        this("7bit", micAlgs);
    }

    public SMIMESignedGenerator(String defaultContentTransferEncoding, Map micAlgs) {
        this.defaultContentTransferEncoding = defaultContentTransferEncoding;
        this.micAlgs = micAlgs;
    }

    public void addSigners(SignerInformationStore signerStore) {
        Iterator it = signerStore.getSigners().iterator();
        while (it.hasNext()) {
            this._oldSigners.add(it.next());
        }
    }

    public void addSignerInfoGenerator(SignerInfoGenerator sigInfoGen) {
        this.signerInfoGens.add(sigInfoGen);
    }

    public void addCertificates(Store certStore) {
        this.certStores.add(certStore);
    }

    public void addCRLs(Store crlStore) {
        this.crlStores.add(crlStore);
    }

    public void addAttributeCertificates(Store certStore) {
        this.attrCertStores.add(certStore);
    }

    private void addHashHeader(StringBuffer header, List signers) {
        int count = 0;
        Iterator it = signers.iterator();
        TreeSet<String> micAlgSet = new TreeSet<String>();
        while (it.hasNext()) {
            Object signer = it.next();
            ASN1ObjectIdentifier digestOID = signer instanceof SignerInformation ? ((SignerInformation)signer).getDigestAlgorithmID().getAlgorithm() : ((SignerInfoGenerator)signer).getDigestAlgorithm().getAlgorithm();
            String micAlg = (String)this.micAlgs.get(digestOID);
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

    private MimeMultipart make(MimeBodyPart content) throws SMIMEException {
        try {
            MimeBodyPart sig = new MimeBodyPart();
            sig.setContent((Object)new ContentSigner(content, false), DETACHED_SIGNATURE_TYPE);
            sig.addHeader("Content-Type", DETACHED_SIGNATURE_TYPE);
            sig.addHeader("Content-Disposition", "attachment; filename=\"smime.p7s\"");
            sig.addHeader("Content-Description", "S/MIME Cryptographic Signature");
            sig.addHeader("Content-Transfer-Encoding", this.encoding);
            StringBuffer header = new StringBuffer("signed; protocol=\"application/pkcs7-signature\"");
            ArrayList allSigners = new ArrayList(this._signers);
            allSigners.addAll(this._oldSigners);
            allSigners.addAll(this.signerInfoGens);
            this.addHashHeader(header, allSigners);
            MimeMultipart mm = new MimeMultipart(header.toString());
            mm.addBodyPart((BodyPart)content);
            mm.addBodyPart((BodyPart)sig);
            return mm;
        }
        catch (MessagingException e) {
            throw new SMIMEException("exception putting multi-part together.", (Exception)((Object)e));
        }
    }

    private MimeBodyPart makeEncapsulated(MimeBodyPart content) throws SMIMEException {
        try {
            MimeBodyPart sig = new MimeBodyPart();
            sig.setContent((Object)new ContentSigner(content, true), ENCAPSULATED_SIGNED_CONTENT_TYPE);
            sig.addHeader("Content-Type", ENCAPSULATED_SIGNED_CONTENT_TYPE);
            sig.addHeader("Content-Disposition", "attachment; filename=\"smime.p7m\"");
            sig.addHeader("Content-Description", "S/MIME Cryptographic Signed Data");
            sig.addHeader("Content-Transfer-Encoding", this.encoding);
            return sig;
        }
        catch (MessagingException e) {
            throw new SMIMEException("exception putting body part together.", (Exception)((Object)e));
        }
    }

    public Map getGeneratedDigests() {
        return new HashMap(this._digests);
    }

    public MimeMultipart generate(MimeBodyPart content) throws SMIMEException {
        return this.make(this.makeContentBodyPart(content));
    }

    public MimeMultipart generate(MimeMessage message) throws SMIMEException {
        try {
            message.saveChanges();
        }
        catch (MessagingException e) {
            throw new SMIMEException("unable to save message", (Exception)((Object)e));
        }
        return this.make(this.makeContentBodyPart(message));
    }

    public MimeBodyPart generateEncapsulated(MimeBodyPart content) throws SMIMEException {
        return this.makeEncapsulated(this.makeContentBodyPart(content));
    }

    public MimeBodyPart generateEncapsulated(MimeMessage message) throws SMIMEException {
        try {
            message.saveChanges();
        }
        catch (MessagingException e) {
            throw new SMIMEException("unable to save message", (Exception)((Object)e));
        }
        return this.makeEncapsulated(this.makeContentBodyPart(message));
    }

    public MimeBodyPart generateCertificateManagement() throws SMIMEException {
        try {
            MimeBodyPart sig = new MimeBodyPart();
            sig.setContent((Object)new ContentSigner(null, true), CERTIFICATE_MANAGEMENT_CONTENT);
            sig.addHeader("Content-Type", CERTIFICATE_MANAGEMENT_CONTENT);
            sig.addHeader("Content-Disposition", "attachment; filename=\"smime.p7c\"");
            sig.addHeader("Content-Description", "S/MIME Certificate Management Message");
            sig.addHeader("Content-Transfer-Encoding", this.encoding);
            return sig;
        }
        catch (MessagingException e) {
            throw new SMIMEException("exception putting body part together.", (Exception)((Object)e));
        }
    }

    static {
        AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                CommandMap commandMap = CommandMap.getDefaultCommandMap();
                if (commandMap instanceof MailcapCommandMap) {
                    CommandMap.setDefaultCommandMap((CommandMap)MailcapUtil.addCommands((MailcapCommandMap)commandMap));
                }
                return null;
            }
        });
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

    private class ContentSigner
    implements SMIMEStreamingProcessor {
        private final MimeBodyPart content;
        private final boolean encapsulate;
        private final boolean noProvider;

        ContentSigner(MimeBodyPart content, boolean encapsulate) {
            this.content = content;
            this.encapsulate = encapsulate;
            this.noProvider = true;
        }

        protected CMSSignedDataStreamGenerator getGenerator() throws CMSException {
            CMSSignedDataStreamGenerator gen = new CMSSignedDataStreamGenerator();
            Iterator it = SMIMESignedGenerator.this.certStores.iterator();
            while (it.hasNext()) {
                gen.addCertificates((Store)it.next());
            }
            it = SMIMESignedGenerator.this.crlStores.iterator();
            while (it.hasNext()) {
                gen.addCRLs((Store)it.next());
            }
            it = SMIMESignedGenerator.this.attrCertStores.iterator();
            while (it.hasNext()) {
                gen.addAttributeCertificates((Store)it.next());
            }
            it = SMIMESignedGenerator.this.signerInfoGens.iterator();
            while (it.hasNext()) {
                gen.addSignerInfoGenerator((SignerInfoGenerator)it.next());
            }
            gen.addSigners(new SignerInformationStore((Collection)SMIMESignedGenerator.this._oldSigners));
            return gen;
        }

        private void writeBodyPart(OutputStream out, MimeBodyPart bodyPart) throws IOException, MessagingException {
            if (SMIMEUtil.isMultipartContent((Part)bodyPart)) {
                Object content = bodyPart.getContent();
                Object mp = content instanceof Multipart ? (Multipart)content : new MimeMultipart(bodyPart.getDataHandler().getDataSource());
                ContentType contentType = new ContentType(mp.getContentType());
                String boundary = "--" + contentType.getParameter("boundary");
                SMIMEUtil.LineOutputStream lOut = new SMIMEUtil.LineOutputStream(out);
                Enumeration headers = bodyPart.getAllHeaderLines();
                while (headers.hasMoreElements()) {
                    lOut.writeln((String)headers.nextElement());
                }
                lOut.writeln();
                SMIMEUtil.outputPreamble(lOut, bodyPart, boundary);
                for (int i = 0; i < mp.getCount(); ++i) {
                    lOut.writeln(boundary);
                    this.writeBodyPart(out, (MimeBodyPart)mp.getBodyPart(i));
                    lOut.writeln();
                }
                lOut.writeln(boundary + "--");
            } else {
                if (SMIMEUtil.isCanonicalisationRequired(bodyPart, SMIMESignedGenerator.this.defaultContentTransferEncoding)) {
                    out = new CRLFOutputStream(out);
                }
                bodyPart.writeTo(out);
            }
        }

        @Override
        public void write(OutputStream out) throws IOException {
            try {
                CMSSignedDataStreamGenerator gen = this.getGenerator();
                OutputStream signingStream = gen.open(out, this.encapsulate);
                if (this.content != null) {
                    if (!this.encapsulate) {
                        this.writeBodyPart(signingStream, this.content);
                    } else {
                        CommandMap commandMap = CommandMap.getDefaultCommandMap();
                        if (commandMap instanceof MailcapCommandMap) {
                            this.content.getDataHandler().setCommandMap((CommandMap)MailcapUtil.addCommands((MailcapCommandMap)commandMap));
                        }
                        this.content.writeTo(signingStream);
                    }
                }
                signingStream.close();
                SMIMESignedGenerator.this._digests = gen.getGeneratedDigests();
            }
            catch (MessagingException e) {
                throw new IOException(e.toString());
            }
            catch (CMSException e) {
                throw new IOException(e.toString());
            }
        }
    }
}

