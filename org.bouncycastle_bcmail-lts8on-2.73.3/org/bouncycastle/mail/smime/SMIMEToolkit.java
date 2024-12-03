/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.MessagingException
 *  javax.mail.Multipart
 *  javax.mail.Part
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMessage
 *  javax.mail.internet.MimeMultipart
 *  org.bouncycastle.cert.X509CertificateHolder
 *  org.bouncycastle.cms.CMSException
 *  org.bouncycastle.cms.Recipient
 *  org.bouncycastle.cms.RecipientId
 *  org.bouncycastle.cms.RecipientInfoGenerator
 *  org.bouncycastle.cms.RecipientInformation
 *  org.bouncycastle.cms.RecipientInformationStore
 *  org.bouncycastle.cms.SignerId
 *  org.bouncycastle.cms.SignerInfoGenerator
 *  org.bouncycastle.cms.SignerInformation
 *  org.bouncycastle.cms.SignerInformationVerifier
 *  org.bouncycastle.operator.DigestCalculatorProvider
 *  org.bouncycastle.operator.OutputEncryptor
 *  org.bouncycastle.util.CollectionStore
 *  org.bouncycastle.util.Selector
 *  org.bouncycastle.util.Store
 */
package org.bouncycastle.mail.smime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMEEnvelopedParser;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.mail.smime.SMIMESignedParser;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

public class SMIMEToolkit {
    private final DigestCalculatorProvider digestCalculatorProvider;

    public SMIMEToolkit(DigestCalculatorProvider digestCalculatorProvider) {
        this.digestCalculatorProvider = digestCalculatorProvider;
    }

    public boolean isEncrypted(Part message) throws MessagingException {
        return message.getHeader("Content-Type")[0].equals("application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data");
    }

    public boolean isSigned(Part message) throws MessagingException {
        return message.getHeader("Content-Type")[0].startsWith("multipart/signed") || message.getHeader("Content-Type")[0].equals("application/pkcs7-mime; name=smime.p7m; smime-type=signed-data");
    }

    public boolean isSigned(MimeMultipart message) throws MessagingException {
        return message.getBodyPart(1).getHeader("Content-Type")[0].equals("application/pkcs7-signature; name=smime.p7s; smime-type=signed-data");
    }

    public boolean isValidSignature(Part message, SignerInformationVerifier verifier) throws SMIMEException, MessagingException {
        try {
            SMIMESignedParser s = message.isMimeType("multipart/signed") ? new SMIMESignedParser(this.digestCalculatorProvider, (MimeMultipart)message.getContent()) : new SMIMESignedParser(this.digestCalculatorProvider, message);
            return this.isAtLeastOneValidSigner(s, verifier);
        }
        catch (CMSException e) {
            throw new SMIMEException("CMS processing failure: " + e.getMessage(), (Exception)((Object)e));
        }
        catch (IOException e) {
            throw new SMIMEException("Parsing failure: " + e.getMessage(), e);
        }
    }

    private boolean isAtLeastOneValidSigner(SMIMESignedParser s, SignerInformationVerifier verifier) throws CMSException {
        if (verifier.hasAssociatedCertificate()) {
            X509CertificateHolder cert = verifier.getAssociatedCertificate();
            SignerInformation signer = s.getSignerInfos().get(new SignerId(cert.getIssuer(), cert.getSerialNumber()));
            if (signer != null) {
                return signer.verify(verifier);
            }
        }
        Collection c = s.getSignerInfos().getSigners();
        for (SignerInformation signer : c) {
            if (!signer.verify(verifier)) continue;
            return true;
        }
        return false;
    }

    public boolean isValidSignature(MimeMultipart message, SignerInformationVerifier verifier) throws SMIMEException, MessagingException {
        try {
            SMIMESignedParser s = new SMIMESignedParser(this.digestCalculatorProvider, message);
            return this.isAtLeastOneValidSigner(s, verifier);
        }
        catch (CMSException e) {
            throw new SMIMEException("CMS processing failure: " + e.getMessage(), (Exception)((Object)e));
        }
    }

    public X509CertificateHolder extractCertificate(Part message, SignerInformation signerInformation) throws SMIMEException, MessagingException {
        try {
            SMIMESignedParser s = message instanceof MimeMessage && message.isMimeType("multipart/signed") ? new SMIMESignedParser(this.digestCalculatorProvider, (MimeMultipart)message.getContent()) : new SMIMESignedParser(this.digestCalculatorProvider, message);
            Collection certCollection = s.getCertificates().getMatches((Selector)signerInformation.getSID());
            Iterator certIt = certCollection.iterator();
            if (certIt.hasNext()) {
                return (X509CertificateHolder)certIt.next();
            }
            return null;
        }
        catch (CMSException e) {
            throw new SMIMEException("CMS processing failure: " + e.getMessage(), (Exception)((Object)e));
        }
        catch (IOException e) {
            throw new SMIMEException("Parsing failure: " + e.getMessage(), e);
        }
    }

