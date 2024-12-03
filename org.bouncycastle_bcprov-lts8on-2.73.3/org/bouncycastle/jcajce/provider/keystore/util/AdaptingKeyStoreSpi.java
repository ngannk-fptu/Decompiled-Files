/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.keystore.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi;
import org.bouncycastle.jcajce.provider.keystore.util.JKSKeyStoreSpi;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Properties;

public class AdaptingKeyStoreSpi
extends KeyStoreSpi {
    public static final String COMPAT_OVERRIDE = "keystore.type.compat";
    private final JKSKeyStoreSpi jksStore;
    private final KeyStoreSpi primaryStore;
    private KeyStoreSpi keyStoreSpi;

    public AdaptingKeyStoreSpi(JcaJceHelper helper, KeyStoreSpi primaryStore) {
        this.jksStore = new JKSKeyStoreSpi(helper);
        this.primaryStore = primaryStore;
        this.keyStoreSpi = primaryStore;
    }

    @Override
    public boolean engineProbe(InputStream stream) throws IOException {
        if (this.keyStoreSpi instanceof PKCS12KeyStoreSpi) {
            return ((PKCS12KeyStoreSpi)this.keyStoreSpi).engineProbe(stream);
        }
        return false;
    }

    @Override
    public Key engineGetKey(String alias, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        return this.keyStoreSpi.engineGetKey(alias, password);
    }

    @Override
    public Certificate[] engineGetCertificateChain(String alias) {
        return this.keyStoreSpi.engineGetCertificateChain(alias);
    }

    @Override
    public Certificate engineGetCertificate(String alias) {
        return this.keyStoreSpi.engineGetCertificate(alias);
    }

    @Override
    public Date engineGetCreationDate(String alias) {
        return this.keyStoreSpi.engineGetCreationDate(alias);
    }

    @Override
    public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
        this.keyStoreSpi.engineSetKeyEntry(alias, key, password, chain);
    }

    @Override
    public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) throws KeyStoreException {
        this.keyStoreSpi.engineSetKeyEntry(alias, key, chain);
    }

    @Override
    public void engineSetCertificateEntry(String alias, Certificate cert) throws KeyStoreException {
        this.keyStoreSpi.engineSetCertificateEntry(alias, cert);
    }

    @Override
    public void engineDeleteEntry(String alias) throws KeyStoreException {
        this.keyStoreSpi.engineDeleteEntry(alias);
    }

    @Override
    public Enumeration<String> engineAliases() {
        return this.keyStoreSpi.engineAliases();
    }

    @Override
    public boolean engineContainsAlias(String alias) {
        return this.keyStoreSpi.engineContainsAlias(alias);
    }

    @Override
    public int engineSize() {
        return this.keyStoreSpi.engineSize();
    }

    @Override
    public boolean engineIsKeyEntry(String alias) {
        return this.keyStoreSpi.engineIsKeyEntry(alias);
    }

    @Override
    public boolean engineIsCertificateEntry(String alias) {
        return this.keyStoreSpi.engineIsCertificateEntry(alias);
    }

    @Override
    public String engineGetCertificateAlias(Certificate cert) {
        return this.keyStoreSpi.engineGetCertificateAlias(cert);
    }

    @Override
    public void engineStore(OutputStream stream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        this.keyStoreSpi.engineStore(stream, password);
    }

    @Override
    public void engineStore(KeyStore.LoadStoreParameter parameter) throws IOException, NoSuchAlgorithmException, CertificateException {
        this.keyStoreSpi.engineStore(parameter);
    }

    @Override
    public void engineLoad(InputStream stream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (stream == null) {
            this.keyStoreSpi = this.primaryStore;
            this.keyStoreSpi.engineLoad(null, password);
        } else {
            if (Properties.isOverrideSet(COMPAT_OVERRIDE) || !(this.primaryStore instanceof PKCS12KeyStoreSpi)) {
                if (!stream.markSupported()) {
                    stream = new BufferedInputStream(stream);
                }
                stream.mark(8);
                this.keyStoreSpi = this.jksStore.engineProbe(stream) ? this.jksStore : this.primaryStore;
                stream.reset();
            } else {
                this.keyStoreSpi = this.primaryStore;
            }
            this.keyStoreSpi.engineLoad(stream, password);
        }
    }

    @Override
    public void engineLoad(KeyStore.LoadStoreParameter parameter) throws IOException, NoSuchAlgorithmException, CertificateException {
        this.keyStoreSpi.engineLoad(parameter);
    }
}

