/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DEROutputStream
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.EncryptedContentInfo
 *  org.bouncycastle.asn1.cms.EnvelopedData
 *  org.bouncycastle.asn1.cms.IssuerAndSerialNumber
 *  org.bouncycastle.asn1.cms.KeyTransRecipientInfo
 *  org.bouncycastle.asn1.cms.RecipientIdentifier
 *  org.bouncycastle.asn1.cms.RecipientInfo
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.TBSCertificate
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfPublicKeyRecipient;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.TBSCertificate;

public class PdfPublicKeySecurityHandler {
    static final int SEED_LENGTH = 20;
    private List<PdfPublicKeyRecipient> recipients = null;
    private byte[] seed = new byte[20];

    public PdfPublicKeySecurityHandler() {
        try {
            KeyGenerator key = KeyGenerator.getInstance("AES");
            key.init(192, new SecureRandom());
            SecretKey sk = key.generateKey();
            System.arraycopy(sk.getEncoded(), 0, this.seed, 0, 20);
        }
        catch (NoSuchAlgorithmException e) {
            this.seed = SecureRandom.getSeed(20);
        }
        this.recipients = new ArrayList<PdfPublicKeyRecipient>();
    }

    public void addRecipient(PdfPublicKeyRecipient recipient) {
        this.recipients.add(recipient);
    }

    protected byte[] getSeed() {
        return (byte[])this.seed.clone();
    }

    public int getRecipientsSize() {
        return this.recipients.size();
    }

    public byte[] getEncodedRecipient(int index) throws IOException, GeneralSecurityException {
        PdfPublicKeyRecipient recipient = this.recipients.get(index);
        byte[] cms = recipient.getCms();
        if (cms != null) {
            return cms;
        }
        Certificate certificate = recipient.getCertificate();
        int permission = recipient.getPermission();
        int revision = 3;
        permission |= 0xFFFFF0C0;
        permission &= 0xFFFFFFFC;
        byte[] pkcs7input = new byte[24];
        byte one = (byte)(++permission);
        byte two = (byte)(permission >> 8);
        byte three = (byte)(permission >> 16);
        byte four = (byte)(permission >> 24);
        System.arraycopy(this.seed, 0, pkcs7input, 0, 20);
        pkcs7input[20] = four;
        pkcs7input[21] = three;
        pkcs7input[22] = two;
        pkcs7input[23] = one;
        ASN1Primitive obj = this.createDERForRecipient(pkcs7input, (X509Certificate)certificate);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DEROutputStream k = new DEROutputStream((OutputStream)baos);
        k.writeObject(obj);
        cms = baos.toByteArray();
        recipient.setCms(cms);
        return cms;
    }

    public PdfArray getEncodedRecipients() throws IOException {
        PdfArray encodedRecipients = new PdfArray();
        byte[] cms = null;
        for (int i = 0; i < this.recipients.size(); ++i) {
            try {
                cms = this.getEncodedRecipient(i);
                encodedRecipients.add(new PdfLiteral(PdfContentByte.escapeString(cms)));
                continue;
            }
            catch (IOException | GeneralSecurityException e) {
                encodedRecipients = null;
                break;
            }
        }
        return encodedRecipients;
    }

    private ASN1Primitive createDERForRecipient(byte[] in, X509Certificate cert) throws IOException, GeneralSecurityException {
        String s = "1.2.840.113549.3.2";
        AlgorithmParameterGenerator algorithmparametergenerator = AlgorithmParameterGenerator.getInstance(s);
        AlgorithmParameters algorithmparameters = algorithmparametergenerator.generateParameters();
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(algorithmparameters.getEncoded("ASN.1"));
        ASN1InputStream asn1inputstream = new ASN1InputStream((InputStream)bytearrayinputstream);
        ASN1Primitive derobject = asn1inputstream.readObject();
        KeyGenerator keygenerator = KeyGenerator.getInstance(s);
        keygenerator.init(128);
        SecretKey secretkey = keygenerator.generateKey();
        Cipher cipher = Cipher.getInstance(s);
        cipher.init(1, (Key)secretkey, algorithmparameters);
        byte[] abyte1 = cipher.doFinal(in);
        DEROctetString deroctetstring = new DEROctetString(abyte1);
        KeyTransRecipientInfo keytransrecipientinfo = this.computeRecipientInfo(cert, secretkey.getEncoded());
        DERSet derset = new DERSet((ASN1Encodable)new RecipientInfo(keytransrecipientinfo));
        AlgorithmIdentifier algorithmidentifier = new AlgorithmIdentifier(new ASN1ObjectIdentifier(s), (ASN1Encodable)derobject);
        EncryptedContentInfo encryptedcontentinfo = new EncryptedContentInfo(PKCSObjectIdentifiers.data, algorithmidentifier, (ASN1OctetString)deroctetstring);
        ASN1Set set = null;
        EnvelopedData env = new EnvelopedData(null, (ASN1Set)derset, encryptedcontentinfo, set);
        ContentInfo contentinfo = new ContentInfo(PKCSObjectIdentifiers.envelopedData, (ASN1Encodable)env);
        return contentinfo.toASN1Primitive();
    }

    private KeyTransRecipientInfo computeRecipientInfo(X509Certificate x509certificate, byte[] abyte0) throws GeneralSecurityException, IOException {
        ASN1InputStream asn1inputstream = new ASN1InputStream((InputStream)new ByteArrayInputStream(x509certificate.getTBSCertificate()));
        TBSCertificate tbsCertificate = TBSCertificate.getInstance((Object)asn1inputstream.readObject());
        AlgorithmIdentifier algorithmidentifier = tbsCertificate.getSubjectPublicKeyInfo().getAlgorithm();
        IssuerAndSerialNumber issuerandserialnumber = new IssuerAndSerialNumber(tbsCertificate.getIssuer(), tbsCertificate.getSerialNumber().getValue());
        Cipher cipher = Cipher.getInstance(algorithmidentifier.getAlgorithm().getId());
        cipher.init(1, x509certificate);
        DEROctetString deroctetstring = new DEROctetString(cipher.doFinal(abyte0));
        RecipientIdentifier recipId = new RecipientIdentifier(issuerandserialnumber);
        return new KeyTransRecipientInfo(recipId, algorithmidentifier, (ASN1OctetString)deroctetstring);
    }
}

