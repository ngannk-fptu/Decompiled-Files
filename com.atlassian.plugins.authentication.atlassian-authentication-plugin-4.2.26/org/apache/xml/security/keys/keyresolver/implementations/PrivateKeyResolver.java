/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.keys.keyresolver.implementations;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import javax.crypto.SecretKey;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.keys.content.x509.XMLX509IssuerSerial;
import org.apache.xml.security.keys.content.x509.XMLX509SKI;
import org.apache.xml.security.keys.content.x509.XMLX509SubjectName;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class PrivateKeyResolver
extends KeyResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger(PrivateKeyResolver.class);
    private final KeyStore keyStore;
    private final char[] password;

    public PrivateKeyResolver(KeyStore keyStore, char[] password) {
        this.keyStore = keyStore;
        this.password = password;
    }

    @Override
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        return XMLUtils.elementIsInSignatureSpace(element, "X509Data") || XMLUtils.elementIsInSignatureSpace(element, "KeyName");
    }

    @Override
    protected PublicKey engineResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        return null;
    }

    @Override
    protected X509Certificate engineResolveX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        return null;
    }

    @Override
    protected SecretKey engineResolveSecretKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        return null;
    }

    @Override
    public PrivateKey engineResolvePrivateKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        if (XMLUtils.elementIsInSignatureSpace(element, "X509Data")) {
            PrivateKey privKey = this.resolveX509Data(element, baseURI);
            if (privKey != null) {
                return privKey;
            }
        } else if (XMLUtils.elementIsInSignatureSpace(element, "KeyName")) {
            LOG.debug("Can I resolve KeyName?");
            String keyName = element.getFirstChild().getNodeValue();
            try {
                Key key = this.keyStore.getKey(keyName, this.password);
                if (key instanceof PrivateKey) {
                    return (PrivateKey)key;
                }
            }
            catch (Exception e) {
                LOG.debug("Cannot recover the key", (Throwable)e);
            }
        }
        return null;
    }

    private PrivateKey resolveX509Data(Element element, String baseURI) {
        LOG.debug("Can I resolve X509Data?");
        try {
            PrivateKey privKey;
            int i;
            X509Data x509Data = new X509Data(element, baseURI);
            int len = x509Data.lengthSKI();
            for (i = 0; i < len; ++i) {
                XMLX509SKI x509SKI = x509Data.itemSKI(i);
                privKey = this.resolveX509SKI(x509SKI);
                if (privKey == null) continue;
                return privKey;
            }
            len = x509Data.lengthIssuerSerial();
            for (i = 0; i < len; ++i) {
                XMLX509IssuerSerial x509Serial = x509Data.itemIssuerSerial(i);
                privKey = this.resolveX509IssuerSerial(x509Serial);
                if (privKey == null) continue;
                return privKey;
            }
            len = x509Data.lengthSubjectName();
            for (i = 0; i < len; ++i) {
                XMLX509SubjectName x509SubjectName = x509Data.itemSubjectName(i);
                privKey = this.resolveX509SubjectName(x509SubjectName);
                if (privKey == null) continue;
                return privKey;
            }
            len = x509Data.lengthCertificate();
            for (i = 0; i < len; ++i) {
                XMLX509Certificate x509Cert = x509Data.itemCertificate(i);
                privKey = this.resolveX509Certificate(x509Cert);
                if (privKey == null) continue;
                return privKey;
            }
        }
        catch (XMLSecurityException e) {
            LOG.debug("XMLSecurityException", (Throwable)e);
        }
        catch (KeyStoreException e) {
            LOG.debug("KeyStoreException", (Throwable)e);
        }
        return null;
    }

    private PrivateKey resolveX509SKI(XMLX509SKI x509SKI) throws XMLSecurityException, KeyStoreException {
        LOG.debug("Can I resolve X509SKI?");
        Enumeration<String> aliases = this.keyStore.aliases();
        while (aliases.hasMoreElements()) {
            XMLX509SKI certSKI;
            Certificate cert;
            String alias = aliases.nextElement();
            if (!this.keyStore.isKeyEntry(alias) || !((cert = this.keyStore.getCertificate(alias)) instanceof X509Certificate) || !(certSKI = new XMLX509SKI(x509SKI.getDocument(), (X509Certificate)cert)).equals(x509SKI)) continue;
            LOG.debug("match !!! ");
            try {
                Key key = this.keyStore.getKey(alias, this.password);
                if (!(key instanceof PrivateKey)) continue;
                return (PrivateKey)key;
            }
            catch (Exception e) {
                LOG.debug("Cannot recover the key", (Throwable)e);
            }
        }
        return null;
    }

    private PrivateKey resolveX509IssuerSerial(XMLX509IssuerSerial x509Serial) throws KeyStoreException {
        LOG.debug("Can I resolve X509IssuerSerial?");
        Enumeration<String> aliases = this.keyStore.aliases();
        while (aliases.hasMoreElements()) {
            XMLX509IssuerSerial certSerial;
            Certificate cert;
            String alias = aliases.nextElement();
            if (!this.keyStore.isKeyEntry(alias) || !((cert = this.keyStore.getCertificate(alias)) instanceof X509Certificate) || !(certSerial = new XMLX509IssuerSerial(x509Serial.getDocument(), (X509Certificate)cert)).equals(x509Serial)) continue;
            LOG.debug("match !!! ");
            try {
                Key key = this.keyStore.getKey(alias, this.password);
                if (!(key instanceof PrivateKey)) continue;
                return (PrivateKey)key;
            }
            catch (Exception e) {
                LOG.debug("Cannot recover the key", (Throwable)e);
            }
        }
        return null;
    }

    private PrivateKey resolveX509SubjectName(XMLX509SubjectName x509SubjectName) throws KeyStoreException {
        LOG.debug("Can I resolve X509SubjectName?");
        Enumeration<String> aliases = this.keyStore.aliases();
        while (aliases.hasMoreElements()) {
            XMLX509SubjectName certSN;
            Certificate cert;
            String alias = aliases.nextElement();
            if (!this.keyStore.isKeyEntry(alias) || !((cert = this.keyStore.getCertificate(alias)) instanceof X509Certificate) || !(certSN = new XMLX509SubjectName(x509SubjectName.getDocument(), (X509Certificate)cert)).equals(x509SubjectName)) continue;
            LOG.debug("match !!! ");
            try {
                Key key = this.keyStore.getKey(alias, this.password);
                if (!(key instanceof PrivateKey)) continue;
                return (PrivateKey)key;
            }
            catch (Exception e) {
                LOG.debug("Cannot recover the key", (Throwable)e);
            }
        }
        return null;
    }

    private PrivateKey resolveX509Certificate(XMLX509Certificate x509Cert) throws XMLSecurityException, KeyStoreException {
        LOG.debug("Can I resolve X509Certificate?");
        byte[] x509CertBytes = x509Cert.getCertificateBytes();
        Enumeration<String> aliases = this.keyStore.aliases();
        while (aliases.hasMoreElements()) {
            Certificate cert;
            String alias = aliases.nextElement();
            if (!this.keyStore.isKeyEntry(alias) || !((cert = this.keyStore.getCertificate(alias)) instanceof X509Certificate)) continue;
            byte[] certBytes = null;
            try {
                certBytes = cert.getEncoded();
            }
            catch (CertificateEncodingException e1) {
                LOG.debug("Cannot recover the key", (Throwable)e1);
            }
            if (certBytes == null || !Arrays.equals(certBytes, x509CertBytes)) continue;
            LOG.debug("match !!! ");
            try {
                Key key = this.keyStore.getKey(alias, this.password);
                if (!(key instanceof PrivateKey)) continue;
                return (PrivateKey)key;
            }
            catch (Exception e) {
                LOG.debug("Cannot recover the key", (Throwable)e);
            }
        }
        return null;
    }
}

