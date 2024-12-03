/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.keystore.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.jcajce.BCLoadStoreParameter;
import org.bouncycastle.jcajce.provider.keystore.util.ParameterUtil;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.Streams;

public class JKSKeyStoreSpi
extends KeyStoreSpi {
    private static final String NOT_IMPLEMENTED_MESSAGE = "BC JKS store is read-only and only supports certificate entries";
    private final Hashtable<String, BCJKSTrustedCertEntry> certificateEntries = new Hashtable();
    private final JcaJceHelper helper;

    public JKSKeyStoreSpi(JcaJceHelper helper) {
        this.helper = helper;
    }

    @Override
    public boolean engineProbe(InputStream stream) throws IOException {
        DataInputStream storeStream = stream instanceof DataInputStream ? (DataInputStream)stream : new DataInputStream(stream);
        int magic = storeStream.readInt();
        int storeVersion = storeStream.readInt();
        return magic == -17957139 && (storeVersion == 1 || storeVersion == 2);
    }

    @Override
    public Key engineGetKey(String alias, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        return null;
    }

    @Override
    public Certificate[] engineGetCertificateChain(String alias) {
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Certificate engineGetCertificate(String alias) {
        Hashtable<String, BCJKSTrustedCertEntry> hashtable = this.certificateEntries;
        synchronized (hashtable) {
            BCJKSTrustedCertEntry ent = this.certificateEntries.get(alias);
            if (ent != null) {
                return ent.cert;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Date engineGetCreationDate(String alias) {
        Hashtable<String, BCJKSTrustedCertEntry> hashtable = this.certificateEntries;
        synchronized (hashtable) {
            BCJKSTrustedCertEntry ent = this.certificateEntries.get(alias);
            if (ent != null) {
                return ent.date;
            }
        }
        return null;
    }

    @Override
    public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
        throw new KeyStoreException(NOT_IMPLEMENTED_MESSAGE);
    }

    @Override
    public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) throws KeyStoreException {
        throw new KeyStoreException(NOT_IMPLEMENTED_MESSAGE);
    }

    @Override
    public void engineSetCertificateEntry(String alias, Certificate cert) throws KeyStoreException {
        throw new KeyStoreException(NOT_IMPLEMENTED_MESSAGE);
    }

    @Override
    public void engineDeleteEntry(String alias) throws KeyStoreException {
        throw new KeyStoreException(NOT_IMPLEMENTED_MESSAGE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Enumeration<String> engineAliases() {
        Hashtable<String, BCJKSTrustedCertEntry> hashtable = this.certificateEntries;
        synchronized (hashtable) {
            return this.certificateEntries.keys();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean engineContainsAlias(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias value is null");
        }
        Hashtable<String, BCJKSTrustedCertEntry> hashtable = this.certificateEntries;
        synchronized (hashtable) {
            return this.certificateEntries.containsKey(alias);
        }
    }

    @Override
    public int engineSize() {
        return this.certificateEntries.size();
    }

    @Override
    public boolean engineIsKeyEntry(String alias) {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean engineIsCertificateEntry(String alias) {
        Hashtable<String, BCJKSTrustedCertEntry> hashtable = this.certificateEntries;
        synchronized (hashtable) {
            return this.certificateEntries.containsKey(alias);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String engineGetCertificateAlias(Certificate cert) {
        Hashtable<String, BCJKSTrustedCertEntry> hashtable = this.certificateEntries;
        synchronized (hashtable) {
            for (Map.Entry<String, BCJKSTrustedCertEntry> entry : this.certificateEntries.entrySet()) {
                if (!entry.getValue().cert.equals(cert)) continue;
                return entry.getKey();
            }
            return null;
        }
    }

    @Override
    public void engineStore(OutputStream stream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        throw new IOException(NOT_IMPLEMENTED_MESSAGE);
    }

    @Override
    public void engineLoad(KeyStore.LoadStoreParameter loadStoreParameter) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (loadStoreParameter == null) {
            this.engineLoad(null, null);
        } else if (loadStoreParameter instanceof BCLoadStoreParameter) {
            BCLoadStoreParameter bcParam = (BCLoadStoreParameter)loadStoreParameter;
            this.engineLoad(bcParam.getInputStream(), ParameterUtil.extractPassword(loadStoreParameter));
        } else {
            throw new IllegalArgumentException("no support for 'param' of type " + loadStoreParameter.getClass().getName());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void engineLoad(InputStream stream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (stream == null) {
            return;
        }
        ErasableByteStream storeStream = this.validateStream(stream, password);
        Hashtable<String, BCJKSTrustedCertEntry> hashtable = this.certificateEntries;
        synchronized (hashtable) {
            try {
                block25: {
                    DataInputStream dIn = new DataInputStream(storeStream);
                    int magic = dIn.readInt();
                    int storeVersion = dIn.readInt();
                    if (magic != -17957139) break block25;
                    CertificateFactory certFact = null;
                    Hashtable<String, CertificateFactory> certFactories = null;
                    switch (storeVersion) {
                        case 1: {
                            certFact = this.createCertFactory("X.509");
                            break;
                        }
                        case 2: {
                            certFactories = new Hashtable<String, CertificateFactory>();
                            break;
                        }
                        default: {
                            throw new IllegalStateException("unable to discern store version");
                        }
                    }
                    int numEntries = dIn.readInt();
                    block17: for (int t = 0; t < numEntries; ++t) {
                        int tag = dIn.readInt();
                        switch (tag) {
                            case 1: {
                                throw new IOException(NOT_IMPLEMENTED_MESSAGE);
                            }
                            case 2: {
                                Certificate cert;
                                String alias = dIn.readUTF();
                                Date date = new Date(dIn.readLong());
                                if (storeVersion == 2) {
                                    String certFormat = dIn.readUTF();
                                    if (certFactories.containsKey(certFormat)) {
                                        certFact = (CertificateFactory)certFactories.get(certFormat);
                                    } else {
                                        certFact = this.createCertFactory(certFormat);
                                        certFactories.put(certFormat, certFact);
                                    }
                                }
                                int l = dIn.readInt();
                                byte[] certData = new byte[l];
                                dIn.readFully(certData);
                                ErasableByteStream certStream = new ErasableByteStream(certData, 0, certData.length);
                                try {
                                    cert = certFact.generateCertificate(certStream);
                                    if (certStream.available() != 0) {
                                        throw new IOException("password incorrect or store tampered with");
                                    }
                                }
                                finally {
                                    certStream.erase();
                                }
                                this.certificateEntries.put(alias, new BCJKSTrustedCertEntry(date, cert));
                                continue block17;
                            }
                            default: {
                                throw new IllegalStateException("unable to discern entry type");
                            }
                        }
                    }
                }
                if (storeStream.available() != 0) {
                    throw new IOException("password incorrect or store tampered with");
                }
            }
            finally {
                storeStream.erase();
            }
        }
    }

    private CertificateFactory createCertFactory(String certFormat) throws CertificateException {
        if (this.helper != null) {
            try {
                return this.helper.createCertificateFactory(certFormat);
            }
            catch (NoSuchProviderException e) {
                throw new CertificateException(e.toString());
            }
        }
        return CertificateFactory.getInstance(certFormat);
    }

    private void addPassword(Digest digest, char[] password) throws IOException {
        for (int i = 0; i < password.length; ++i) {
            digest.update((byte)(password[i] >> 8));
            digest.update((byte)password[i]);
        }
        digest.update(Strings.toByteArray("Mighty Aphrodite"), 0, 16);
    }

    private ErasableByteStream validateStream(InputStream inputStream, char[] password) throws IOException {
        Digest checksumCalculator = DigestFactory.getDigest("SHA-1");
        byte[] rawStore = Streams.readAll(inputStream);
        if (password != null) {
            this.addPassword(checksumCalculator, password);
            checksumCalculator.update(rawStore, 0, rawStore.length - checksumCalculator.getDigestSize());
            byte[] checksum = new byte[checksumCalculator.getDigestSize()];
            checksumCalculator.doFinal(checksum, 0);
            byte[] streamChecksum = new byte[checksum.length];
            System.arraycopy(rawStore, rawStore.length - checksum.length, streamChecksum, 0, checksum.length);
            if (!Arrays.constantTimeAreEqual(checksum, streamChecksum)) {
                Arrays.fill(rawStore, (byte)0);
                throw new IOException("password incorrect or store tampered with");
            }
            return new ErasableByteStream(rawStore, 0, rawStore.length - checksum.length);
        }
        return new ErasableByteStream(rawStore, 0, rawStore.length - checksumCalculator.getDigestSize());
    }

    private static final class BCJKSTrustedCertEntry {
        final Date date;
        final Certificate cert;

        public BCJKSTrustedCertEntry(Date date, Certificate cert) {
            this.date = date;
            this.cert = cert;
        }
    }

    private static final class ErasableByteStream
    extends ByteArrayInputStream {
        public ErasableByteStream(byte[] buf, int offSet, int length) {
            super(buf, offSet, length);
        }

        public void erase() {
            Arrays.fill(this.buf, (byte)0);
        }
    }
}

