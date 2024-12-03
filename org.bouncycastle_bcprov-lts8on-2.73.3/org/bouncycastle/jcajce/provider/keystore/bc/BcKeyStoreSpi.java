/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.keystore.bc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.io.DigestInputStream;
import org.bouncycastle.crypto.io.DigestOutputStream;
import org.bouncycastle.crypto.io.MacInputStream;
import org.bouncycastle.crypto.io.MacOutputStream;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.jcajce.io.CipherOutputStream;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.BCKeyStore;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.io.Streams;
import org.bouncycastle.util.io.TeeOutputStream;

public class BcKeyStoreSpi
extends KeyStoreSpi
implements BCKeyStore {
    private static final int STORE_VERSION = 2;
    private static final int STORE_SALT_SIZE = 20;
    private static final String STORE_CIPHER = "PBEWithSHAAndTwofish-CBC";
    private static final int KEY_SALT_SIZE = 20;
    private static final int MIN_ITERATIONS = 1024;
    private static final String KEY_CIPHER = "PBEWithSHAAnd3-KeyTripleDES-CBC";
    static final int NULL = 0;
    static final int CERTIFICATE = 1;
    static final int KEY = 2;
    static final int SECRET = 3;
    static final int SEALED = 4;
    static final int KEY_PRIVATE = 0;
    static final int KEY_PUBLIC = 1;
    static final int KEY_SECRET = 2;
    protected Hashtable table = new Hashtable();
    protected SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    protected int version;
    private final JcaJceHelper helper = new BCJcaJceHelper();

    public BcKeyStoreSpi(int version) {
        this.version = version;
    }

    private void encodeCertificate(Certificate cert, DataOutputStream dOut) throws IOException {
        try {
            byte[] cEnc = cert.getEncoded();
            dOut.writeUTF(cert.getType());
            dOut.writeInt(cEnc.length);
            dOut.write(cEnc);
        }
        catch (CertificateEncodingException ex) {
            throw new IOException(ex.toString());
        }
    }

    private Certificate decodeCertificate(DataInputStream dIn) throws IOException {
        String type = dIn.readUTF();
        byte[] cEnc = new byte[dIn.readInt()];
        dIn.readFully(cEnc);
        try {
            CertificateFactory cFact = this.helper.createCertificateFactory(type);
            ByteArrayInputStream bIn = new ByteArrayInputStream(cEnc);
            return cFact.generateCertificate(bIn);
        }
        catch (NoSuchProviderException ex) {
            throw new IOException(ex.toString());
        }
        catch (CertificateException ex) {
            throw new IOException(ex.toString());
        }
    }

    private void encodeKey(Key key, DataOutputStream dOut) throws IOException {
        byte[] enc = key.getEncoded();
        if (enc == null) {
            throw new IOException("unable to store encoding of protected key");
        }
        if (key instanceof PrivateKey) {
            dOut.write(0);
        } else if (key instanceof PublicKey) {
            dOut.write(1);
        } else {
            dOut.write(2);
        }
        dOut.writeUTF(key.getFormat());
        dOut.writeUTF(key.getAlgorithm());
        dOut.writeInt(enc.length);
        dOut.write(enc);
    }

    private Key decodeKey(DataInputStream dIn) throws IOException {
        EncodedKeySpec spec;
        int keyType = dIn.read();
        String format = dIn.readUTF();
        String algorithm = dIn.readUTF();
        byte[] enc = new byte[dIn.readInt()];
        dIn.readFully(enc);
        if (format.equals("PKCS#8") || format.equals("PKCS8")) {
            spec = new PKCS8EncodedKeySpec(enc);
        } else if (format.equals("X.509") || format.equals("X509")) {
            spec = new X509EncodedKeySpec(enc);
        } else {
            if (format.equals("RAW")) {
                return new SecretKeySpec(enc, algorithm);
            }
            throw new IOException("Key format " + format + " not recognised!");
        }
        try {
            switch (keyType) {
                case 0: {
                    return BouncyCastleProvider.getPrivateKey(PrivateKeyInfo.getInstance(enc));
                }
                case 1: {
                    return BouncyCastleProvider.getPublicKey(SubjectPublicKeyInfo.getInstance(enc));
                }
                case 2: {
                    return this.helper.createSecretKeyFactory(algorithm).generateSecret(spec);
                }
            }
            throw new IOException("Key type " + keyType + " not recognised!");
        }
        catch (Exception e) {
            throw new IOException("Exception creating key: " + e.toString());
        }
    }

    protected Cipher makePBECipher(String algorithm, int mode, char[] password, byte[] salt, int iterationCount) throws IOException {
        try {
            PBEKeySpec pbeSpec = new PBEKeySpec(password);
            SecretKeyFactory keyFact = this.helper.createSecretKeyFactory(algorithm);
            PBEParameterSpec defParams = new PBEParameterSpec(salt, iterationCount);
            Cipher cipher = this.helper.createCipher(algorithm);
            cipher.init(mode, (Key)keyFact.generateSecret(pbeSpec), defParams);
            return cipher;
        }
        catch (Exception e) {
            throw new IOException("Error initialising store of key store: " + e);
        }
    }

    @Override
    public void setRandom(SecureRandom rand) {
        this.random = rand;
    }

    public Enumeration engineAliases() {
        return this.table.keys();
    }

    @Override
    public boolean engineContainsAlias(String alias) {
        return this.table.get(alias) != null;
    }

    @Override
    public void engineDeleteEntry(String alias) throws KeyStoreException {
        Object entry = this.table.get(alias);
        if (entry == null) {
            return;
        }
        this.table.remove(alias);
    }

    @Override
    public Certificate engineGetCertificate(String alias) {
        StoreEntry entry = (StoreEntry)this.table.get(alias);
        if (entry != null) {
            if (entry.getType() == 1) {
                return (Certificate)entry.getObject();
            }
            Certificate[] chain = entry.getCertificateChain();
            if (chain != null) {
                return chain[0];
            }
        }
        return null;
    }

    @Override
    public String engineGetCertificateAlias(Certificate cert) {
        Enumeration e = this.table.elements();
        while (e.hasMoreElements()) {
            Certificate[] chain;
            Certificate c;
            StoreEntry entry = (StoreEntry)e.nextElement();
            if (!(entry.getObject() instanceof Certificate ? (c = (Certificate)entry.getObject()).equals(cert) : (chain = entry.getCertificateChain()) != null && chain[0].equals(cert))) continue;
            return entry.getAlias();
        }
        return null;
    }

    @Override
    public Certificate[] engineGetCertificateChain(String alias) {
        StoreEntry entry = (StoreEntry)this.table.get(alias);
        if (entry != null) {
            return entry.getCertificateChain();
        }
        return null;
    }

    @Override
    public Date engineGetCreationDate(String alias) {
        StoreEntry entry = (StoreEntry)this.table.get(alias);
        if (entry != null) {
            return entry.getDate();
        }
        return null;
    }

    @Override
    public Key engineGetKey(String alias, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        StoreEntry entry = (StoreEntry)this.table.get(alias);
        if (entry == null || entry.getType() == 1) {
            return null;
        }
        return (Key)entry.getObject(password);
    }

    @Override
    public boolean engineIsCertificateEntry(String alias) {
        StoreEntry entry = (StoreEntry)this.table.get(alias);
        return entry != null && entry.getType() == 1;
    }

    @Override
    public boolean engineIsKeyEntry(String alias) {
        StoreEntry entry = (StoreEntry)this.table.get(alias);
        return entry != null && entry.getType() != 1;
    }

    @Override
    public void engineSetCertificateEntry(String alias, Certificate cert) throws KeyStoreException {
        StoreEntry entry = (StoreEntry)this.table.get(alias);
        if (entry != null && entry.getType() != 1) {
            throw new KeyStoreException("key store already has a key entry with alias " + alias);
        }
        this.table.put(alias, new StoreEntry(alias, cert));
    }

    @Override
    public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) throws KeyStoreException {
        this.table.put(alias, new StoreEntry(alias, key, chain));
    }

    @Override
    public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
        if (key instanceof PrivateKey) {
            if (chain == null) {
                throw new KeyStoreException("no certificate chain for private key");
            }
            if (key.getEncoded() == null) {
                this.table.put(alias, new StoreEntry(alias, new Date(), 2, key, chain));
                return;
            }
        }
        try {
            this.table.put(alias, new StoreEntry(alias, key, password, chain));
        }
        catch (Exception e) {
            throw new BCKeyStoreException(e.toString(), e);
        }
    }

    @Override
    public int engineSize() {
        return this.table.size();
    }

    protected void loadStore(InputStream in) throws IOException {
        DataInputStream dIn = new DataInputStream(in);
        int type = dIn.read();
        while (type > 0) {
            String alias = dIn.readUTF();
            Date date = new Date(dIn.readLong());
            int chainLength = dIn.readInt();
            Certificate[] chain = null;
            if (chainLength != 0) {
                chain = new Certificate[chainLength];
                for (int i = 0; i != chainLength; ++i) {
                    chain[i] = this.decodeCertificate(dIn);
                }
            }
            switch (type) {
                case 1: {
                    Certificate cert = this.decodeCertificate(dIn);
                    this.table.put(alias, new StoreEntry(alias, date, 1, cert));
                    break;
                }
                case 2: {
                    Key key = this.decodeKey(dIn);
                    this.table.put(alias, new StoreEntry(alias, date, 2, key, chain));
                    break;
                }
                case 3: 
                case 4: {
                    byte[] b = new byte[dIn.readInt()];
                    dIn.readFully(b);
                    this.table.put(alias, new StoreEntry(alias, date, type, b, chain));
                    break;
                }
                default: {
                    throw new IOException("Unknown object type in store.");
                }
            }
            type = dIn.read();
        }
    }

    protected void saveStore(OutputStream out) throws IOException {
        Enumeration e = this.table.elements();
        DataOutputStream dOut = new DataOutputStream(out);
        block5: while (e.hasMoreElements()) {
            StoreEntry entry = (StoreEntry)e.nextElement();
            dOut.write(entry.getType());
            dOut.writeUTF(entry.getAlias());
            dOut.writeLong(entry.getDate().getTime());
            Certificate[] chain = entry.getCertificateChain();
            if (chain == null) {
                dOut.writeInt(0);
            } else {
                dOut.writeInt(chain.length);
                for (int i = 0; i != chain.length; ++i) {
                    this.encodeCertificate(chain[i], dOut);
                }
            }
            switch (entry.getType()) {
                case 1: {
                    this.encodeCertificate((Certificate)entry.getObject(), dOut);
                    continue block5;
                }
                case 2: {
                    this.encodeKey((Key)entry.getObject(), dOut);
                    continue block5;
                }
                case 3: 
                case 4: {
                    byte[] b = (byte[])entry.getObject();
                    dOut.writeInt(b.length);
                    dOut.write(b);
                    continue block5;
                }
            }
            throw new IOException("Unknown object type in store.");
        }
        dOut.write(0);
    }

    @Override
    public void engineLoad(InputStream stream, char[] password) throws IOException {
        this.table.clear();
        if (stream == null) {
            return;
        }
        DataInputStream dIn = new DataInputStream(stream);
        int version = dIn.readInt();
        if (version != 2 && version != 0 && version != 1) {
            throw new IOException("Wrong version of key store.");
        }
        int saltLength = dIn.readInt();
        if (saltLength <= 0) {
            throw new IOException("Invalid salt detected");
        }
        byte[] salt = new byte[saltLength];
        dIn.readFully(salt);
        int iterationCount = dIn.readInt();
        HMac hMac = new HMac(new SHA1Digest());
        if (password != null && password.length != 0) {
            byte[] passKey = PBEParametersGenerator.PKCS12PasswordToBytes(password);
            PKCS12ParametersGenerator pbeGen = new PKCS12ParametersGenerator(new SHA1Digest());
            pbeGen.init(passKey, salt, iterationCount);
            CipherParameters macParams = version != 2 ? ((PBEParametersGenerator)pbeGen).generateDerivedMacParameters(hMac.getMacSize()) : ((PBEParametersGenerator)pbeGen).generateDerivedMacParameters(hMac.getMacSize() * 8);
            Arrays.fill(passKey, (byte)0);
            hMac.init(macParams);
            MacInputStream mIn = new MacInputStream(dIn, hMac);
            this.loadStore(mIn);
            byte[] mac = new byte[hMac.getMacSize()];
            hMac.doFinal(mac, 0);
            byte[] oldMac = new byte[hMac.getMacSize()];
            dIn.readFully(oldMac);
            if (!Arrays.constantTimeAreEqual(mac, oldMac)) {
                this.table.clear();
                throw new IOException("KeyStore integrity check failed.");
            }
        } else {
            this.loadStore(dIn);
            byte[] oldMac = new byte[hMac.getMacSize()];
            dIn.readFully(oldMac);
        }
    }

    @Override
    public void engineStore(OutputStream stream, char[] password) throws IOException {
        DataOutputStream dOut = new DataOutputStream(stream);
        byte[] salt = new byte[20];
        int iterationCount = 1024 + (this.random.nextInt() & 0x3FF);
        this.random.nextBytes(salt);
        dOut.writeInt(this.version);
        dOut.writeInt(salt.length);
        dOut.write(salt);
        dOut.writeInt(iterationCount);
        HMac hMac = new HMac(new SHA1Digest());
        MacOutputStream mOut = new MacOutputStream(hMac);
        PKCS12ParametersGenerator pbeGen = new PKCS12ParametersGenerator(new SHA1Digest());
        byte[] passKey = PBEParametersGenerator.PKCS12PasswordToBytes(password);
        pbeGen.init(passKey, salt, iterationCount);
        if (this.version < 2) {
            hMac.init(((PBEParametersGenerator)pbeGen).generateDerivedMacParameters(hMac.getMacSize()));
        } else {
            hMac.init(((PBEParametersGenerator)pbeGen).generateDerivedMacParameters(hMac.getMacSize() * 8));
        }
        for (int i = 0; i != passKey.length; ++i) {
            passKey[i] = 0;
        }
        this.saveStore(new TeeOutputStream(dOut, mOut));
        byte[] mac = new byte[hMac.getMacSize()];
        hMac.doFinal(mac, 0);
        dOut.write(mac);
        dOut.close();
    }

    private static class BCKeyStoreException
    extends KeyStoreException {
        private final Exception cause;

        public BCKeyStoreException(String msg, Exception cause) {
            super(msg);
            this.cause = cause;
        }

        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }

    public static class BouncyCastleStore
    extends BcKeyStoreSpi {
        public BouncyCastleStore() {
            super(1);
        }

        @Override
        public void engineLoad(InputStream stream, char[] password) throws IOException {
            this.table.clear();
            if (stream == null) {
                return;
            }
            DataInputStream dIn = new DataInputStream(stream);
            int version = dIn.readInt();
            if (version != 2 && version != 0 && version != 1) {
                throw new IOException("Wrong version of key store.");
            }
            byte[] salt = new byte[dIn.readInt()];
            if (salt.length != 20) {
                throw new IOException("Key store corrupted.");
            }
            dIn.readFully(salt);
            int iterationCount = dIn.readInt();
            if (iterationCount < 0 || iterationCount > 65536) {
                throw new IOException("Key store corrupted.");
            }
            String cipherAlg = version == 0 ? "OldPBEWithSHAAndTwofish-CBC" : BcKeyStoreSpi.STORE_CIPHER;
            Cipher cipher = this.makePBECipher(cipherAlg, 2, password, salt, iterationCount);
            CipherInputStream cIn = new CipherInputStream(dIn, cipher);
            SHA1Digest dig = new SHA1Digest();
            DigestInputStream dgIn = new DigestInputStream(cIn, dig);
            this.loadStore(dgIn);
            byte[] hash = new byte[dig.getDigestSize()];
            dig.doFinal(hash, 0);
            byte[] oldHash = new byte[dig.getDigestSize()];
            Streams.readFully(cIn, oldHash);
            if (!Arrays.constantTimeAreEqual(hash, oldHash)) {
                this.table.clear();
                throw new IOException("KeyStore integrity check failed.");
            }
        }

        @Override
        public void engineStore(OutputStream stream, char[] password) throws IOException {
            DataOutputStream dOut = new DataOutputStream(stream);
            byte[] salt = new byte[20];
            int iterationCount = 1024 + (this.random.nextInt() & 0x3FF);
            this.random.nextBytes(salt);
            dOut.writeInt(this.version);
            dOut.writeInt(salt.length);
            dOut.write(salt);
            dOut.writeInt(iterationCount);
            Cipher cipher = this.makePBECipher(BcKeyStoreSpi.STORE_CIPHER, 1, password, salt, iterationCount);
            CipherOutputStream cOut = new CipherOutputStream(dOut, cipher);
            DigestOutputStream dgOut = new DigestOutputStream(new SHA1Digest());
            this.saveStore(new TeeOutputStream(cOut, dgOut));
            byte[] dig = dgOut.getDigest();
            cOut.write(dig);
            cOut.close();
        }
    }

    public static class Std
    extends BcKeyStoreSpi {
        public Std() {
            super(2);
        }
    }

    private class StoreEntry {
        int type;
        String alias;
        Object obj;
        Certificate[] certChain;
        Date date = new Date();

        StoreEntry(String alias, Certificate obj) {
            this.type = 1;
            this.alias = alias;
            this.obj = obj;
            this.certChain = null;
        }

        StoreEntry(String alias, byte[] obj, Certificate[] certChain) {
            this.type = 3;
            this.alias = alias;
            this.obj = obj;
            this.certChain = certChain;
        }

        StoreEntry(String alias, Key key, char[] password, Certificate[] certChain) throws Exception {
            this.type = 4;
            this.alias = alias;
            this.certChain = certChain;
            byte[] salt = new byte[20];
            BcKeyStoreSpi.this.random.nextBytes(salt);
            int iterationCount = 1024 + (BcKeyStoreSpi.this.random.nextInt() & 0x3FF);
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            DataOutputStream dOut = new DataOutputStream(bOut);
            dOut.writeInt(salt.length);
            dOut.write(salt);
            dOut.writeInt(iterationCount);
            Cipher cipher = BcKeyStoreSpi.this.makePBECipher(BcKeyStoreSpi.KEY_CIPHER, 1, password, salt, iterationCount);
            CipherOutputStream cOut = new CipherOutputStream(dOut, cipher);
            dOut = new DataOutputStream(cOut);
            BcKeyStoreSpi.this.encodeKey(key, dOut);
            dOut.close();
            this.obj = bOut.toByteArray();
        }

        StoreEntry(String alias, Date date, int type, Object obj) {
            this.alias = alias;
            this.date = date;
            this.type = type;
            this.obj = obj;
        }

        StoreEntry(String alias, Date date, int type, Object obj, Certificate[] certChain) {
            this.alias = alias;
            this.date = date;
            this.type = type;
            this.obj = obj;
            this.certChain = certChain;
        }

        int getType() {
            return this.type;
        }

        String getAlias() {
            return this.alias;
        }

        Object getObject() {
            return this.obj;
        }

        Object getObject(char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {
            if ((password == null || password.length == 0) && this.obj instanceof Key) {
                return this.obj;
            }
            if (this.type == 4) {
                ByteArrayInputStream bIn = new ByteArrayInputStream((byte[])this.obj);
                DataInputStream dIn = new DataInputStream(bIn);
                try {
                    byte[] salt = new byte[dIn.readInt()];
                    dIn.readFully(salt);
                    int iterationCount = dIn.readInt();
                    Cipher cipher = BcKeyStoreSpi.this.makePBECipher(BcKeyStoreSpi.KEY_CIPHER, 2, password, salt, iterationCount);
                    CipherInputStream cIn = new CipherInputStream(dIn, cipher);
                    try {
                        return BcKeyStoreSpi.this.decodeKey(new DataInputStream(cIn));
                    }
                    catch (Exception x) {
                        bIn = new ByteArrayInputStream((byte[])this.obj);
                        dIn = new DataInputStream(bIn);
                        salt = new byte[dIn.readInt()];
                        dIn.readFully(salt);
                        iterationCount = dIn.readInt();
                        cipher = BcKeyStoreSpi.this.makePBECipher("BrokenPBEWithSHAAnd3-KeyTripleDES-CBC", 2, password, salt, iterationCount);
                        cIn = new CipherInputStream(dIn, cipher);
                        Key k = null;
                        try {
                            k = BcKeyStoreSpi.this.decodeKey(new DataInputStream(cIn));
                        }
                        catch (Exception y) {
                            bIn = new ByteArrayInputStream((byte[])this.obj);
                            dIn = new DataInputStream(bIn);
                            salt = new byte[dIn.readInt()];
                            dIn.readFully(salt);
                            iterationCount = dIn.readInt();
                            cipher = BcKeyStoreSpi.this.makePBECipher("OldPBEWithSHAAnd3-KeyTripleDES-CBC", 2, password, salt, iterationCount);
                            cIn = new CipherInputStream(dIn, cipher);
                            k = BcKeyStoreSpi.this.decodeKey(new DataInputStream(cIn));
                        }
                        if (k != null) {
                            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                            DataOutputStream dOut = new DataOutputStream(bOut);
                            dOut.writeInt(salt.length);
                            dOut.write(salt);
                            dOut.writeInt(iterationCount);
                            Cipher out = BcKeyStoreSpi.this.makePBECipher(BcKeyStoreSpi.KEY_CIPHER, 1, password, salt, iterationCount);
                            CipherOutputStream cOut = new CipherOutputStream(dOut, out);
                            dOut = new DataOutputStream(cOut);
                            BcKeyStoreSpi.this.encodeKey(k, dOut);
                            dOut.close();
                            this.obj = bOut.toByteArray();
                            return k;
                        }
                        throw new UnrecoverableKeyException("no match");
                    }
                }
                catch (Exception e) {
                    throw new UnrecoverableKeyException("no match");
                }
            }
            throw new RuntimeException("forget something!");
        }

        Certificate[] getCertificateChain() {
            return this.certChain;
        }

        Date getDate() {
            return this.date;
        }
    }

    public static class Version1
    extends BcKeyStoreSpi {
        public Version1() {
            super(1);
            if (!Properties.isOverrideSet("org.bouncycastle.bks.enable_v1")) {
                throw new IllegalStateException("BKS-V1 not enabled");
            }
        }
    }
}

