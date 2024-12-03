/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.x509.AuthorityKeyIdentifier
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.GeneralNames
 *  org.bouncycastle.asn1.x509.SubjectKeyIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.cert;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertRuntimeException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DigestCalculator;

public class X509ExtensionUtils {
    private DigestCalculator calculator;

    public X509ExtensionUtils(DigestCalculator calculator) {
        this.calculator = calculator;
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(X509CertificateHolder certHolder) {
        GeneralName genName = new GeneralName(certHolder.getIssuer());
        return new AuthorityKeyIdentifier(this.getSubjectKeyIdentifier(certHolder), new GeneralNames(genName), certHolder.getSerialNumber());
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(SubjectPublicKeyInfo publicKeyInfo) {
        return new AuthorityKeyIdentifier(this.calculateIdentifier(publicKeyInfo));
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(SubjectPublicKeyInfo publicKeyInfo, GeneralNames generalNames, BigInteger serial) {
        return new AuthorityKeyIdentifier(this.calculateIdentifier(publicKeyInfo), generalNames, serial);
    }

    public SubjectKeyIdentifier createSubjectKeyIdentifier(SubjectPublicKeyInfo publicKeyInfo) {
        return new SubjectKeyIdentifier(this.calculateIdentifier(publicKeyInfo));
    }

    public SubjectKeyIdentifier createTruncatedSubjectKeyIdentifier(SubjectPublicKeyInfo publicKeyInfo) {
        byte[] digest = this.calculateIdentifier(publicKeyInfo);
        byte[] id = new byte[8];
        System.arraycopy(digest, digest.length - 8, id, 0, id.length);
        id[0] = (byte)(id[0] & 0xF);
        id[0] = (byte)(id[0] | 0x40);
        return new SubjectKeyIdentifier(id);
    }

    private byte[] getSubjectKeyIdentifier(X509CertificateHolder certHolder) {
        if (certHolder.getVersionNumber() != 3) {
            return this.calculateIdentifier(certHolder.getSubjectPublicKeyInfo());
        }
        Extension ext = certHolder.getExtension(Extension.subjectKeyIdentifier);
        if (ext != null) {
            return ASN1OctetString.getInstance((Object)ext.getParsedValue()).getOctets();
        }
        return this.calculateIdentifier(certHolder.getSubjectPublicKeyInfo());
    }

    private byte[] calculateIdentifier(SubjectPublicKeyInfo publicKeyInfo) {
        byte[] bytes = publicKeyInfo.getPublicKeyData().getBytes();
        OutputStream cOut = this.calculator.getOutputStream();
        try {
            cOut.write(bytes);
            cOut.close();
        }
        catch (IOException e) {
            throw new CertRuntimeException("unable to calculate identifier: " + e.getMessage(), e);
        }
        return this.calculator.getDigest();
    }
}

