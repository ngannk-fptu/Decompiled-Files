/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1String
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x500.X500NameStyle
 *  org.bouncycastle.asn1.x500.style.RFC4519Style
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.AuthorityKeyIdentifier
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.GeneralNames
 *  org.bouncycastle.asn1.x509.SubjectKeyIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.util.Integers
 */
package org.bouncycastle.cert.jcajce;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Integers;

public class JcaX509ExtensionUtils
extends X509ExtensionUtils {
    public JcaX509ExtensionUtils() throws NoSuchAlgorithmException {
        super(new SHA1DigestCalculator(MessageDigest.getInstance("SHA1")));
    }

    public JcaX509ExtensionUtils(DigestCalculator calculator) {
        super(calculator);
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(X509Certificate cert) throws CertificateEncodingException {
        return super.createAuthorityKeyIdentifier(new JcaX509CertificateHolder(cert));
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(PublicKey pubKey) {
        return super.createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance((Object)pubKey.getEncoded()));
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(PublicKey pubKey, X500Principal name, BigInteger serial) {
        return super.createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance((Object)pubKey.getEncoded()), new GeneralNames(new GeneralName(X500Name.getInstance((Object)name.getEncoded()))), serial);
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(PublicKey pubKey, GeneralNames generalNames, BigInteger serial) {
        return super.createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance((Object)pubKey.getEncoded()), generalNames, serial);
    }

    public SubjectKeyIdentifier createSubjectKeyIdentifier(PublicKey publicKey) {
        return super.createSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }

    public SubjectKeyIdentifier createTruncatedSubjectKeyIdentifier(PublicKey publicKey) {
        return super.createTruncatedSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }

    public static ASN1Primitive parseExtensionValue(byte[] encExtValue) throws IOException {
        return ASN1Primitive.fromByteArray((byte[])ASN1OctetString.getInstance((Object)encExtValue).getOctets());
    }

    public static Collection getIssuerAlternativeNames(X509Certificate cert) throws CertificateParsingException {
        byte[] extVal = cert.getExtensionValue(Extension.issuerAlternativeName.getId());
        return JcaX509ExtensionUtils.getAlternativeNames(extVal);
    }

    public static Collection getSubjectAlternativeNames(X509Certificate cert) throws CertificateParsingException {
        byte[] extVal = cert.getExtensionValue(Extension.subjectAlternativeName.getId());
        return JcaX509ExtensionUtils.getAlternativeNames(extVal);
    }

    private static Collection getAlternativeNames(byte[] extVal) throws CertificateParsingException {
        if (extVal == null) {
            return Collections.EMPTY_LIST;
        }
        try {
            ArrayList temp = new ArrayList();
            Enumeration it = DERSequence.getInstance((Object)JcaX509ExtensionUtils.parseExtensionValue(extVal)).getObjects();
            block11: while (it.hasMoreElements()) {
                GeneralName genName = GeneralName.getInstance(it.nextElement());
                ArrayList<Object> list = new ArrayList<Object>();
                list.add(Integers.valueOf((int)genName.getTagNo()));
                switch (genName.getTagNo()) {
                    case 0: 
                    case 3: 
                    case 5: {
                        list.add(genName.getEncoded());
                        break;
                    }
                    case 4: {
                        list.add(X500Name.getInstance((X500NameStyle)RFC4519Style.INSTANCE, (Object)genName.getName()).toString());
                        break;
                    }
                    case 1: 
                    case 2: 
                    case 6: {
                        list.add(((ASN1String)genName.getName()).getString());
                        break;
                    }
                    case 8: {
                        list.add(ASN1ObjectIdentifier.getInstance((Object)genName.getName()).getId());
                        break;
                    }
                    case 7: {
                        String addr;
                        byte[] addrBytes = DEROctetString.getInstance((Object)genName.getName()).getOctets();
                        try {
                            addr = InetAddress.getByAddress(addrBytes).getHostAddress();
                        }
                        catch (UnknownHostException e) {
                            continue block11;
                        }
                        list.add(addr);
                        break;
                    }
                    default: {
                        throw new IOException("Bad tag number: " + genName.getTagNo());
                    }
                }
                temp.add(list);
            }
            return Collections.unmodifiableCollection(temp);
        }
        catch (Exception e) {
            throw new CertificateParsingException(e.getMessage());
        }
    }

    private static class SHA1DigestCalculator
    implements DigestCalculator {
        private ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        private MessageDigest digest;

        public SHA1DigestCalculator(MessageDigest digest) {
            this.digest = digest;
        }

        @Override
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
        }

        @Override
        public OutputStream getOutputStream() {
            return this.bOut;
        }

        @Override
        public byte[] getDigest() {
            byte[] bytes = this.digest.digest(this.bOut.toByteArray());
            this.bOut.reset();
            return bytes;
        }
    }
}

