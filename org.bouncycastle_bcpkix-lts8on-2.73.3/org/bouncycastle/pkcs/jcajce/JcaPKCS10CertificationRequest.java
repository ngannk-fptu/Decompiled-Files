/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.pkcs.CertificationRequest
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.asn1.x9.X9ObjectIdentifiers
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 */
package org.bouncycastle.pkcs.jcajce;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Hashtable;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

public class JcaPKCS10CertificationRequest
extends PKCS10CertificationRequest {
    private static Hashtable keyAlgorithms = new Hashtable();
    private JcaJceHelper helper = new DefaultJcaJceHelper();

    public JcaPKCS10CertificationRequest(CertificationRequest certificationRequest) {
        super(certificationRequest);
    }

    public JcaPKCS10CertificationRequest(byte[] encoding) throws IOException {
        super(encoding);
    }

    public JcaPKCS10CertificationRequest(PKCS10CertificationRequest requestHolder) {
        super(requestHolder.toASN1Structure());
    }

    public JcaPKCS10CertificationRequest setProvider(String providerName) {
        this.helper = new NamedJcaJceHelper(providerName);
        return this;
    }

    public JcaPKCS10CertificationRequest setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public PublicKey getPublicKey() throws InvalidKeyException, NoSuchAlgorithmException {
        try {
            KeyFactory kFact;
            SubjectPublicKeyInfo keyInfo = this.getSubjectPublicKeyInfo();
            X509EncodedKeySpec xspec = new X509EncodedKeySpec(keyInfo.getEncoded());
            try {
                kFact = this.helper.createKeyFactory(keyInfo.getAlgorithm().getAlgorithm().getId());
            }
            catch (NoSuchAlgorithmException e) {
                if (keyAlgorithms.get(keyInfo.getAlgorithm().getAlgorithm()) != null) {
                    String keyAlgorithm = (String)keyAlgorithms.get(keyInfo.getAlgorithm().getAlgorithm());
                    kFact = this.helper.createKeyFactory(keyAlgorithm);
                }
                throw e;
            }
            return kFact.generatePublic(xspec);
        }
        catch (InvalidKeySpecException e) {
            throw new InvalidKeyException("error decoding public key");
        }
        catch (IOException e) {
            throw new InvalidKeyException("error extracting key encoding");
        }
        catch (NoSuchProviderException e) {
            throw new NoSuchAlgorithmException("cannot find provider: " + e.getMessage());
        }
    }

    static {
        keyAlgorithms.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
        keyAlgorithms.put(X9ObjectIdentifiers.id_dsa, "DSA");
        keyAlgorithms.put(X9ObjectIdentifiers.id_ecPublicKey, "EC");
    }
}