    public X509CertificateHolder extractCertificate(MimeMultipart message, SignerInformation signerInformation) throws SMIMEException, MessagingException {
        try {
            SMIMESignedParser s = new SMIMESignedParser(this.digestCalculatorProvider, message);
            Collection certCollection = s.getCertificates().getMatches((Selector)signerInformation.getSID());
            Iterator certIt = certCollection.iterator();
            if (certIt.hasNext()) {
                return (X509CertificateHolder)certIt.next();
            }
            return null;
        }
        catch (CMSException e) {
            throw new SMIMEException("CMS processing failure: " + e.getMessage(), (Exception)((Object)e));
        }
    }

    public MimeMultipart sign(MimeBodyPart message, SignerInfoGenerator signerInfoGenerator) throws SMIMEException {
        SMIMESignedGenerator gen = new SMIMESignedGenerator();
        if (signerInfoGenerator.hasAssociatedCertificate()) {
            ArrayList<X509CertificateHolder> certList = new ArrayList<X509CertificateHolder>();
            certList.add(signerInfoGenerator.getAssociatedCertificate());
            gen.addCertificates((Store)new CollectionStore(certList));
        }
        gen.addSignerInfoGenerator(signerInfoGenerator);
        return gen.generate(message);
    }

    public MimeBodyPart signEncapsulated(MimeBodyPart message, SignerInfoGenerator signerInfoGenerator) throws SMIMEException {
        SMIMESignedGenerator gen = new SMIMESignedGenerator();
        if (signerInfoGenerator.hasAssociatedCertificate()) {
            ArrayList<X509CertificateHolder> certList = new ArrayList<X509CertificateHolder>();
            certList.add(signerInfoGenerator.getAssociatedCertificate());
            gen.addCertificates((Store)new CollectionStore(certList));
        }
        gen.addSignerInfoGenerator(signerInfoGenerator);
        return gen.generateEncapsulated(message);
    }

    public MimeBodyPart encrypt(MimeBodyPart mimePart, OutputEncryptor contentEncryptor, RecipientInfoGenerator recipientGenerator) throws SMIMEException {
        SMIMEEnvelopedGenerator envGen = new SMIMEEnvelopedGenerator();
        envGen.addRecipientInfoGenerator(recipientGenerator);
        return envGen.generate(mimePart, contentEncryptor);
    }

    public MimeBodyPart encrypt(MimeMultipart multiPart, OutputEncryptor contentEncryptor, RecipientInfoGenerator recipientGenerator) throws SMIMEException, MessagingException {
        SMIMEEnvelopedGenerator envGen = new SMIMEEnvelopedGenerator();
        envGen.addRecipientInfoGenerator(recipientGenerator);
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent((Multipart)multiPart);
        return envGen.generate(bodyPart, contentEncryptor);
    }

    public MimeBodyPart encrypt(MimeMessage message, OutputEncryptor contentEncryptor, RecipientInfoGenerator recipientGenerator) throws SMIMEException {
        SMIMEEnvelopedGenerator envGen = new SMIMEEnvelopedGenerator();
        envGen.addRecipientInfoGenerator(recipientGenerator);
        return envGen.generate(message, contentEncryptor);
    }

    public MimeBodyPart decrypt(MimeBodyPart mimePart, RecipientId recipientId, Recipient recipient) throws SMIMEException, MessagingException {
        try {
            SMIMEEnvelopedParser m = new SMIMEEnvelopedParser(mimePart);
            RecipientInformationStore recipients = m.getRecipientInfos();
            RecipientInformation recipientInformation = recipients.get(recipientId);
            if (recipientInformation == null) {
                return null;
            }
            return SMIMEUtil.toMimeBodyPart(recipientInformation.getContent(recipient));
        }
        catch (CMSException e) {
            throw new SMIMEException("CMS processing failure: " + e.getMessage(), (Exception)((Object)e));
        }
        catch (IOException e) {
            throw new SMIMEException("Parsing failure: " + e.getMessage(), e);
        }
    }

    public MimeBodyPart decrypt(MimeMessage message, RecipientId recipientId, Recipient recipient) throws SMIMEException, MessagingException {
        try {
            SMIMEEnvelopedParser m = new SMIMEEnvelopedParser(message);
            RecipientInformationStore recipients = m.getRecipientInfos();
            RecipientInformation recipientInformation = recipients.get(recipientId);
            if (recipientInformation == null) {
                return null;
            }
            return SMIMEUtil.toMimeBodyPart(recipientInformation.getContent(recipient));
        }
        catch (CMSException e) {
            throw new SMIMEException("CMS processing failure: " + e.getMessage(), (Exception)((Object)e));
        }
        catch (IOException e) {
            throw new SMIMEException("Parsing failure: " + e.getMessage(), e);
        }
    }
}

