/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.cert.X509CertificateHolder
 *  org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
 *  org.bouncycastle.operator.OperatorCreationException
 *  org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
 */
package com.nimbusds.oauth2.sdk.util;

import com.nimbusds.jose.util.X509CertUtils;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.Subject;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public final class X509CertificateUtils {
    public static boolean hasMatchingIssuerAndSubject(X509Certificate cert) {
        Principal issuer = cert.getIssuerDN();
        Principal subject = cert.getSubjectDN();
        return issuer != null && issuer.equals(subject);
    }

    public static boolean isSelfIssued(X509Certificate cert) {
        return X509CertificateUtils.hasMatchingIssuerAndSubject(cert) && X509CertificateUtils.isSelfSigned(cert);
    }

    public static boolean isSelfSigned(X509Certificate cert) {
        PublicKey publicKey = cert.getPublicKey();
        return X509CertificateUtils.hasValidSignature(cert, publicKey);
    }

    public static boolean hasValidSignature(X509Certificate cert, PublicKey pubKey) {
        try {
            cert.verify(pubKey);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean publicKeyMatches(X509Certificate cert, PublicKey pubKey) {
        PublicKey certPubKey = cert.getPublicKey();
        return Arrays.equals(certPubKey.getEncoded(), pubKey.getEncoded());
    }

    public static X509Certificate generate(X500Principal issuer, X500Principal subject, Date nbf, Date exp, PublicKey certKey, PrivateKey signingKey) throws OperatorCreationException, IOException {
        String signingAlg;
        BigInteger serialNumber = new BigInteger(64, new SecureRandom());
        if ("RSA".equalsIgnoreCase(signingKey.getAlgorithm())) {
            signingAlg = "SHA256withRSA";
        } else if ("EC".equalsIgnoreCase(signingKey.getAlgorithm())) {
            signingAlg = "SHA256withECDSA";
        } else {
            throw new OperatorCreationException("Unsupported signing key algorithm: " + signingKey.getAlgorithm());
        }
        X509CertificateHolder certHolder = new JcaX509v3CertificateBuilder(issuer, serialNumber, nbf, exp, subject, certKey).build(new JcaContentSignerBuilder(signingAlg).build(signingKey));
        return X509CertUtils.parse(certHolder.getEncoded());
    }

    public static X509Certificate generate(Issuer issuer, Subject subject, Date nbf, Date exp, PublicKey certKey, PrivateKey signingKey) throws OperatorCreationException, IOException {
        return X509CertificateUtils.generate(new X500Principal("cn=" + issuer), new X500Principal("cn=" + subject), nbf, exp, certKey, signingKey);
    }

    public static X509Certificate generateSelfSigned(Issuer issuer, Date nbf, Date exp, PublicKey certKey, PrivateKey signingKey) throws OperatorCreationException, IOException {
        return X509CertificateUtils.generate(issuer, new Subject(issuer.getValue()), nbf, exp, certKey, signingKey);
    }

    private X509CertificateUtils() {
    }
}

